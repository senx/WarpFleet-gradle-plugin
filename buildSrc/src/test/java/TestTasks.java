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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test tasks.
 */
public class TestTasks extends AbstractTests {
  /**
   * Test get tasks.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("tasks")
  public void testGetTasks() throws IOException {
    String buildFileContent = "plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments("tasks")
      .build();

    assertTrue(result.getOutput().contains("WarpFleet tasks"));
    assertTrue(result.getOutput().contains("wfDoc"));
    assertTrue(result.getOutput().contains("wfGetArtifacts"));
    assertTrue(result.getOutput().contains("wfGetGroups"));
    assertTrue(result.getOutput().contains("wfGetVersions"));
    assertTrue(result.getOutput().contains("wfInstall"));
    assertTrue(result.getOutput().contains("wgGetArtifactInfo"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":tasks")).getOutcome());
  }
}
