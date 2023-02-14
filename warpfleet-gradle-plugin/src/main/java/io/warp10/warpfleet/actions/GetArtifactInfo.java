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
import kong.unirest.json.JSONObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

/**
 * The type Get artifact info.
 */
public abstract class GetArtifactInfo extends DefaultTask {
  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  @Input
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  abstract public Property<String> getWFGroup();

  /**
   * Gets wf artifact.
   *
   * @return the wf artifact
   */
  @Input
  @Option(option = "artifact", description = "Artifact's name, ie: warp10-plugin-mqtt")
  abstract public Property<String> getWFArtifact();

  /**
   * The Wf version.
   *
   * @return the wf version
   */
  @Input
  @Optional
  @Option(option = "vers", description = "Artifact's version, ie: 0.0.3")
  abstract public Property<String> getWFVersion();

  /**
   * Instantiates a new Get artifact info.
   */
  public GetArtifactInfo() {
    this.setDescription("Get Artifact info");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Gets artifact info.
   */
  @TaskAction
  public void getArtifactInfo() {
    String version = this.getWFVersion().getOrNull();
    if (null == version || "unspecified".equals(version) || "latest".equals(version)) {
      version = Helper.getLatest(this.getWFGroup().get(), this.getWFArtifact().get()).getJSONObject("latest").getString("version");
    }
    JSONObject info = Helper.getArtifactInfo(this.getWFGroup().get(), this.getWFArtifact().get(), version);
    System.out.printf("- %s:%s:%s (%s)\n",
      info.getString("group"),
      info.getString("artifact"),
      info.getString("version"),
      info.getString("description"));
  }
}
