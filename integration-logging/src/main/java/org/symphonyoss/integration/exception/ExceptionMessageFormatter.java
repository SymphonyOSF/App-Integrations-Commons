/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.exception;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible to format an exception.
 *
 * Format sample:
 * Component: MyComponent
 * Message: Some Description about the problem.
 * Root cause: The real exception that happened.
 * Solutions: A list of solution to solve the problem
 * Stack trace: Trace of the error.
 *
 * Created by cmarcondes on 10/20/16.
 */
public class ExceptionMessageFormatter {

  private static final String COMPONENT = "Component: ";
  private static final String MESSAGE = "Message: ";
  private static final String SOLUTIONS = "Solutions: ";
  private static final String STACKTRACE = "Stack trace: ";
  private static final String LINE_BREAK = "\n";
  private static final String UNKNOWN = "Unknown";
  private static final String NONE = "None";
  private static final String NO_SOLUTION_MESSAGE =
      "No solution has been cataloged for troubleshooting this problem.";

  /**
   * Formats the message following this sample:
   *
   * Component: <Component>
   * Message: <Message>
   * @param component The component where the exception was thrown.
   * @param message The message why the exceptions happened.
   * @return Formatted message
   */
  public static String format(String component, String message) {
    return getMessage(component, message, null);
  }

  /**
   * Formats the message following this sample:
   *
   * Component: <Component>
   * Message: <Message>
   * Solutions:
   * <Solution A>
   * <Solution B>
   * @param component The component where the exception was thrown.
   * @param message The message why the exceptions happened.
   * @param solutions The solutions provided by exceptions.
   * @return Formatted message
   */
  public static String format(String component, String message,
      String... solutions) {
    return getMessage(component, message, null, solutions);
  }

  /**
   * Formats the message following this sample:
   *
   * Component: <Component>
   * Message: <Message>
   * Stacktrace: <Trace>
   * @param component The component where the exception was thrown.
   * @param message The message why the exceptions happened.
   * @param t The exception caused the problem.
   * @return Formatted message
   */
  public static String format(String component, String message, Throwable t) {
    return getMessage(component, message, t);
  }

  /**
   * * Formats the message following this sample:
   *
   * Component: <Component>
   * Message: <Message>
   * Solutions:
   * <Solution A>
   * <Solution B>
   * Stacktrace: <Trace>
   * @param component The component where the exception was thrown.
   * @param message The message why the exceptions happened.
   * @param t The exception caused the problem.
   * @param solutions The solutions provided by exceptions.
   * @return Formatted message
   */
  public static String format(String component, String message, Throwable t, String... solutions) {
    return getMessage(component, message, t, solutions);
  }

  private static String getMessage(String component, String message, Throwable t, String... solutions) {
    StrBuilder sb = new StrBuilder(LINE_BREAK);
    sb.append(COMPONENT).appendln(StringUtils.isEmpty(component) ? UNKNOWN : component)
        .append(MESSAGE).appendln(StringUtils.isEmpty(message) ? NONE : message);

    sb.appendln(SOLUTIONS);
    if ((solutions != null) && (solutions.length > 0)) {
      sb.appendWithSeparators(solutions, LINE_BREAK).appendNewLine();
    } else {
      sb.appendln(NO_SOLUTION_MESSAGE);
    }

    if (t != null) {
      sb.append(STACKTRACE).appendln(t.getMessage());
    }

    return sb.toString();
  }


}
