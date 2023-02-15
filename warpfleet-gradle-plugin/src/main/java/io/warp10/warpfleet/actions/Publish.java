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
import kong.unirest.Unirest;
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
import java.util.ArrayList;
import java.util.List;

/**
 * The type Publish.
 */
abstract public class Publish extends DefaultTask {
  /**
   * Gets repo url.
   *
   * @return the repo url
   */
  @Input
  @Optional
  @Option(option = "repoUrl", description = "Maven repository URL")
  abstract public Property<String> getWFRepoUrl();

  /**
   * Gets vers.
   *
   * @return the vers
   */
  @Input
  @Optional
  @Option(option = "vers", description = "Artifact version to publish")
  abstract public Property<String> getWfVersion();

  /**
   * Gets gpg key id.
   *
   * @return the gpg key id
   */
  @Input
  @Optional
  @Option(option = "gpgKeyId", description = "GPG Key Id")
  abstract public Property<String> getWFGpgKeyId();

  /**
   * Gets gpg arg.
   *
   * @return the gpg arg
   */
  @Input
  @Optional
  @Option(option = "gpgArg", description = "GPG gpgArg")
  abstract public Property<String> getWFGpgArg();

  /**
   * Gets wf json.
   *
   * @return the wf json
   */
  @Input
  @Option(option = "wfJson", description = "Path to wf.json")
  abstract public Property<String> getWFJson();

  /**
   * Instantiates a new Publish.
   */
  public Publish() {
    this.setDescription("Publishes a plugin, macro or extension against WarpFleet");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Publish artifact.
   *
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  @TaskAction
  public void publishArtifact() throws IOException, InterruptedException {
    // Checks
    File wfJson = new File(this.getWFJson().get());
    if (!wfJson.exists()) {
      throw new RuntimeException("Cannot reach wf.json");
    }
    JSONObject conf = new JSONObject(FileUtils.readFileToString(wfJson, StandardCharsets.UTF_8));
    conf.put("ts", System.currentTimeMillis());
    if (null != this.getWFRepoUrl().getOrNull()) {
      conf.put("repoUrl", this.getWFRepoUrl().get());
    }
    if (null != this.getWfVersion().getOrNull()) {
      conf.put("version", this.getWfVersion().get());
    }

    Logger.messageInfo("About to publish: " +
      conf.getString("group") + ":" +
      conf.getString("artifact") + ":" +
      conf.getString("version")
    );

    List<String> missingFiles = new ArrayList<>();
    if (!new File(wfJson.getParentFile().getCanonicalPath() + File.separator  + "README.md").exists()) {
      missingFiles.add("README.md");
    }

    if (!missingFiles.isEmpty()) {
      Logger.messageError("Some mandatory files ar missing: " + String.join(", ", missingFiles));
      throw new RuntimeException("Missing mandatory files");
    }

    // GPG signature
    File tmpConf = new File(wfJson.getCanonicalPath() + ".tmp");
    FileUtils.write(tmpConf, conf.toString(2), StandardCharsets.UTF_8);
    Helper.signArtefact(tmpConf, this.getWFGpgKeyId().getOrNull(), this.getWFGpgArg().getOrNull());

    // Publication
    Logger.messageInfo("Publishing");
    JSONObject result = Unirest.post(Helper.WF_URL + "/data/publish")
      .field("sig", new File(tmpConf.getCanonicalPath() + ".gpg"))
      .field("meta", conf.toString(2))
      .asJson().ifFailure(Helper::processHTTPError)
      .getBody().getObject();
    FileUtils.delete(new File(tmpConf.getCanonicalPath() + ".gpg"));
    if (!result.optBoolean("status", false)) {
      Logger.messageError("An error Occurs");
      if (result.has("message")) {
        Logger.messageError(result.getString("message"));
        throw new RuntimeException(result.getString("message"));
      }
    } else {
      Logger.messageSusccess(
        conf.getString("group") + ":" +
          conf.getString("artifact") + ":" +
          conf.getString("version") + " Published"
      );
    }
  }
}
