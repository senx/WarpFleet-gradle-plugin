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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test install artifact.
 */
public class TestInstallArtifact extends AbstractTests {
  private static final String TASK = "wfInstall";

  /**
   * Simulate warp 10 directory.
   *
   * @throws IOException the io exception
   */
  @BeforeEach
  public void simulateWarp10Directory() throws IOException {
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d").toFile().mkdirs());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib").toFile().mkdirs());
  }

  /**
   * Test install artifact.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall")
  public void testInstallArtifact() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
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

  /**
   * Test install latest artifact.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall with latest as version")
  public void testInstallLatestArtifact() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
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

  /**
   * Test install artifact specific version.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall with a specific version")
  public void testInstallArtifactSpecificVersion() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
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

  /**
   * Test install multi artifacts.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall multiple packages")
  public void testInstallMultiArtifacts() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
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

  /**
   * Test install multi artifacts wrong syntax.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall multiple packages wrong syntax")
  public void testInstallMultiArtifactsWrongSyntax() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      "packages", "io.warp10:warp10-plugin-warpstudio:,io.warp10warp10-ext-barcodelatest",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
    assertTrue(result.getOutput().contains("Bad syntax"));
  }

  /**
   * Test install multi artifacts w classifier.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall multiple packages using classifier")
  public void testInstallMultiArtifactsWClassifier() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
      "packages", "io.warp10:warp10-plugin-warpstudio:2.0.9:uberjar",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    assertTrue(result.getOutput().contains("successfully deployed"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "etc", "conf.d", "99-io.warp10-warp10-plugin-warpstudio.conf").toFile().exists());
    assertTrue(result.getOutput().contains("99-io.warp10-warp10-plugin-warpstudio.conf"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "warp10", "lib", "io.warp10-warp10-plugin-warpstudio-2.0.9.jar").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test install wo artifact.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall without artifact")
  public void testInstallWOArtifact() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      "group", "io.warp10",
      "vers", "latest",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test install wo group.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall without group")
  public void testInstallWOGroup() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      //"group", "io.warp10",
      "artifact", "warp10-ext-barcode",
      "warp10Dir", Paths.get(testProjectDir.getCanonicalPath(), "warp10").toFile().getCanonicalPath()
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  /**
   * Test install wo W10 dir.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfInstall without warp10Dir")
  public void testInstallWOW10Dir() throws IOException {
    BuildResult result = this.buildAndFail(Helper.getParamsMap(
      "group", "io.warp10",
      "artifact", "warp10-ext-barcode"
    ), TASK);
    assertEquals(FAILED, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
