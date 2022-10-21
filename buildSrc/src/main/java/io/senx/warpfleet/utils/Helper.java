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

package io.senx.warpfleet.utils;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The type Helper.
 */
@SuppressWarnings("unused")
public class Helper {
  private static final String WF_URL = "https://warpfleet.senx.io/api";

  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_BLACK = "\u001B[30m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_WHITE = "\u001B[37m";
  private static final String ANSI_CHECK = "\u2714";
  private static final String ANSI_CROSS = "\u2716";
  private static final String ANSI_INFO = "\u25CB";
  private static final String ANSI_WARN = "/!\\";

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
  public static void exec(String command) throws InterruptedException, IOException {
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec(command);
    new Thread(() -> {
      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String line;
      try {
        while ((line = input.readLine()) != null)
          messageInfo(line);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
    new Thread(() -> {
      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
      String line;
      try {
        while ((line = input.readLine()) != null)
          messageError(ANSI_RED + line + ANSI_RESET);
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
    messageError("Oh No! Status" + response.getStatus());
    response.getParsingError().ifPresent(e -> {
      messageError("Parsing Exception: " + e.getMessage());
      messageError("Original body: " + e.getOriginalBody());
    });
  }

  /**
   * Message info.
   *
   * @param message the message
   */
  public static void messageInfo(String message) {
    System.out.println(ANSI_CYAN + ANSI_INFO + " " + message + ANSI_RESET);
  }

  /**
   * Message error.
   *
   * @param message the message
   */
  public static void messageError(String message) {
    System.out.println(ANSI_RED + ANSI_CROSS + " " + message + ANSI_RESET);
  }

  /**
   * Message susccess.
   *
   * @param message the message
   */
  public static void messageSusccess(String message) {
    System.out.println(ANSI_GREEN + ANSI_CHECK + " " + message + ANSI_RESET);
  }

  /**
   * Message warning.
   *
   * @param message the message
   */
  public static void messageWarning(String message) {
    System.out.println(ANSI_YELLOW + ANSI_WARN + " " + message + ANSI_RESET);
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
}
