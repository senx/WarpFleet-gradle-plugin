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

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Markdown generator.
 */
public class MarkdownGenerator extends AbstractGenerator {

  /**
   * Instantiates a new Markdown generator.
   */
  public MarkdownGenerator() {
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
      String fName = d.getString("file").replace(".mc2", ".md");
      File f = new File(dest.getCanonicalPath() + File.separator + fName);
      JSONObject docObj = d.optJSONObject("doc");
      if (null == docObj) docObj = new JSONObject();

      String title = d.optString(d.getString("name"), docObj.optString("name", ""));
      if (!title.startsWith("@")) {
        title = "@" + title;
      }
      index.add(new JSONObject().put("f", fName).put("title", title));
      String md = "# `" + title + "`\n\n" +
        docObj.optString("desc", "") + "\n\n" +
        this.generateRelated(docObj.optJSONArray("related"))  +
        this.generateMarkdownSignatures(this.generateSignature(docObj), title) +
        this.generateMarkdownSamples(docObj);
      FileUtils.write(f, md, StandardCharsets.UTF_8);
    }
    return index;
  }

  private String generateMarkdownSamples(JSONObject doc) {
    StringBuilder md = new StringBuilder().append("## Samples\n\n");
    JSONArray examples = doc.optJSONArray("examples");
    if (null == examples) examples = new JSONArray();
    examples.forEach(e -> md.append("````warpscript\n").append(e).append("\n````\n\n"));
    return md.toString();
  }

  private String formatParam(JSONObject s) {
    return AbstractGenerator.sanitize(s.getString("name")) +
      "&lt;" +
      AbstractGenerator.sanitize(s.getString("type")) +
      "&gt;";
  }

  private String generateMarkdownSignatures(JSONObject sig, String fnName) {
    JSONObject input = sig.getJSONObject("input");
    JSONObject output = sig.getJSONObject("output");
    JSONArray signatures = sig.optJSONArray("signatures");
    if (null == signatures) signatures = new JSONArray();

    StringBuilder md = new StringBuilder().append("## Signatures");
    signatures.forEach(sign -> {
      JSONObject signature = (JSONObject) sign;
      md.append("- ");
      signature.getJSONArray("in").forEach(in -> {
        JSONObject item = (JSONObject) in;
        if (item.getBoolean("isArray") && !item.getBoolean("isObject")) {
          md.append("[ ");
          @SuppressWarnings("unchecked")
          List<JSONObject> sigItems = item.getJSONArray("sigItems").toList();
          Collections.reverse(sigItems);
          sigItems.forEach(s -> md.append(this.formatParam(s)).append(" "));
          md.append("] ");
        }

        if (!item.getBoolean("isArray") && item.getBoolean("isObject")) {
          md.append("{ ");
          @SuppressWarnings("unchecked")
          List<JSONObject> sigItems = item.getJSONArray("sigItems").toList();
          Collections.reverse(sigItems);
          sigItems.forEach(s -> md.append(this.formatParam(s)).append(" "));
          md.append("} ");
        }

        if (!item.getBoolean("isArray") && !item.getBoolean("isObject")) {
          md.append(this.formatParam(item.optJSONObject("sigDesc"))).append(" ");
        }
      });
      md.append("`").append(fnName).append("` ");
      signature.getJSONArray("out").forEach(out -> {
        JSONObject item = (JSONObject) out;
        if (item.getBoolean("isArray") && !item.getBoolean("isObject")) {
          md.append("[ ");
          @SuppressWarnings("unchecked")
          List<JSONObject> sigItems = item.getJSONArray("sigItems").toList();
          Collections.reverse(sigItems);
          sigItems.forEach(s -> md.append(this.formatParam(s)).append(" "));
          md.append("] ");
        }

        if (!item.getBoolean("isArray") && item.getBoolean("isObject")) {
          md.append("{ ");
          @SuppressWarnings("unchecked")
          List<JSONObject> sigItems = item.getJSONArray("sigItems").toList();
          Collections.reverse(sigItems);
          sigItems.forEach(s -> md.append(this.formatParam(s)).append(" "));
          md.append("} ");
        }

        if (!item.getBoolean("isArray") && !item.getBoolean("isObject")) {
          md.append(this.formatParam(item.optJSONObject("sigDesc"))).append(" ");
        }

      });
      md.append('\n');
    });
    md.append("\n\n| Name | Type | Description |\n| --- | --- | --- |\n");
    input.keySet().forEach(k -> {
      JSONObject it = input.getJSONObject(k);
      md.append("| ").append(it.getString("name"))
        .append(" | ").append(it.getString("type"))
        .append(" | ").append(it.optString("desc", "").replaceAll("\n", "<br />"))
        .append(" |\n");
    });
    output.keySet().forEach(k -> {
      JSONObject it = output.getJSONObject(k);
      md.append("| ").append(it.getString("name"))
        .append(" | ").append(it.getString("type"))
        .append(" | ").append(it.optString("desc", "").replaceAll("\n", "<br />"))
        .append(" |\n");
    });
    md.append('\n');
    return md.toString();
  }

  private String generateRelated(JSONArray related) {
    if (null == related) return "";
    StringBuilder md = new StringBuilder().append("## See also\n\n");
    related.forEach(r -> {
      JSONObject rel = (JSONObject) r;
      md
        .append("- [./")
        .append(rel.optString("b64", ""))
        .append(".md](")
        .append(rel.optString("label", ""))
        .append(")\n\n");
    });
    return null;
  }
}
