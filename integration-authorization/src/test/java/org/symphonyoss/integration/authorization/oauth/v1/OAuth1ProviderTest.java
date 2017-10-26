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

package org.symphonyoss.integration.authorization.oauth.v1;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.symphonyoss.integration.authorization.oauth.OAuthRsaSignerFactory;
import org.symphonyoss.integration.exception.ExceptionMessageFormatter;
import org.symphonyoss.integration.logging.LogMessageSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Unit tests for {@link OAuth1Provider}.
 * Created by campidelli on 25-jul-17.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(
    {OAuth1Provider.class, NetHttpTransport.class, HttpRequestFactory.class, HttpRequest.class,
        HttpResponseException.class})
public class OAuth1ProviderTest {

  static final URL BASE_URL = makeUrl("http://www.1nv4lidh0st.com");
  static final URL REQUEST_TEMPORARY_TOKEN_URL = makeUrl(BASE_URL, "/reqTempToken");
  static final URL AUTHORIZATION_CALLBACK_URL = makeUrl(BASE_URL, "/myCallback");
  static final URL AUTHORIZE_TEMPORARY_TOKEN_URL = makeUrl(BASE_URL, "/authTempToken");
  static final URL REQUEST_ACCESS_TOKEN_URL = makeUrl(BASE_URL, "/reqAccessToken");
  static final String TOKEN = "token123";
  static final String CONSUMER_KEY = "OauthKey";
  static final String PRIVATE_KEY = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAN8wcS"
      + "F5AE7sL30p2mnM0X3T1OZy4BDfxucZTYdYmg99vqv6uVQyjc4zKOHRiwnCh2GwatT4jBfoQfWx6VUmvcxKHuZwcVCH"
      + "F/u/Vw85wsMDpD4pBglpX1GsFlfSQe1E115X7mHD7tHlkQHvtVplf5BmYxM6G2EljBmiRRQq4OLbAgMBAAECgYEAxu"
      + "54h6tAWRgvo9IgOVk0CIE9LEKL8L5knStybQbOGqyrvMJ3WdLNjlMPR2fsE8DtxmbmcfkvdUexMvtmzF0BoWDvJgqn"
      + "GaUr9l0gZfGCR0ir2PBJ7V9OOJz5ug4ExLz6S9WNV6RdtXOSXSbNG3/L+56tocA05JpZrZaUfK43V0ECQQDyjkokOr"
      + "k54DwdnSH86V2bXn+RlzAyumhfGKJpC7pbeZgcSJtkbV9RslEr+TcVuuJyHZGeWtPEStl1BaKnvRLxAkEA649aVUD1"
      + "b9Cly+Q2l7KbgDjny5k/Ezw7JK3hjYEKQrHjgkMejOuKSkeRz2imWD8PLoJ01GgMXLIiu+F1lb06iwJBAI7NJuldiV"
      + "+BnOLyd+gmnG20nPZiRIYZKQmTv0qJFRZ16A/+zz25Br1adl+lQcERXfBBaFIKt1KBnrU+tBx9PIECQQCLquG6rttX"
      + "wvSrIdMkuufsbNEzLNfzRcEjjF2yExLMXMEymS1iDL5gMHNJ8RjANhOAViWDU3YQ+CYUFCgt8pblAkEAhM5ky54f3U"
      + "ViEO29UyWv2ZNaZPd17bSr8HAo/lxXyju4TRNRB3vIq79lMNalX5HKHlI9EST7xXLh110xXRH9/Q\\=\\=";

  @Mock
  LogMessageSource logMessage;

  @Mock
  private OAuth1GetTemporaryToken mockOAuth1GetTemporaryToken;

  @Mock
  private OAuth1GetAccessToken mockOAuth1GetAccessToken;

  @Mock
  private OAuthRsaSignerFactory rsaSignerFactory;

  @Mock
  private NetHttpTransport transport;

  @Mock
  private HttpRequestFactory requestFactory;

  @Mock
  private HttpRequest mockHttpRequest;

  @Mock
  private HttpResponseException mockHttpResponseException;

  private OAuthRsaSigner mockOAuthRsaSigner = new OAuthRsaSigner();

  @InjectMocks
  private OAuth1Provider authProvider =
      new OAuth1ProviderMock(CONSUMER_KEY, PRIVATE_KEY, REQUEST_TEMPORARY_TOKEN_URL,
          AUTHORIZATION_CALLBACK_URL, AUTHORIZE_TEMPORARY_TOKEN_URL, REQUEST_ACCESS_TOKEN_URL);

