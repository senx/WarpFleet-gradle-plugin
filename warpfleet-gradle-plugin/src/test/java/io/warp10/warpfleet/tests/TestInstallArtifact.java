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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInstallArtifact extends AbstractTests {
  private static final String TASK = "wfInstall";

  @BeforeEach
  public void simulateWarp10Directory() throws IOException {
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d").toFile().mkdirs());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib").toFile().mkdirs());
  }

  @Test
  @DisplayName("wfInstall")
  public void testInstallArtifact() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    AbstractTests.printDirectoryTree(Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile());
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-ext-barcode.conf"));
    assertTrue(result.getOutput().contains("successfully deployed"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-ext-barcode.conf").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfInstall with latest as version")
  public void testInstallLatestArtifact() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "latest",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    AbstractTests.printDirectoryTree(Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile());
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-ext-barcode.conf"));
    assertTrue(result.getOutput().contains("successfully deployed"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-ext-barcode.conf").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfInstall with a specific version")
  public void testInstallArtifactSpecificVersion() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "vers", "1.0.2-uberjar",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    AbstractTests.printDirectoryTree(Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile());
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-ext-barcode.conf"));
    assertTrue(result.getOutput().contains("successfully deployed"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-ext-barcode.conf").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib", "io.warp10-warp10-ext-barcode-1.0.2-uberjar.jar").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfInstall multiple packages")
  public void testInstallMultiArtifacts() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "packages", "io.warp10:warp10-plugin-warpstudio:2.0.9,io.warp10:warp10-ext-barcode:latest,io.warp10:warp10-ext-jdbc",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    AbstractTests.printDirectoryTree(Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile());
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-plugin-warpstudio.conf"));
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-ext-barcode.conf"));
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-ext-jdbc.conf"));
    assertTrue(result.getOutput().contains("successfully deployed"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-plugin-warpstudio.conf").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-ext-barcode.conf").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-ext-jdbc.conf").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib", "warp10-warpstudio-server-2.0.9.jar").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib", "io.warp10-warp10-plugin-warpstudio-2.0.9.jar").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

}
