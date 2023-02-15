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
import org.gradle.api.internal.tasks.userinput.UserInputHandler;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The type Un publish.
 */
abstract public class UnPublish extends DefaultTask {
  /**
   * The User input.
   */
  UserInputHandler userInput = getServices().get(UserInputHandler.class);

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
  @Option(option = "vers", description = "Artifact version to unpublish")
  abstract public Property<String> getWFVersion();

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

  @Input
  @Optional
  @Option(option = "force", description = "Force unpublish")
  abstract public Property<Boolean> getWFForce();

  /**
   * Instantiates a new Un publish.
   */
  public UnPublish() {
    this.setDescription("Unpublishes a plugin, macro or extension against WarpFleet");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Un publish artifact.
   *
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  @TaskAction
  public void unPublishArtifact() throws IOException, InterruptedException {
    File wfJson = new File(this.getWFJson().get());
    if (!wfJson.exists()) {
      throw new RuntimeException("Cannot reach wf.json");
    }
    JSONObject conf = new JSONObject(FileUtils.readFileToString(wfJson, StandardCharsets.UTF_8));
    conf.put("ts", System.currentTimeMillis());
    if (null != this.getWFRepoUrl().getOrNull()) {
      conf.put("repoUrl", this.getWFRepoUrl().get());
    }
    if (null != this.getWFVersion().getOrNull()) {
      conf.put("version", this.getWFVersion().get());
    }
    if (this.getWFForce().getOrElse(false) || userInput.askYesNoQuestion("Are you sure to unpublish " +
      conf.getString("group") + ":" +
      conf.getString("artifact") + ":" +
      conf.getString("version") + "?", false)) {
      Logger.messageInfo("About to unpublish: " +
        conf.getString("group") + ":" +
        conf.getString("artifact") + ":" +
        conf.getString("version")
      );

      // GPG signature
      File tmpConf = new File(wfJson.getCanonicalPath() + ".tmp");
      FileUtils.write(tmpConf, conf.toString(2), StandardCharsets.UTF_8);
      Helper.signArtefact(tmpConf, this.getWFGpgKeyId().getOrNull(), this.getWFGpgArg().getOrNull());

      Logger.messageInfo("Unpublishing");
      JSONObject result = Unirest.post(Helper.WF_URL + "/data/unpublish")
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
            conf.getString("version") + " Unpublished"
        );
      }

    }
  }
}
