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

package io.senx.warpfleet;

import io.senx.warpfleet.utils.Constants;
import io.senx.warpfleet.utils.Helper;
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

/**
 * The type Get artifact info.
 */
@SuppressWarnings("unused")
public class GetArtifactInfo extends DefaultTask {
  @Input
  String wfGroup;
  /**
   * The Wf artifact.
   */
  @Input
  String wfArtifact;
  /**
   * The Wf version.
   */
  @Input
  @Optional
  String wfVersion;

  public GetArtifactInfo() {
    this.setDescription("Get Artifact info");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Java task.
   */
  @TaskAction
  public void getArtifactInfo() {
    if (null == this.wfVersion || "unspecified".equals(this.wfVersion) || "latest".equals(this.wfVersion)) {
      this.wfVersion = Helper.getLatest(this.wfGroup, this.wfArtifact).getJSONObject("latest").getString("version");
    }
    JSONObject info = Helper.getArtifactInfo(this.wfGroup, this.wfArtifact, this.wfVersion);
    System.out.printf("- %s:%s:%s (%s)\n",
        info.getString("group"),
        info.getString("artifact"),
        info.getString("version"),
        info.getString("description"));
  }

  /**
   * Gets wfGroup.
   *
   * @return the wfGroup
   */
  public String getWfGroup() {
    return wfGroup;
  }

  /**
   * Sets wfGroup.
   *
   * @param wfGroup the wfGroup
   */
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  public void setWfGroup(String wfGroup) {
    this.wfGroup = wfGroup;
  }

  /**
   * Gets wf artifact.
   *
   * @return the wf artifact
   */
  public String getWfArtifact() {
    return wfArtifact;
  }

  /**
   * Sets wf artifact.
   *
   * @param wfArtifact the wf artifact
   */
  @Option(option = "artifact", description = "Artifact's name, ie: warp10-plugin-mqtt")
  public void setWfArtifact(String wfArtifact) {
    this.wfArtifact = wfArtifact;
  }

  /**
   * Gets wf version.
   *
   * @return the wf version
   */
  public String getWfVersion() {
    return wfVersion;
  }

  /**
   * Sets wf version.
   *
   * @param wfVersion the wf version
   */
  @Option(option = "version", description = "Artifact's version, ie: 0.0.3")
  public void setWfVersion(String wfVersion) {
    this.wfVersion = wfVersion;
  }
}
