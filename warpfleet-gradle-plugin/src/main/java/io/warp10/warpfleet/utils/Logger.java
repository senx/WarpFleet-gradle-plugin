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

package io.warp10.warpfleet.utils;

/**
 * The type Logger.
 */
public class Logger {

  /**
   * The constant ANSI_RESET.
   */
  public static final String ANSI_RESET = "\u001B[0m";
  /**
   * The constant ANSI_BLACK.
   */
  public static final String ANSI_BLACK = "\u001B[30m";
  /**
   * The constant ANSI_RED.
   */
  public static final String ANSI_RED = "\u001B[31m";
  /**
   * The constant ANSI_GREEN.
   */
  public static final String ANSI_GREEN = "\u001B[32m";
  /**
   * The constant ANSI_YELLOW.
   */
  public static final String ANSI_YELLOW = "\u001B[33m";
  /**
   * The constant ANSI_BLUE.
   */
  public static final String ANSI_BLUE = "\u001B[34m";
  /**
   * The constant ANSI_PURPLE.
   */
  public static final String ANSI_PURPLE = "\u001B[35m";
  /**
   * The constant ANSI_CYAN.
   */
  public static final String ANSI_CYAN = "\u001B[36m";
  /**
   * The constant ANSI_WHITE.
   */
  public static final String ANSI_WHITE = "\u001B[37m";
  /**
   * The constant ANSI_CHECK.
   */
  public static final String ANSI_CHECK = "✔";
  /**
   * The constant ANSI_CROSS.
   */
  public static final String ANSI_CROSS = "✖";
  /**
   * The constant ANSI_INFO.
   */
  public static final String ANSI_INFO = "○";
  /**
   * The constant ANSI_WARN.
   */
  public static final String ANSI_WARN = "⚠";

  /**
   * Instantiates a new Logger.
   */
  Logger() {

  }

  /**
   * Message info.
   *
   * @param message the message
   */
  public static void messageInfo(String message) {
    System.out.println(ANSI_CYAN + ANSI_INFO + " " + message + ANSI_RESET);
  }

  /**
   * Message error.
   *
   * @param message the message
   */
  public static void messageError(String message) {
    System.out.println(ANSI_RED + ANSI_CROSS + " " + message + ANSI_RESET);
  }

  /**
   * Message susccess.
   *
   * @param message the message
   */
  public static void messageSusccess(String message) {
    System.out.println(ANSI_GREEN + ANSI_CHECK + " " + message + ANSI_RESET);
  }

  /**
   * Message warning.
   *
   * @param message the message
   */
  public static void messageWarning(String message) {
    System.out.println(ANSI_YELLOW + ANSI_WARN + " " + message + ANSI_RESET);
  }
}
