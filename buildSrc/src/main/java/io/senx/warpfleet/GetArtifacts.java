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
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

/**
 * The type Get artifacts.
 */
@SuppressWarnings("unused")
public class GetArtifacts extends DefaultTask {
  /**
   * The Wf group.
   */
  @Input
  String wfGroup;

  public GetArtifacts() {
    this.setDescription("Get list of available artifacts");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Gets artifacts.
   */
  @TaskAction
  public void getArtifacts() {
    Helper.getArtifacts(this.wfGroup).forEach(item -> {
      JSONObject repo = (JSONObject) item;
      System.out.printf("- %s:%s:%s (%s)\n",
          repo.getJSONObject("latest").getString("group"),
          repo.getJSONObject("latest").getString("artifact"),
          repo.getJSONObject("latest").getString("version"),
          repo.getJSONObject("latest").getString("description"));
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
}
