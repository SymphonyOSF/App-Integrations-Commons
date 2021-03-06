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

package org.symphonyoss.integration.exception.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.symphonyoss.integration.exception.ExpectedMessageBuilder;

/**
 * Unit tests for {@link ForbiddenUserException}.
 * Created by crepache on 23/06/17.
 */
public class ForbiddenUserExceptionTest {

  public static final String COMPONENT = "Configuration Service";

  public static final String MESSAGE = "message";

  @Test
  public void testForbiddenUserExceptionWithCauseAndSolution() {
    Throwable cause = new Throwable("cause");
    ForbiddenUserException exception = new ForbiddenUserException("message", cause, "solution");
    String resultMessage = exception.getMessage();
    String expectedMessage = new ExpectedMessageBuilder()
        .component(COMPONENT)
        .message(MESSAGE)
        .solutions("solution")
        .stackTrace(exception.getCause().getMessage())
        .build();

    assertEquals(expectedMessage, resultMessage);
  }
}
