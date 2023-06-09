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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Helper {
  public static final String WF_URL = "https://warpfleet.senx.io";

  Helper() {
  }

  public static JSONArray getGroups() {
    return Unirest.get(Helper.WF_URL + "/api/")
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject()
      .getJSONArray("children");
  }

  public static JSONObject getVersions(String group, String artifact) {
    return Unirest.get(Helper.WF_URL + "/api/{group}/{artifact}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  public static JSONObject getArtifactInfo(PackageInfo pi) {
    return getArtifactInfo(pi.getGroup(), pi.getName(), pi.getVersion());
  }

  public static JSONObject getArtifactInfo(String group, String artifact, String version) {
    return Unirest.get(Helper.WF_URL + "/api/{group}/{artifact}/{version}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .routeParam("version", version)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  public static JSONArray getArtifacts(String group) {
    return Unirest.get(Helper.WF_URL + "/api/{group}")
      .routeParam("group", group)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject()
      .getJSONArray("children");
  }

  public static JSONObject getLatest(String group, String artifact) {
    return Unirest.get(Helper.WF_URL + "/api/{group}/{artifact}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .header("accept", "application/json")
      .asJson()
      .ifFailure(Helper::processHTTPError)
      .getBody()
      .getObject();
  }

  public static void execCmd(File workDir, String[] cmd) throws IOException, InterruptedException {
    Helper.execCmd(workDir, Arrays.asList(cmd));
  }

  public static void execCmd(File workDir, String cmd) throws IOException, InterruptedException {
    System.out.println(cmd);
    Helper.execCmd(workDir, cmd.split(" "));
  }

  public static void execCmd(File workDir, List<String> cmd) throws IOException, InterruptedException {
    final ProcessBuilder p = new ProcessBuilder(cmd);
    if(null != workDir) {
      p.directory(workDir);
    }
    p.redirectInput(ProcessBuilder.Redirect.PIPE);
    p.redirectOutput(ProcessBuilder.Redirect.PIPE);
    p.redirectError(ProcessBuilder.Redirect.PIPE);

    Process process;
    try {
      process = p.start();
    } catch (final IOException ex) {
      throw new RuntimeException("Failed to run command: " + cmd.get(0), ex);
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        Logger.messageInfo(line);
      }
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        Logger.messageError(Logger.ANSI_RED + line + Logger.ANSI_RESET);
      }
    }
    process.waitFor();
    if (process.exitValue() != 0) {
      throw new RuntimeException("Failed to run command: " + cmd.get(0));
    }

  }

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
    throw new RuntimeException("An HTTP error occurs: " + response.getStatus() + " " + response.getStatusText());
  }

  public static String path(String... dirs) {
    return String.join(File.separator, dirs);
  }

  public static File filePath(String... dirs) {
    return new File(path(dirs));
  }

  public static void getMacro(String group, String artifact, String version, JSONObject macro, String warp10Dir) throws IOException {
    File dest = filePath(warp10Dir, "lib", macro.getString("path"));
    // Create macro dir
    if (!dest.getParentFile().exists()) {
      if (!dest.getParentFile().mkdirs()) {
        throw new IOException("Cannot write " + dest.getAbsolutePath());
      }
    }
    // Get macro
    String macroContent = Unirest.get(Helper.WF_URL + "/api/{group}/{artifact}/{version}/{macro}")
      .routeParam("group", group)
      .routeParam("artifact", artifact)
      .routeParam("version", version)
      .routeParam("macro", macro.getString("path"))
      .asString()
      .ifFailure(Helper::processHTTPError)
      .getBody();
    FileUtils.write(dest, macroContent, StandardCharsets.UTF_8);
  }

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

  public static InputStream getFileAsIOStream(final String fileName, Class<?> clazz) {
    InputStream ioStream = clazz.getClassLoader().getResourceAsStream(fileName);
    if (ioStream == null) {
      throw new IllegalArgumentException(fileName + " is not found");
    }
    return ioStream;
  }

  public static Map<String, String> getParamsMap(String... data) {
    Map<String, String> result = new HashMap<>();

    if (data.length % 2 != 0) {
      throw new IllegalArgumentException("Odd number of arguments");
    }

    String key = null;
    int step = -1;

    for (String value : data) {
      step++;
      switch (step % 2) {
        case 0:
          if (value == null)
            throw new IllegalArgumentException("Null key value");
          key = value;
          continue;
        case 1:
          result.put(key, value);
          break;
      }
    }
    return result;
  }


  public static void signArtefact(File f, String gpgKeyId, String gpgArg) throws IOException, InterruptedException {
    Logger.messageInfo("Signing artifact");
    List<String> gpgArgs = new ArrayList<>();
    gpgArgs.add("gpg");
    gpgArgs.add("--sign");
    gpgArgs.add("--yes");
    if (null != gpgArg) {
      Collections.addAll(gpgArgs, gpgArg.split(" "));
    }
    if (null != gpgKeyId) {
      gpgArgs.add("--default-key");
      gpgArgs.add(gpgKeyId);
    }
    gpgArgs.add(f.getCanonicalPath());
    gpgArgs = gpgArgs.stream().map(String::trim).collect(Collectors.toList());
    Helper.execCmd(null, gpgArgs);
    FileUtils.delete(f);
    Logger.messageSusccess("Artifact signed");
  }
}
