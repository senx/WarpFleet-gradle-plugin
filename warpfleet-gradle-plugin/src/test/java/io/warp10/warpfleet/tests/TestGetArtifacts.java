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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test get artifacts.
 */
public class TestGetArtifacts extends AbstractTests {
  private static final String TASK = "wfGetArtifacts";


  /**
   * Test wf get artifacts with no group.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifacts with no group")
  public void testWfGetArtifactsWithNoGroup() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-s3"));
    assertTrue(result.getOutput().contains("io.senx:warp10-ext-web3"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test wf get artifacts with no group.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifacts with group")
  public void testWfGetArtifactsWithGroup() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap("group", "io.warp10"), TASK);
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-s3"));
    assertFalse(result.getOutput().contains("io.senx:warp10-ext-web3"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test wf get artifacts non existing group.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetArtifacts with a non existing group")
  public void testWfGetArtifactsNonExistingGroup() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap("group", "dummy"), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
