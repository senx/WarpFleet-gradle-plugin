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
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Json generator.
 */
public class JSONGenerator extends AbstractGenerator {

  /**
   * Instantiates a new Json generator.
   */
  public JSONGenerator() {
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
      String fName = d.getString("file").replace(".mc2", ".json");
      File f = new File(dest.getCanonicalPath() + File.separator + fName);
      String title = d.optString(d.getString("name"), "");
      JSONObject docObj = d.optJSONObject("doc");
      if (null != docObj) {
        title = docObj.optString("name", "");
      }
      if (!title.startsWith("@")) {
        title = "@" + title;
      }
      index.add(new JSONObject().put("f", fName).put("title", title));
      FileUtils.write(f, d.toString(2), StandardCharsets.UTF_8);
    }
    JSONArray toc = new JSONArray();
    for (JSONObject i : index) {
        toc.put(new JSONObject()
          .put("link", i.optString("f", "").replace(dest.getCanonicalPath(), ""))
          .put("title", i.optString("title", "")));
    }
    FileUtils.write(new File(dest.getCanonicalPath() + File.separator + "index.json"), toc.toString(2), StandardCharsets.UTF_8);
    return index;
  }
}
