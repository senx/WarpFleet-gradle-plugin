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

import com.openhtmltopdf.util.XRLog;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import kong.unirest.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Pdf generator.
 */
public class PDFGenerator extends HTMLGenerator {

  /**
   * Instantiates a new Pdf generator.
   */
  public PDFGenerator() {
    XRLog.listRegisteredLoggers().forEach(logger -> XRLog.setLevel(logger, java.util.logging.Level.OFF));
  }

  public List<JSONObject> output(File dest, List<JSONObject> doc) throws IOException {
    List<JSONObject> index = new ArrayList<>();
    List<String> pages = new ArrayList<>();
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
      String fName = d.getString("file").replace(".mc2", ".pdf");
      //   File f = new File(dest.getCanonicalPath() + File.separator + fName);
      JSONObject docObj = d.optJSONObject("doc");
      if (null == docObj) docObj = new JSONObject();

      String title = d.optString(d.getString("name"), docObj.optString("name", ""));
      if (!title.startsWith("@")) {
        title = "@" + title;
      }
      index.add(new JSONObject().put("f", fName).put("title", title));
      pages.add(this.renderer.render(this.parser.parse(this.getMarkdown(docObj, title))));
      //PdfConverterExtension.exportToPdf(f.getCanonicalPath(), this.wrapHTML(this.renderer.render(this.parser.parse(this.getMarkdown(docObj, title))), title), "", OPTIONS);
    }

    String toc = index.stream().map(i -> {
      try {
        return this.generateHTMLIndex(i, dest.getCanonicalPath());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.joining("\n"));
    PdfConverterExtension.exportToPdf(
      dest.getCanonicalPath() + File.separator + "index.pdf",
      this.wrapHTML(
        //"<ul class=\"toc\">" + toc + "</ul>" +
        String.join("\n", pages), "Index"),
      "", OPTIONS);
    return index;
  }
}
