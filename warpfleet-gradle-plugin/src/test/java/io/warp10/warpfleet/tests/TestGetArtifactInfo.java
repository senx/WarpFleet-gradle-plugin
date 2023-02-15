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

import io.warp10.warpfleet.utils.Helper;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test get artifact info.
 */
public class TestGetArtifactInfo extends AbstractTests {
  /**
   * The constant TASK.
   */
  public static final String TASK = "wfGetArtifactInfo";

  /**
   * Test get artifact info.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo with group and artifact")
  public void testGetArtifactInfo() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test get latest artifact info.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo with group, artifact and latest as version")
  public void testGetLatestArtifactInfo() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "latest"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test get specific artifact info.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo with group, artifact and version")
  public void testGetSpecificArtifactInfo() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "1.0.2-uberjar"
    ), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode:1.0.2-uberjar"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test get artifact info wo group and artifact.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo without group and artifact")
  public void testGetArtifactInfoWOGroupAndArtifact() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test get artifact info wo artifact.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo without artifact")
  public void testGetArtifactInfoWOArtifact() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      "group", "io.warp10"
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test get artifact info wo group.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifactInfo without group ")
  public void testGetArtifactInfoWOGroup() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      "artifact", "warp10-ext-barcode"
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
