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

import org.gradle.api.provider.Property;

/**
 * The type Warp fleet extension.
 */
public abstract class WarpFleetExtension {

  /**
   * Gets group.
   *
   * @return the group
   */
  abstract public Property<String> getGroup();

  /**
   * Gets artifact.
   *
   * @return the artifact
   */
  abstract public Property<String> getArtifact();

  /**
   * Gets vers.
   *
   * @return the vers
   */
  abstract public Property<String> getVers();

  /**
   * Gets classifier.
   *
   * @return the classifier
   */
  abstract public Property<String> getClassifier();

  /**
   * Gets warp 10 dir.
   *
   * @return the warp 10 dir
   */
  abstract public Property<String> getWarp10Dir();

  /**
   * Gets packages.
   *
   * @return the packages
   */
  abstract public Property<String> getPackages();

  /**
   * Gets repo url.
   *
   * @return the repo url
   */
  abstract public Property<String> getRepoURL();

  /**
   * Gets url.
   *
   * @return the url
   */
  abstract public Property<String> getUrl();

  /**
   * Gets source.
   *
   * @return the source
   */
  abstract public Property<String> getSource();

  /**
   * Gets dest.
   *
   * @return the dest
   */
  abstract public Property<String> getDest();

  /**
   * Gets format.
   *
   * @return the format
   */
  abstract public Property<String> getFormat();

  /**
   * Gets macro dir.
   *
   * @return the macro dir
   */
  abstract  public Property<String> getMacroDir();
}
