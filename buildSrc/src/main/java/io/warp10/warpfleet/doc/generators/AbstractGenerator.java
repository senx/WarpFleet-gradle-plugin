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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Abstract generator.
 */
public abstract class AbstractGenerator {

  /**
   * Instantiates a new Abstract generator.
   */
  AbstractGenerator() {

  }

  /**
   * Output list.
   *
   * @param dest the dest
   * @param doc  the doc
   * @return the list
   * @throws IOException the io exception
   */
  public abstract List<JSONObject> output(File dest, List<JSONObject> doc) throws IOException;

  /**
   * Generate signature json object.
   *
   * @param doc the doc
   * @return the json object
   */
  protected JSONObject generateSignature(JSONObject doc) {
    JSONObject input = new JSONObject();
    JSONObject output = new JSONObject();
    List<JSONObject> signatures = new ArrayList<>();
    if (!doc.has("params")) {
      doc.put("params", new JSONObject());
    }
    if (!doc.has("sig")) {
      doc.put("sig", new JSONArray());
    }
    doc.getJSONArray("sig").forEach(sig -> {
      JSONObject signature = new JSONObject()
        .put("in", new JSONArray())
        .put("out", new JSONArray());
      // in
      ((JSONArray) sig).getJSONArray(0).forEach(item -> {
        if (item instanceof JSONArray) {
          JSONArray sigItems = new JSONArray();
          ((JSONArray) item).forEach(s -> {
            String[] it = ((String) s).split(":");
            JSONObject sigDesc = new JSONObject()
              .put("type", it[1])
              .put("name", it[0])
              .put("desc", doc.getJSONObject("params").getString(it[0]));
            sigItems.put(sigDesc);
            input.put(it[0], sigDesc);
          });
          signature.getJSONArray("in")
            .put(new JSONObject()
              .put("isArray", true)
              .put("isObject", false)
              .put("sigItems", sigItems)
            );
        } else if (item instanceof JSONObject) {
          JSONArray sigItems = new JSONArray();
          ((JSONObject) item).keySet().forEach(k -> {
            String[] it = ((JSONObject) item).getString(k).split(":");
            JSONObject sigDesc = new JSONObject()
              .put("type", it[1])
              .put("name", it[0])
              .put("desc", doc.getJSONObject("params").getString(it[0]));
            sigItems.put(sigDesc);
            input.put(it[0], sigDesc);
          });
          signature.getJSONArray("in")
            .put(new JSONObject()
              .put("isArray", false)
              .put("isObject", true)
              .put("sigItems", sigItems)
            );

        } else {
          String[] it = ((String) item).split(":");
          JSONObject sigDesc = new JSONObject()
            .put("type", it[1])
            .put("name", it[0])
            .put("desc", doc.getJSONObject("params").getString(it[0]));
          input.put(it[0], sigDesc);
          signature.getJSONArray("in")
            .put(new JSONObject()
              .put("isArray", false)
              .put("isObject", false)
              .put("sigItems", sigDesc)
            );
        }
      });
      // out
      ((JSONArray) sig).getJSONArray(1).forEach(item -> {
        if (item instanceof JSONArray) {
          JSONArray sigItems = new JSONArray();
          ((JSONArray) item).forEach(s -> {
            String[] it = ((String) s).split(":");
            JSONObject sigDesc = new JSONObject()
              .put("type", it[1])
              .put("name", it[0])
              .put("desc", doc.getJSONObject("params").getString(it[0]));
            sigItems.put(sigDesc);
            input.put(it[0], sigDesc);
          });
          signature.getJSONArray("out")
            .put(new JSONObject()
              .put("isArray", true)
              .put("isObject", false)
              .put("sigItems", sigItems)
            );
        } else if (item instanceof JSONObject) {
          JSONArray sigItems = new JSONArray();
          ((JSONObject) item).keySet().forEach(k -> {
            String[] it = ((JSONObject) item).getString(k).split(":");
            JSONObject sigDesc = new JSONObject()
              .put("type", it[1])
              .put("name", it[0])
              .put("desc", doc.getJSONObject("params").getString(it[0]));
            sigItems.put(sigDesc);
            input.put(it[0], sigDesc);
          });
          signature.getJSONArray("out")
            .put(new JSONObject()
              .put("isArray", false)
              .put("isObject", true)
              .put("sigItems", sigItems)
            );
        } else {
          String[] it = ((String) item).split(":");
          JSONObject sigDesc = new JSONObject()
            .put("type", it[1])
            .put("name", it[0])
            .put("desc", doc.getJSONObject("params").getString(it[0]));
          input.put(it[0], sigDesc);
          signature.getJSONArray("out")
            .put(new JSONObject()
              .put("isArray", false)
              .put("isObject", false)
              .put("sigItems", sigDesc)
            );
        }
      });
    });
    return new JSONObject()
      .put("input", input)
      .put(" output", output)
      .put("signatures", signatures);
  }
}
