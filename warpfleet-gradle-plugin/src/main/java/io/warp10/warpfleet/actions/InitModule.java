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

import io.warp10.warpfleet.utils.Constants;
import io.warp10.warpfleet.utils.Helper;
import io.warp10.warpfleet.utils.Logger;
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

/**
 * The type Init module.
 */
abstract public class InitModule extends DefaultTask {
  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  @Input
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  abstract public Property<String> getWfGroup();

  /**
   * Gets wf artifact.
   *
   * @return the wf artifact
   */
  @Input
  @Option(option = "artifact", description = "Artifact's name, ie: warp10-plugin-mqtt")
  abstract public Property<String> getWfArtifact();

  /**
   * Gets wf version.
   *
   * @return the wf version
   */
  @Input
  @Optional
  @Option(option = "vers", description = "Artifact's version, ie: 0.0.3")
  abstract public Property<String> getWfVersion();

  /**
   * Gets wf type.
   *
   * @return the wf type
   */
  @Input
  @Option(option = "type", description = "Artifact's type [ext|plugin|macro]")
  abstract public Property<String> getWfType();

  /**
   * Gets wf dest.
   *
   * @return the wf dest
   */
  @Input
  @Optional
  @Option(option = "dest", description = "Project parent's folder")
  abstract public Property<String> getWfDest();

  /**
   * Instantiates a new Init module.
   */
  public InitModule() {
    this.setDescription("Init module dev");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Init module.
   *
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  @TaskAction
  public void initModule() throws IOException, InterruptedException {
    Logger.messageInfo("About to generate " + this.getWfGroup().get() + ":" + this.getWfArtifact().get() + ":" + this.getWfVersion().getOrElse("0.0.1"));
    Logger.messageInfo("Retrieving starter kit");
    File dest = Helper.filePath(this.getWfDest().getOrElse("."), this.getWfArtifact().get());
    File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + ".git");
    if(tmp.exists()) {
      FileUtils.deleteDirectory(tmp);
    }
    Helper.execCmd(dest.getParentFile(), "git clone -q --depth=1 --separate-git-dir=" + tmp.getAbsolutePath() + " https://github.com/senx/warp10-module-template.git " + dest.getCanonicalPath());
    FileUtils.delete(Helper.filePath(dest.getAbsolutePath() + File.separator + ".git"));
    Logger.messageInfo("Generating artifacts");
    String cmd = "./gradlew -q bootstrap" +
      " -Pg=" + this.getWfGroup().get() +
      " -Pa=" + this.getWfArtifact().get() +
      " -Pv=" + this.getWfVersion().getOrElse("0.0.1") +
      " -Pt=" + this.getWfType().get() +
      " -Pd=\"\"";

    Helper.execCmd(dest, cmd);

    FileUtils.write(Helper.filePath(dest.getAbsolutePath(), "README.md"),
      "# " + this.getWfGroup().get() + ":" + this.getWfArtifact().get(),
      StandardCharsets.UTF_8);

    JSONObject wf = new JSONObject()
      .put("group", this.getWfGroup().get())
      .put("artifact", this.getWfArtifact().get())
      .put("type", this.getWfType().get())
      .put("classifier", "")
      .put("version", this.getWfVersion().getOrElse("0.0.1"))
      .put("description", "")
      .put("license", "")
      .put("git", "")
      .put("author", "")
      .put("email", "")
      .put("repoUrl", "")
      .put("tags", new JSONArray())
      .put("conf", new JSONArray())
      .put("macros", new JSONArray())
      .put("dependencies", new JSONArray());

    FileUtils.write(Helper.filePath(dest.getAbsolutePath(), "wf.json"), wf.toString(2), StandardCharsets.UTF_8);
    Logger.messageSusccess("Artifact generated, happy coding");


  }
}
