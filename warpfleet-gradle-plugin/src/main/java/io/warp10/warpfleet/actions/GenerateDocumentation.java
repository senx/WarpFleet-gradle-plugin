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
import io.warp10.warpfleet.doc.generators.HTMLGenerator;
import io.warp10.warpfleet.doc.generators.JSONGenerator;
import io.warp10.warpfleet.doc.generators.MarkdownGenerator;
import io.warp10.warpfleet.doc.generators.PDFGenerator;
import io.warp10.warpfleet.utils.Constants;
import io.warp10.warpfleet.utils.Helper;
import io.warp10.warpfleet.utils.Logger;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Generate documentation.
 */
abstract public class GenerateDocumentation extends DefaultTask {
  /**
   * Gets wf url.
   *
   * @return the wf url
   */
  @Input
  @Option(option = "url", description = "Warp 10 url, e.g. http://localhost:8080/api/v0")
  abstract public Property<String> getWfUrl();

  /**
   * Gets wf source.
   *
   * @return the wf source
   */
  @Input
  @Optional
  @Option(option = "source", description = "Macros source directory, e.g. /path/to/warp10/macros")
  abstract public Property<String> getWfSource();

  /**
   * Gets wf dest.
   *
   * @return the wf dest
   */
  @Input
  @Option(option = "dest", description = "Output directory, e.g. ./resArr")
  abstract public Property<String> getWfDest();

  /**
   * Gets wf format.
   *
   * @return the wf format
   */
  @Input
  @Optional
  @Option(option = "format", description = "Output format (json, html, markdown)")
  abstract public Property<String> getWfFormat();

  /**
   * Gets wf macro dir.
   *
   * @return the wf macro dir
   */
  @Input
  @Optional
  @Option(option = "macroDir", description = "Macro sub directory")
  abstract  public Property<String> getWfMacroDir();

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
    List<File> filestoProcess = this.getFiles(new File(this.getWfSource().getOrElse(".")));
    List<JSONObject> fileList = new ArrayList<>();
    List<JSONObject> fileListToProcess = new ArrayList<>();
    filestoProcess.forEach(f -> fileList.add(
      new JSONObject()
        .put("fileObj", f)
        .put("file", f.getAbsolutePath().replace(new File(this.getWfSource().getOrElse(".")).getAbsolutePath(), "").substring(1))
        .put("dir", this.getWfSource())
    ));
    fileList.forEach(f -> {
      try {
        String macroWS = FileUtils.readFileToString(new File(f.getString("fileObj")), StandardCharsets.UTF_8);
        String macro = "@" + f.getString("file").replace(".mc2", "");
        String tpl;
        try {
          tpl = Helper.getFileAsString("infomode.tpl", getClass());
        } catch (IOException | IllegalArgumentException e) {
          Logger.messageError("Error, cannot read infomode.tpl ");
          throw new RuntimeException(e);
        }
        tpl = tpl.replace("{{macro}}", wrapMacro(macroWS));
        Logger.messageInfo("Generating documentation for " + macro);
        String res = Unirest.post(this.getWfUrl().getOrElse("http://localhost:8080/api/v0/exec"))
          .header("Content-Type", "text/plain")
          .body(tpl)
          .asString()
          .ifFailure(Helper::processHTTPError)
          .getBody();
        JSONArray resArr = new JSONArray(res);
        JSONObject doc = resArr.length() > 0 ? resArr.getJSONObject(0) : new JSONObject();
        f.put("doc", doc).put("name", macro);
        fileListToProcess.add(f);
      } catch (IOException e) {
        Logger.messageError("Error, cannot read " + f.getString("fileObj"));
        throw new RuntimeException(e);
      }
    });
    AtomicReference<AbstractGenerator> generator = new AtomicReference<>();
    switch (this.getWfFormat().getOrElse("json")) {
      case "md":
      case "markdown":
        generator.set(new MarkdownGenerator());
        break;
      case "html":
        generator.set(new HTMLGenerator());
        break;
      case "pdf":
        generator.set(new PDFGenerator());
        break;
      case "json":
      default:
        generator.set(new JSONGenerator());
        break;
    }
    String dest = this.getWfDest().getOrElse(".");
    if (dest.startsWith(".")) {
      dest = System.getProperty("user.dir") + File.separator + dest;
    }
    Logger.messageInfo("Generating " + this.getWfFormat().getOrElse("json") + " files into " + new File(dest).getCanonicalPath());
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
      return stream.map(Path::toFile).collect(Collectors.toList());
    }
  }
}
