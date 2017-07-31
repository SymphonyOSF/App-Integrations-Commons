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

package org.symphonyoss.integration.authentication.exception;

import org.symphonyoss.integration.exception.IntegrationRuntimeException;

import java.util.List;

/**
 * Abstract class to be used for all Authentication Exception, it contains the component name:
 * Authentication Proxy
 *
 * Created by cmarcondes on 10/26/16.
 */
public abstract class AuthenticationException extends IntegrationRuntimeException {

  private static final String COMPONENT = "Authentication Proxy";

  public AuthenticationException(String message) {
    super(COMPONENT, message);
  }

  public AuthenticationException(String message, String... solutions) {
    super(COMPONENT, message, solutions);
  }

  public AuthenticationException(String message, Throwable cause, String... solutions) {
    super(COMPONENT, message, cause, solutions);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(COMPONENT, message, cause);
  }
}
