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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test get groups.
 */
public class TestGetGroups extends AbstractTests {
  private static final String TASK = "wfGetGroups";

  /**
   * Test wf get groups.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfGetGroups")
  public void testWfGetGroups() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(), TASK);
    assertTrue(result.getOutput().contains("io.warp10"));
    assertTrue(result.getOutput().contains("io.senx"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
