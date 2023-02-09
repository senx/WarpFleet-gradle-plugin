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

package io.warp10.warpfleet.actions;

import io.warp10.warpfleet.doc.generators.AbstractGenerator;
import io.warp10.warpfleet.doc.generators.JSONGenerator;
import io.warp10.warpfleet.utils.Constants;
import io.warp10.warpfleet.utils.Helper;
import io.warp10.warpfleet.utils.Logger;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * The type Generate documentation.
 */
@SuppressWarnings("unused")
public class GenerateDocumentation extends DefaultTask {
  /**
   * The Wf url.
   */
  @Input
  String wfURL;
  /**
   * The Source.
   */
  @Input
  @Optional
  String wfSource;
  /**
   * The Dest.
   */
  @Input
  String wfDest;
  /**
   * The Format.
   */
  @Input
  @Optional
  String wfFormat = "json";
  /**
   * The Wf macro dir.
   */
  @Input
  @Optional
  String wfMacroDir = "json";

  /**
   * Instantiates a new Generate documentation.
   */
  public GenerateDocumentation() {
    this.setDescription("Generates documentation files against a Warp 10 instance thanks to the INFOMODE.");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Generate documentation.
   *
   * @throws IOException the io exception
   */
  @TaskAction
  public void generateDocumentation() throws IOException {
    List<File> filestoProcess = this.getFiles(new File(this.getWfSource()));
    List<JSONObject> fileList = new ArrayList<>();
    List<JSONObject> fileListToProcess = new ArrayList<>();
    filestoProcess.forEach(f -> fileList.add(
      new JSONObject()
        .put("fileObj", f)
        .put("file", f.getAbsolutePath().replace(new File(this.getWfSource()).getAbsolutePath(), "").substring(1))
        .put("dir", this.getWfSource())
    ));
    fileList.forEach(f -> {
      try {
        String macroWS = Files.readString(Path.of(f.getString("fileObj")), StandardCharsets.UTF_8);
        String macro = "@" + f.getString("file").replace(".mc2", "");
        String tpl;
        try {
          tpl = Helper.getFileAsString("infomode.tpl", getClass());
        } catch (IOException | IllegalArgumentException e) {
          Logger.messageError("Error, cannot read infomode.tpl ");
          throw new RuntimeException(e);
        }
        tpl = tpl.replace("${macro}", wrapMacro(macroWS));
        Logger.messageInfo("Generating documentation for " + macro);
        String res = Unirest.post(this.getWfURL())
          .header("Content-Type", "text/plain")
          .body(tpl)
          .asString()
          .ifFailure(Helper::processHTTPError)
          .getBody();
        JSONArray resArr = new JSONArray(res);
        JSONObject doc = resArr.length() > 0 ? resArr.getJSONObject(0) : new JSONObject();
        f.put("result", doc).put("name", macro);
        fileListToProcess.add(f);
      } catch (IOException e) {
        Logger.messageError("Error, cannot read " + f.getString("fileObj"));
        throw new RuntimeException(e);
      }
    });
    AtomicReference<AbstractGenerator> generator = new AtomicReference<>();
    switch (this.getWfFormat()) {
      case "json":
        generator.set(new JSONGenerator());
        break;
      default:
        generator.set(new JSONGenerator());
        break;
    }
    String dest = this.getWfDest();
    if (dest.startsWith(".")) {
      dest = System.getProperty("user.dir") + File.separator + dest;
    }
    Logger.messageInfo("Generating " + this.getWfFormat() + " files into " + new File(dest).getCanonicalPath());
    List<JSONObject> output = generator.get().output(new File(dest), fileListToProcess);

    Logger.messageSusccess(output.size() + " scripts parsed");
  }

  private String wrapMacro(String macroWS) {
    return macroWS + "\n 'macro' STORE <% INFOMODE $macro EVAL %> EVAL 'macro' STORE ";
  }

  private List<File> getFiles(File root) throws IOException {
    try (Stream<Path> stream = Files.find(Paths.get(root.getPath()),
      Integer.MAX_VALUE,
      (filePath, fileAttr) -> fileAttr.isRegularFile() && filePath.toFile().getAbsolutePath().endsWith(".mc2")
    )) {
      return stream.map(Path::toFile).toList();
    }
  }

  /**
   * Gets wf url.
   *
   * @return the wf url
   */
  public String getWfURL() {
    return wfURL;
  }

  /**
   * Sets wf url.
   *
   * @param wfURL the wf url
   */
  @Option(option = "url", description = "Warp 10 url, e.g. http://localhost:8080/api/v0")
  public void setWfURL(String wfURL) {
    this.wfURL = wfURL;
  }

  /**
   * Gets wf source.
   *
   * @return the wf source
   */
  public String getWfSource() {
    return null != wfSource ? wfSource : ".";
  }

  /**
   * Sets wf source.
   *
   * @param wfSource the wf source
   */
  @Option(option = "source", description = "Macros source directory, e.g. /path/to/warp10/macros")
  public void setWfSource(String wfSource) {
    this.wfSource = wfSource;
  }

  /**
   * Gets wf dest.
   *
   * @return the wf dest
   */
  public String getWfDest() {
    return wfDest;
  }

  /**
   * Sets wf dest.
   *
   * @param wfDest the wf dest
   */
  @Option(option = "dest", description = "Output directory, e.g. ./resArr")
  public void setWfDest(String wfDest) {
    this.wfDest = wfDest;
  }

  /**
   * Gets wf format.
   *
   * @return the wf format
   */
  public String getWfFormat() {
    return null != wfFormat ? wfFormat : "json";
  }

  /**
   * Sets wf format.
   *
   * @param wfFormat the wf format
   */
  @Option(option = "format", description = "Output format (json, html, markdown)")
  public void setWfFormat(String wfFormat) {
    this.wfFormat = wfFormat;
  }

  /**
   * Gets wf macro dir.
   *
   * @return the wf macro dir
   */
  public String getWfMacroDir() {
    return null != wfMacroDir ? wfMacroDir : ".";
  }

  /**
   * Sets wf macro dir.
   *
   * @param wfMacroDir the wf macro dir
   */
  @Option(option = "macroDir", description = "Macro sub directory")
  public void setWfMacroDir(String wfMacroDir) {
    this.wfMacroDir = wfMacroDir;
  }
}