  @Test
  public void testRequestTemporaryToken() throws Exception {
    doReturn(mockOAuthRsaSigner).when(rsaSignerFactory).getOAuthRsaSigner(PRIVATE_KEY);

    PowerMockito.whenNew(OAuth1GetTemporaryToken.class)
        .withAnyArguments()
        .thenReturn(mockOAuth1GetTemporaryToken);

    doReturn(TOKEN).when(mockOAuth1GetTemporaryToken).getValue();

    String temporaryToken = authProvider.requestTemporaryToken();
    assertEquals(TOKEN, temporaryToken);
  }

  @Test
  public void testRequestAuthorizationUrl() throws Exception {
    String expected = AUTHORIZE_TEMPORARY_TOKEN_URL + "?oauth_token=" + TOKEN;
    String result = authProvider.requestAuthorizationUrl(TOKEN);
    assertEquals(expected, result);
  }

  @Test
  public void testRequestAcessToken() throws Exception {
    PowerMockito.whenNew(OAuth1GetAccessToken.class)
        .withAnyArguments()
        .thenReturn(mockOAuth1GetAccessToken);

    doReturn(TOKEN).when(mockOAuth1GetAccessToken).getValue();

    String temporaryToken = authProvider.requestAcessToken(StringUtils.EMPTY, StringUtils.EMPTY);
    assertEquals(TOKEN, temporaryToken);
  }

  @Test(expected = OAuth1Exception.class)
  public void testMakeInvalidAuthorizedRequest() throws Exception {
    doReturn(mockOAuthRsaSigner).when(rsaSignerFactory).getOAuthRsaSigner(PRIVATE_KEY);

    HttpResponse response = authProvider.makeAuthorizedRequest(
        TOKEN, BASE_URL, HttpMethods.GET, null);
    authProvider.makeAuthorizedRequest(TOKEN, BASE_URL, HttpMethods.GET, null);
    fail("Should have thrown OAuth1Exception.");
  }

  @Test(expected = OAuth1Exception.class)
  public void testRequestTemporaryTokenOAuth1Exception() throws Exception {
    PowerMockito.whenNew(OAuth1GetTemporaryToken.class)
        .withAnyArguments()
        .thenReturn(mockOAuth1GetTemporaryToken);

    doThrow(IOException.class).when(mockOAuth1GetTemporaryToken).getValue();
    authProvider.requestTemporaryToken();

    fail("Should have thrown OAuth1Exception.");
  }

  @Test(expected = OAuth1Exception.class)
  public void testRequestAccessTokenOAuth1Exception() throws Exception {
    PowerMockito.whenNew(OAuth1GetAccessToken.class)
        .withAnyArguments()
        .thenReturn(mockOAuth1GetAccessToken);

    doThrow(IOException.class).when(mockOAuth1GetAccessToken).getValue();
    authProvider.requestAcessToken(StringUtils.EMPTY, StringUtils.EMPTY);

    fail("Should have thrown OAuth1Exception.");
  }

  @Test(expected = OAuth1Exception.class)
  public void testUnconfiguredProvider() throws Exception {
    ((OAuth1ProviderMock) authProvider).setConfigured(false);
    authProvider.requestAcessToken(StringUtils.EMPTY, StringUtils.EMPTY);

    fail("Should have thrown OAuth1Exception.");
  }

  @Test
  public void testInvalidHttpRequest() throws Exception {
    String message = "Invalid parameters";

    PowerMockito.whenNew(NetHttpTransport.class).withAnyArguments().thenReturn(transport);
    doReturn(requestFactory).when(transport).createRequestFactory(any(OAuthParameters.class));
    doReturn(mockHttpRequest).when(requestFactory)
        .buildRequest(anyString(), any(GenericUrl.class), any(HttpContent.class));
    doThrow(mockHttpResponseException).when(mockHttpRequest).execute();

    doReturn(HttpStatus.SC_BAD_REQUEST).when(mockHttpResponseException).getStatusCode();
    doReturn(message).when(mockHttpResponseException).getStatusMessage();

    try {
      authProvider.makeAuthorizedRequest("", BASE_URL, HttpMethods.GET, null);
      fail("Should have thrown OAuth1HttpRequestException");
    } catch (OAuth1HttpRequestException e) {
      assertEquals(HttpStatus.SC_BAD_REQUEST, e.getCode());
      assertEquals(
          ExceptionMessageFormatter.format("Third-party integration/app authorization.", message),
          e.getMessage());
    }
  }

  private static URL makeUrl(String urlString) {
    try {
      return new URL(urlString);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid URL.", e);
    }
  }

  private static URL makeUrl(URL baseUrl, String urlString) {
    try {
      return new URL(baseUrl, urlString);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Invalid URL.", e);
    }
  }
}
