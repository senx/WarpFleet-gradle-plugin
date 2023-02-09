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
import org.gradle.api.tasks.TaskAction;

/**
 * The type Get groups.
 */
@SuppressWarnings("unused")
public class GetGroups extends DefaultTask {

  /**
   * Instantiates a new Get groups.
   */
  public GetGroups() {
    this.setDescription("Get list of available groups");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Java task.
   */
  @TaskAction
  public void getGroups() {
    Helper.getGroups().forEach(item -> {
      JSONObject repo = (JSONObject) item;
      System.out.printf("- %s\n", repo.getString("name"));
    });
  }
}