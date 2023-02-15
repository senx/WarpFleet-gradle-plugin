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

package io.warp10.warpfleet.utils;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * The type Helper.
 */
public class Helper {
  private static final String WF_URL = "https://warpfleet.senx.io/api";

  /**
   * Instantiates a new Helper.
   */
  Helper() {
  }

  /**
   * Gets groups.
   *
   * @return the groups
   */
  public static JSONArray getGroups() {
    return Unirest.get(Helper.WF_URL + "/")
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject()
      .getJSONArray("children");
  }

  /**
   * Gets versions.
   *
   * @param group    the group
   * @param artifact the artifact
   * @return the versions
   */
  public static JSONObject getVersions(String group, String artifact) {
    return Unirest.get(Helper.WF_URL + "/{group}/{artifact}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  /**
   * Gets artifact info.
   *
   * @param pi the pi
   * @return the artifact info
   */
  public static JSONObject getArtifactInfo(PackageInfo pi) {
    return getArtifactInfo(pi.getGroup(), pi.getName(), pi.getVersion());
  }

  /**
   * Gets artifact info.
   *
   * @param group    the group
   * @param artifact the artifact
   * @param version  the version
   * @return the artifact info
   */
  public static JSONObject getArtifactInfo(String group, String artifact, String version) {
    return Unirest.get(Helper.WF_URL + "/{group}/{artifact}/{version}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .routeParam("version", version)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  /**
   * Gets artifacts.
   *
   * @param group the group
   * @return the artifacts
   */
  public static JSONArray getArtifacts(String group) {
    return Unirest.get(Helper.WF_URL + "/{group}")
      .routeParam("group", group)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject()
      .getJSONArray("children");
  }

  /**
   * Gets latest.
   *
   * @param group    the group
   * @param artifact the artifact
   * @return the latest
   */
  public static JSONObject getLatest(String group, String artifact) {
    return Unirest.get(Helper.WF_URL + "/{group}/{artifact}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  /**
   * Exec.
   *
   * @param command the command
   * @throws InterruptedException the interrupted exception
   * @throws IOException          the io exception
   */
  public static void exec(String[] command) throws InterruptedException, IOException {
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec(command);
    new Thread(() -> {
      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String line;
      try {
        while ((line = input.readLine()) != null) {
          Logger.messageInfo(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    new Thread(() -> {
      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
      String line;
      try {
        while ((line = input.readLine()) != null)
          Logger.messageError(Logger.ANSI_RED + line + Logger.ANSI_RESET);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    pr.waitFor();
  }

  /**
   * Process http error.
   *
   * @param response the response
   */
  public static void processHTTPError(HttpResponse<?> response) {
    Logger.messageError("Oh No! Status: " + response.getStatus());
    Logger.messageError(response.getStatusText());
    response.getHeaders()
      .all().stream()
      .filter(h -> h.getName().contains("X-Warp10"))
      .forEach(h -> Logger.messageError(h.getName() + ": " + h.getValue()));
    response.getParsingError().ifPresent(e -> {
      Logger.messageError("Parsing Exception: " + e.getMessage());
      Logger.messageError("Original body: " + e.getOriginalBody());
    });
  }

  /**
   * Path string.
   *
   * @param dirs the dirs
   * @return the string
   */
  public static String path(String... dirs) {
    return String.join(File.separator, dirs);
  }

  /**
   * File path file.
   *
   * @param dirs the dirs
   * @return the file
   */
  public static File filePath(String... dirs) {
    return new File(path(dirs));
  }

  /**
   * Gets macro.
   *
   * @param group     the group
   * @param artifact  the artifact
   * @param version   the version
   * @param macro     the macro
   * @param warp10Dir the warp 10 dir
   * @throws IOException the io exception
   */
  public static void getMacro(String group, String artifact, String version, JSONObject macro, String warp10Dir) throws IOException {
    File dest = filePath(warp10Dir, "lib", macro.getString("path"));
    // Create macro dir
    if (!dest.getParentFile().exists()) {
      if (!dest.getParentFile().mkdirs()) {
        throw new IOException("Cannot write " + dest.getAbsolutePath());
      }
    }
    // Get macro
    String macroContent = Unirest.get(Helper.WF_URL + "/{group}/{artifact}/{version}/{macro}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .routeParam("version", version)
      .routeParam("macro", macro.getString("path"))
      .asString()
      .ifFailure(Helper::processHTTPError)
      .getBody();
    FileUtils.write(dest, macroContent, StandardCharsets.UTF_8);
  }

  /**
   * Gets file as string.
   *
   * @param fileName the file name
   * @param clazz    the clazz
   * @return the file as string
   * @throws IOException              the io exception
   * @throws IllegalArgumentException the illegal argument exception
   */
  public static String getFileAsString(final String fileName, Class<?> clazz) throws IOException, IllegalArgumentException {
    InputStream is = Helper.getFileAsIOStream(fileName, clazz);
    StringBuilder sb = new StringBuilder();
    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
         BufferedReader br = new BufferedReader(isr)) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append('\n');
      }
      is.close();
    }
    return sb.toString();
  }

  /**
   * Gets file as io stream.
   *
   * @param fileName the file name
   * @param clazz    the clazz
   * @return the file as io stream
   */
  public static InputStream getFileAsIOStream(final String fileName, Class<?> clazz) {
    InputStream ioStream = clazz.getClassLoader().getResourceAsStream(fileName);
    if (ioStream == null) {
      throw new IllegalArgumentException(fileName + " is not found");
    }
    return ioStream;
  }
}
