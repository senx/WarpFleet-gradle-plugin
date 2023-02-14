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

package io.warp10.warpfleet.tests;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGetArtifactInfo extends AbstractTests {
  public static final String TASK = "wgGetArtifactInfo";

  @Test
  @DisplayName("wgGetArtifactInfo with group and artifact")
  public void testGetArtifactInfo() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
  @Test
  @DisplayName("wgGetArtifactInfo with group, artifact and latest as version")
  public void testGetLatestArtifactInfo() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "latest"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
  @Test
  @DisplayName("wgGetArtifactInfo with group, artifact and version")
  public void testGetSpecificArtifactInfo() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "1.0.2-uberjar"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode:1.0.2-uberjar"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wgGetArtifactInfo without group and artifact")
  public void testGetArtifactInfoWOGroupAndArtifact() throws IOException {
    BuildResult result = this.buildAndFail(getParamsMap(), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wgGetArtifactInfo without artifact")
  public void testGetArtifactInfoWOArtifact() throws IOException {
    BuildResult result = this.buildAndFail(getParamsMap(
      "group", "io.warp10"
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wgGetArtifactInfo without group ")
  public void testGetArtifactInfoWOGroup() throws IOException {
    BuildResult result = this.buildAndFail(getParamsMap(
      "artifact", "warp10-ext-barcode"
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
