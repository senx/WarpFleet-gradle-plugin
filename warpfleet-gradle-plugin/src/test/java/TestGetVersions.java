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

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGetVersions extends AbstractTests {

  @Test
  @DisplayName("wfGetVersions with group and artifact")
  public void testWfGetVersions() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n" +
      "warpfleet {\n" +
      "  group = 'io.warp10'\n" +
      "  artifact = 'warp10-ext-barcode'\n" +
      "}\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("wfGetVersions")
      .build();

    assertTrue(result.getOutput().contains("- Latest version:"));
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":wfGetVersions")).getOutcome());
  }
  @Test
  @DisplayName("wfGetVersions with group, artifact and latest as version")
  public void testWfGetVersionsLatest() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n" +
      "warpfleet {\n" +
      "  group = 'io.warp10'\n" +
      "  artifact = 'warp10-ext-barcode'\n" +
      "  vers = 'latest'\n"+
      "}\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("wfGetVersions")
      .build();

    assertTrue(result.getOutput().contains("- Latest version:"));
    assertTrue(result.getOutput().contains("io.warp10:warp10-ext-barcode"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":wfGetVersions")).getOutcome());
  }

  @Test
  @DisplayName("wfGetVersions without group and artifact")
  public void testWfGetVersionsWOGroupAndArtifact() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n" +
      "warpfleet {\n" +
      "}\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("wfGetVersions")
      .buildAndFail();

    assertEquals(FAILED, Objects.requireNonNull(result.task(":wfGetVersions")).getOutcome());
  }

  @Test
  @DisplayName("wfGetVersions without artifact")
  public void testWfGetVersionsWOArtifact() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n" +
      "warpfleet {\n" +
      "  group = 'io.warp10'\n" +
      "}\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("wfGetVersions")
      .buildAndFail();

    assertEquals(FAILED, Objects.requireNonNull(result.task(":wfGetVersions")).getOutcome());
  }

  @Test
  @DisplayName("wfGetVersions without group ")
  public void testWfGetVersionsWOGroup() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n" +
      "warpfleet {\n" +
      "  artifact = 'warp10-ext-barcode'\n" +
      "}\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("wfGetVersions")
      .buildAndFail();

    assertEquals(FAILED, Objects.requireNonNull(result.task(":wfGetVersions")).getOutcome());
  }
}
