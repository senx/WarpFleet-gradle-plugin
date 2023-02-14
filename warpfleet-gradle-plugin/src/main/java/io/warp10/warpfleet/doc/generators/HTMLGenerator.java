/*
 *   Copyright 2022  SenX S.A.S.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.warp10.warpfleet.doc.generators;

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.warp10.warpfleet.utils.Helper;
import io.warp10.warpfleet.utils.Logger;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The type Html generator.
 */
public class HTMLGenerator extends MarkdownGenerator {
  /**
   * The constant OPTIONS.
   */
  protected final static DataHolder OPTIONS = new MutableDataSet()
    .set(HtmlRenderer.INDENT_SIZE, 2)
    .set(Parser.HTML_BLOCK_DEEP_PARSER, true)
    .set(Parser.HTML_BLOCK_DEEP_PARSE_BLANK_LINE_INTERRUPTS, false)
    .set(Parser.HTML_BLOCK_DEEP_PARSE_FIRST_OPEN_TAG_ON_ONE_LINE, true)
    .set(Parser.HTML_BLOCK_DEEP_PARSE_BLANK_LINE_INTERRUPTS_PARTIAL_TAG, false)
    .set(TablesExtension.COLUMN_SPANS, false)
    .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
    .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
    .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
    .set(WikiLinkExtension.IMAGE_LINKS, true)
    .set(Parser.EXTENSIONS,
      Arrays.asList(
        TablesExtension.create(),
        AttributesExtension.create(),
        AutolinkExtension.create(),
        GitLabExtension.create(),
        TypographicExtension.create()
      )
    );
  /**
   * The Parser.
   */
  protected final Parser parser = Parser.builder(OPTIONS).build();
  /**
   * The Renderer.
   */
  protected final HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();

  /**
   * Instantiates a new Html generator.
   */
  public HTMLGenerator() {
  }

  public List<JSONObject> output(File dest, List<JSONObject> doc) throws IOException {
    List<JSONObject> index = new ArrayList<>();
    if (!dest.exists()) {
      if (!dest.mkdirs()) {
        throw new IOException("Cannot create " + dest.getCanonicalPath());
      }
    }
    for (JSONObject d : doc) {
      File folder = new File(dest.getCanonicalPath() + File.separator + d.getString("file")).getParentFile();
      if (!folder.exists()) {
        if (!folder.mkdirs()) {
          throw new IOException("Cannot create " + folder.getCanonicalPath());
        }
      }
      String fName = d.getString("file").replace(".mc2", ".html");
      File f = new File(dest.getCanonicalPath() + File.separator + fName);
      JSONObject docObj = d.optJSONObject("doc");
      if (null == docObj) docObj = new JSONObject();

      String title = d.optString(d.getString("name"), docObj.optString("name", ""));
      if (!title.startsWith("@")) {
        title = "@" + title;
      }
      index.add(new JSONObject().put("f", fName).put("title", title));
      FileUtils.write(f, this.wrapHTML(this.renderer.render(this.parser.parse(this.getMarkdown(docObj, title))), title), StandardCharsets.UTF_8);
    }

    String toc = index.stream().map(i -> {
      try {
        return this.generateHTMLIndex(i, dest.getCanonicalPath());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.joining("\n"));
    FileUtils.write(new File(dest.getCanonicalPath() + File.separator + "index.html"), this.wrapHTML("<ul>" + toc + "</ul>", "Index"), StandardCharsets.UTF_8);
    return index;
  }

  /**
   * Generate html index string.
   *
   * @param i    the
   * @param dest the dest
   * @return the string
   */
  protected String generateHTMLIndex(JSONObject i, String dest) {
    return "<li><a href=\"./" + i.optString("f", "").replace(dest, "") + "\">" +
      i.optString("title", "") +
      "</a></li>";
  }

  /**
   * Wrap html string.
   *
   * @param html  the html
   * @param title the title
   * @return the string
   */
  protected String wrapHTML(String html, String title) {
    String tpl;
    try {
      tpl = Helper.getFileAsString("html.tpl", getClass());
    } catch (IOException | IllegalArgumentException e) {
      Logger.messageError("Error, cannot read html.tpl ");
      throw new RuntimeException(e);
    }
    return tpl
      .replaceAll(Pattern.quote("{{title}}"), Matcher.quoteReplacement(title))
      .replaceAll(Pattern.quote("{{content}}"), Matcher.quoteReplacement(html));
  }
}
