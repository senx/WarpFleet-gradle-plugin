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
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Get artifacts.
 */
public abstract class GetArtifacts extends DefaultTask {
  /**
   * Gets wf group.
   *
   * @return the wf group
   */
  @Input
  @Optional
  @Option(option = "group", description = "Artifact's group, ie: io.warp10")
  abstract public Property<String> getWFGroup();

  /**
   * Instantiates a new Get artifacts.
   */
  public GetArtifacts() {
    this.setDescription("Get list of available artifacts");
    this.setGroup(Constants.GROUP);
  }

  /**
   * Gets artifacts.
   */
  @TaskAction
  public void getArtifacts() {
    System.out.println(this.getWFGroup().getOrNull());
    List<String> groups = new ArrayList<>();
    if (StringUtils.isBlank(this.getWFGroup().getOrNull())) {
      Helper.getGroups().forEach(g -> groups.add(((JSONObject) g).getString("name")));
    } else {
      groups.add(this.getWFGroup().get().trim());
    }
    groups.forEach(g -> Helper.getArtifacts(g).forEach(item -> {
      JSONObject repo = (JSONObject) item;
      JSONObject latest = repo.optJSONObject("latest");
      if (null != latest) {
        System.out.printf("- %s:%s:%s (%s)\n",
          latest.getString("group"),
          latest.getString("artifact"),
          latest.getString("version"),
          latest.getString("description"));
      }
    }));
  }
}
