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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Test publish.
 */
public class TestPublish extends AbstractTests {

  /**
   * Init.
   *
   * @throws IOException the io exception
   */
  @BeforeEach
  public void init() throws IOException {
    String wfJson = "{\n" +
      "  \"type\": \"plugin\",\n" +
      "  \"group\": \"io.warp10\",\n" +
      "  \"artifact\": \"warp10-plugin-warpstudio\",\n" +
      "  \"version\": \"2.0.6\",\n" +
      "  \"description\": \"WarpStudio, the WarpScript editor\",\n" +
      "  \"author\": \"SenX\",\n" +
      "  \"email\": \"contact@senx.io\",\n" +
      "  \"license\": \"Apache-2.0\",\n" +
      "  \"git\": \"https://github.com/senx/warp10-plugin-warpstudio\",\n" +
      "  \"repoUrl\": \"https://repo.maven.apache.org/maven2\",\n" +
      "  \"tags\": [\n" +
      "    \"Editor\",\n" +
      "    \"WarpScript\",\n" +
      "    \"Warp 10\"\n" +
      "  ]\n" +
      "}";
    this.writeFile(Paths.get(testProjectDir.getCanonicalPath(), "wf.json").toFile(), wfJson);
    this.writeFile(Paths.get(testProjectDir.getCanonicalPath(), "README.md").toFile(), "# Test");
  }

  /**
   * Test publish.
   *
   * @throws IOException the io exception
   */
  @Test
  @DisplayName("wfPublish and UnPublish")
  public void testPublish() throws IOException {
    BuildResult result = this.build(Helper.getParamsMap(
      "force", "true",
      "gpgKeyId", "BD49DA0A",
      "gpgArg", "--passphrase warpfleet",
      "vers", "1.0.42",
      "wfJson", Paths.get(testProjectDir.getCanonicalPath(), "wf.json").toFile().getCanonicalPath()
    ), "wfPublish");
    assertTrue(result.getOutput().contains("io.warp10:warp10-plugin-warpstudio:1.0.42 Published"));
    assertEquals(SUCCESS, Objects.requireNonNull(result.task(":wfPublish")).getOutcome());
    BuildResult result2 = this.build(Helper.getParamsMap(
      "force", "true",
      "gpgKeyId", "BD49DA0A",
      "gpgArg", "--passphrase warpfleet",
      "vers", "1.0.42",
      "wfJson", Paths.get(testProjectDir.getCanonicalPath(), "wf.json").toFile().getCanonicalPath()
    ), "wfUnPublish");
    assertTrue(result2.getOutput().contains("io.warp10:warp10-plugin-warpstudio:1.0.42 Unpublished"));
    assertEquals(SUCCESS, Objects.requireNonNull(result2.task(":wfUnPublish")).getOutcome());
  }
}
