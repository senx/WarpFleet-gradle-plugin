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
import io.warp10.warpfleet.actions.InitModule;
import io.warp10.warpfleet.actions.InstallArtifact;
import io.warp10.warpfleet.actions.Publish;
import io.warp10.warpfleet.actions.UnPublish;
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
    // DSL Extension
    WarpFleetExtension ext = project.getExtensions().create("warpfleet", WarpFleetExtension.class);
    // Tasks
    project.getTasks().register("wfGetGroups", GetGroups.class);

    project.getTasks().register("wfGetArtifacts", GetArtifacts.class, t -> t.getWFGroup().set(ext.getGroup()));

    project.getTasks().register("wfGetVersions", GetVersions.class, t -> {
      t.getWFGroup().set(ext.getGroup());
      t.getWFArtifact().set(ext.getArtifact());
    });

    project.getTasks().register("wfGetArtifactInfo", GetArtifactInfo.class, t -> {
      t.getWFGroup().set(ext.getGroup());
      t.getWFArtifact().set(ext.getArtifact());
      t.getWFVersion().set(ext.getVers());
    });

    project.getTasks().register("wfInstall", InstallArtifact.class, t -> {
      t.getWfPackages().set(ext.getPackages());
      t.getWfRepoURL().set(ext.getRepoURL());
      t.getWfClassifier().set(ext.getClassifier());
      t.getWfVersion().set(ext.getVers());
      t.getWfArtifact().set(ext.getArtifact());
      t.getWfGroup().set(ext.getGroup());
      t.getWarp10Dir().set(ext.getWarp10Dir());
    });

    project.getTasks().register("wfDoc", GenerateDocumentation.class, t -> {
      t.getWfFormat().set(ext.getFormat());
      t.getWfMacroDir().set(ext.getMacroDir());
      t.getWfDest().set(ext.getDest());
      t.getWfUrl().set(ext.getUrl());
      t.getWfSource().set(ext.getSource());
    });

    project.getTasks().register("wfPublish", Publish.class, t -> {
      t.getWFRepoUrl().set(ext.getRepoUrl());
      t.getWfVersion().set(ext.getVers());
      t.getWFGpgKeyId().set(ext.getGpgKeyId());
      t.getWFGpgArg().set(ext.getGpgArg());
      t.getWFJson().set(ext.getWfJson());
    });

    project.getTasks().register("wfUnPublish", UnPublish.class, t -> {
      t.getWFRepoUrl().set(ext.getRepoUrl());
      t.getWFVersion().set(ext.getVers());
      t.getWFGpgKeyId().set(ext.getGpgKeyId());
      t.getWFGpgArg().set(ext.getGpgArg());
      t.getWFJson().set(ext.getWfJson());
      t.getWFForce().set(ext.getForce());
    });

    project.getTasks().register("wfInit", InitModule.class, t -> {
      t.getWfVersion().set(ext.getVers());
      t.getWfArtifact().set(ext.getArtifact());
      t.getWfGroup().set(ext.getGroup());
      t.getWfDest().set(ext.getDest());
      t.getWfType().set(ext.getType());
    });
  }
}
