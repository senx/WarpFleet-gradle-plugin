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

package io.warp10.warpfleet;

import io.warp10.warpfleet.actions.GenerateDocumentation;
import io.warp10.warpfleet.actions.GetArtifactInfo;
import io.warp10.warpfleet.actions.GetArtifacts;
import io.warp10.warpfleet.actions.GetGroups;
import io.warp10.warpfleet.actions.GetVersions;
import io.warp10.warpfleet.actions.InstallArtifact;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The type Wf plugin.
 */
@SuppressWarnings("unused")
public class WFPlugin implements Plugin<Project> {
  /**
   * Instantiates a new Wf plugin.
   */
  public WFPlugin() {

  }

  public void apply(Project project) {
    project.getTasks().register("wgGetArtifactInfo", GetArtifactInfo.class);
    project.getTasks().register("wfGetArtifacts", GetArtifacts.class);
    project.getTasks().register("wfGetGroups", GetGroups.class);
    project.getTasks().register("wfGetVersions", GetVersions.class);
    project.getTasks().register("wfInstall", InstallArtifact.class);
    project.getTasks().register("wfDoc", GenerateDocumentation.class);
  }
}
