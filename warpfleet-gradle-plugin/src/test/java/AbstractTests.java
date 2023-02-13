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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
   */
  @BeforeEach
  public void setup() {
    buildFile = new File(testProjectDir, "build.gradle");
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
}
