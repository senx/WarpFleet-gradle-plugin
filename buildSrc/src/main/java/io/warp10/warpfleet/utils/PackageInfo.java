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

package io.warp10.warpfleet.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * The type Package info.
 */
@SuppressWarnings("unused")
public class PackageInfo {
  private String group;
  private String name;
  private String version;
  private String classifier;

  /**
   * Instantiates a new Package info.
   */
  public PackageInfo() {

  }

  /**
   * Instantiates a new Package info.
   *
   * @param group      the group
   * @param artifact   the artifact
   * @param version    the version
   * @param classifier the classifier
   */
  public PackageInfo(String group, String artifact, String version, String classifier) {
    this.group = group;
    this.name = artifact;
    this.version = version;
    this.classifier = classifier;
  }

  @Override
  public String toString() {
    String cn = group + ":" + name + ":" + version;
    if(StringUtils.isNotBlank(classifier)) {
      cn += ":" + classifier;
    }
    return cn;
  }

  /**
   * Gets group.
   *
   * @return the group
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets group.
   *
   * @param group the group
   */
  public void setGroup(String group) {
    this.group = group;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets version.
   *
   * @param version the version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets classifier.
   *
   * @return the classifier
   */
  public String getClassifier() {
    return classifier;
  }

  /**
   * Sets classifier.
   *
   * @param classifier the classifier
   */
  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }
}
