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
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The type Tests.
 */
abstract public class AbstractTests {
  /**
   * The Test project dir.
   */
  @TempDir
  File testProjectDir;
  /**
   * The Build file.
   */
  File buildFile;

  /**
   * Sets .
   *
   * @throws IOException the io exception
   */
  @BeforeEach
  public void setup() throws IOException {
    buildFile = new File(testProjectDir, "build.gradle");
    try (InputStream tpl = getClass().getResourceAsStream("/testkit-gradle.properties")) {
      if (null != tpl) {
        File props = new File(testProjectDir + File.separator + "gradle.properties");
        Files.copy(tpl, props.toPath());
      }
    }
  }

  /**
   * Write file.
   *
   * @param destination the destination
   * @param content     the content
   * @throws IOException the io exception
   */
  void writeFile(File destination, String content) throws IOException {
    try (BufferedWriter output = new BufferedWriter(new FileWriter(destination))) {
      output.write(content);
    }
  }

  /**
   * Build and fail result.
   *
   * @param params the params
   * @param task   the task
   * @return the build result
   * @throws IOException the io exception
   */
  BuildResult buildAndFail(Map<String, String> params, String task) throws IOException {
    this.writeBuildFile(params);
    return GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .withArguments(task)
      .forwardOutput()
      .buildAndFail();
  }

  /**
   * Build result.
   *
   * @param params the params
   * @param task   the task
   * @return the build result
   * @throws IOException the io exception
   */
  BuildResult build(Map<String, String> params, String task) throws IOException {
    this.writeBuildFile(params);
    return GradleRunner.create()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withTestKitDir(testProjectDir)
      .forwardOutput()
      .withArguments(task)
      .build();
  }

  private void writeBuildFile(Map<String, String> params) throws IOException {
    StringBuilder buildFileContent = new StringBuilder()
      .append("plugins { id \"io.warp10.warpfleet-gradle-plugin\" }\n")
      .append("warpfleet {\n");

    params.forEach((key, value) -> buildFileContent.append("\t").append(key).append(" = '").append(value).append("'\n"));
    buildFileContent.append("}\n");
    this.writeFile(buildFile, buildFileContent.toString());
  }

  /**
   * Gets params map.
   *
   * @param data the data
   * @return the params map
   */
  public static Map<String, String> getParamsMap(String... data) {
    Map<String, String> result = new HashMap<>();

    if (data.length % 2 != 0) {
      throw new IllegalArgumentException("Odd number of arguments");
    }

    String key = null;
    int step = -1;

    for (String value : data) {
      step++;
      switch (step % 2) {
        case 0:
          if (value == null)
            throw new IllegalArgumentException("Null key value");
          key = value;
          continue;
        case 1:
          result.put(key, value);
          break;
      }
    }

    return result;
  }

  /**
   * Print directory tree.
   *
   * @param folder the folder
   */
  static void printDirectoryTree(File folder) {
    StringBuilder sb = new StringBuilder();
    printDirectoryTree(folder, 2, sb);
    System.out.println(sb);
  }

  private static void printDirectoryTree(File folder, int indent, StringBuilder sb) {
    if (!folder.isDirectory()) {
      throw new IllegalArgumentException("folder is not a Directory");
    }
    sb.append(getIndentString(indent)).append("+--").append(folder.getName()).append("/\n");
    for (File file : Objects.requireNonNull(folder.listFiles())) {
      if (file.isDirectory()) {
        printDirectoryTree(file, indent + 1, sb);
      } else {
        printFile(file, indent + 1, sb);
      }
    }

  }

  private static void printFile(File file, int indent, StringBuilder sb) {
    sb.append(getIndentString(indent)).append("+--").append(file.getName()).append("\n");
  }

  private static String getIndentString(int indent) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      sb.append("|  ");
    }
    return sb.toString();
  }
}
