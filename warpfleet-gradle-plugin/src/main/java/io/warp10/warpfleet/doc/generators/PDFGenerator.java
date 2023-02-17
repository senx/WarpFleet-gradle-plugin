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
      String fName = d.getString("file").replace(".mc2", ".pdf");
      JSONObject docObj = d.optJSONObject("doc");
      if (null == docObj) docObj = new JSONObject();

      String title = d.optString(d.getString("name"), docObj.optString("name", ""));
      if (!title.startsWith("@")) {
        title = "@" + title;
      }
      index.add(new JSONObject().put("f", fName).put("title", title));
      pages.add(this.renderer.render(this.parser.parse(this.getMarkdown(docObj, title))));
    }
    PdfConverterExtension.exportToPdf(
      dest.getCanonicalPath() + File.separator + "index.pdf",
      this.wrapHTML(String.join("<div class=\"pagebreak\"></div>", pages), "Index"),
      "", OPTIONS);
    return index;
  }
}
