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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDoc extends AbstractTests {
  private static final String TASK = "wfDoc";

  @Test
  @DisplayName("wfDoc default output format")
  public void testGenerateDocDefaultFormat() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath(),
      "macroDir", "senx"
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.json").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.json").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfDoc JSON output format")
  public void testGenerateDocJSON() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath(),
      "macroDir", "senx",
      "format", "json"
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.json").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.json").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfDoc markdown output format")
  public void testGenerateDocMD() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath(),
      "macroDir", "senx",
      "format", "md"
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.md").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.md").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfDoc markdown output format")
  public void testGenerateDocMarkDown() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath(),
      "macroDir", "senx",
      "format", "md"
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.md").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.md").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfDoc html output format")
  public void testGenerateDocHTML() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath(),
      "macroDir", "senx",
      "format", "html"
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.html").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.html").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }

  @Test
  @DisplayName("wfDoc without macroDir")
  public void testGenerateDocWOMacroDir() throws IOException {
    BuildResult result = this.build(getParamsMap(
      "url", "https://sandbox.senx.io/api/v0/exec",
      "source", Paths.get("src", "test", "resources", "macros").toFile().getCanonicalPath(),
      "dest", new File(testProjectDir, "doc").getCanonicalPath()
    ), TASK);
    assertTrue(result.getOutput().contains("@senx/nifi/messageToJson"));
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "index.json").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi").toFile().exists());
    assertTrue(Paths.get(testProjectDir.getCanonicalPath(), "doc", "senx", "nifi", "messageToJson.json").toFile().exists());
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":" + TASK)).getOutcome());
  }
}
