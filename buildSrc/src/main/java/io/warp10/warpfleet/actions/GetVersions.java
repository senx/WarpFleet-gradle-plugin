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
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

/**
 * The type Get versions.
 */
@SuppressWarnings("unused")
public class GetVersions extends DefaultTask {
  /**
   * The Wf group.
   */
  @Input
  String wfGroup;
  /**
   * The Wf artifact.
   */
  @Input
  String wfArtifact;

  /**
   * Instantiates a new Get versions.
   */
  public GetVersions() {
    this.setDescription("Get list of available artifact's versions");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Gets versions.
   */
  @TaskAction
  public void getVersions() {
    JSONObject versions = Helper.getVersions(this.wfGroup, this.wfArtifact);
    System.out.printf("- Name:            %s:%s\n",
        versions.getJSONObject("latest").getString("group"),
        versions.getJSONObject("latest").getString("artifact")
    );
    System.out.printf("- Description:     %s\n", versions.getJSONObject("latest").getString("description"));
    System.out.printf("- Latest version:  %s\n", versions.getJSONObject("latest").getString("version"));
    System.out.println("- Available:");
    versions.getJSONArray("children").forEach(item -> {
      JSONObject repo = (JSONObject) item;
      System.out.printf("    - %s\n", repo.getString("name"));
    });
  }

  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  public String getWfGroup() {
    return wfGroup;
  }

  /**
   * Sets wf group.
   *
   * @param wfGroup the wf group
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
}
