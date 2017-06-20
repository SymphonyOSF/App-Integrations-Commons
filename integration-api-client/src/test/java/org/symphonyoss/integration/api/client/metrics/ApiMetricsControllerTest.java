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

package org.symphonyoss.integration.api.client.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.symphonyoss.integration.api.client.metrics.ApiMetricsConstants.CONFIGURATION_API;
import static org.symphonyoss.integration.api.client.metrics.ApiMetricsConstants.OTHER_API;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Unit tests to validate {@link ApiMetricsController}
 * Created by rsanchez on 14/12/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiMetricsControllerTest {

  private static final String CONFIGURATON_REQUEST_PATH = "/configuration/943ds9234d82331";

  private static final String TEST_REQUEST_PATH = "/test/943ds9234d82331";

  @Spy
  private Counter activeApiCalls;

  @Spy
  private ConcurrentMap<String, Timer> timerByApi = new ConcurrentHashMap<>();

  @Spy
  private ConcurrentMap<String, Counter> apiSuccessCounters = new ConcurrentHashMap<>();

  @Spy
  private ConcurrentMap<String, Counter> apiFailCounters = new ConcurrentHashMap<>();

  @InjectMocks
  private ApiMetricsController controller = new ApiMetricsController();

  @Spy
  private MetricRegistry metricRegistry;

  @Before
  public void init() {
    timerByApi.put(CONFIGURATION_API, new Timer());
    timerByApi.put(OTHER_API, new Timer());

    apiSuccessCounters.put(CONFIGURATION_API, new Counter());
    apiSuccessCounters.put(OTHER_API, new Counter());

    apiFailCounters.put(CONFIGURATION_API, new Counter());
    apiFailCounters.put(OTHER_API, new Counter());
  }

  @Test
  public void testInit() {
    doReturn(activeApiCalls).when(metricRegistry).counter(anyString());
    controller.init();
    assertEquals(6, timerByApi.size());
  }

  @Test
  public void testApiCall() {
    Timer.Context testContextSuccess = controller.startApiCall(TEST_REQUEST_PATH);
    Timer.Context testContextFailed = controller.startApiCall(TEST_REQUEST_PATH);
    Timer.Context configurationContextSuccess = controller.startApiCall(CONFIGURATON_REQUEST_PATH);
    Timer.Context configurationContextFailed = controller.startApiCall(CONFIGURATON_REQUEST_PATH);

    assertNotNull(testContextSuccess);
    assertNotNull(testContextFailed);
    assertNotNull(configurationContextSuccess);
    assertNotNull(configurationContextFailed);
    assertEquals(4, activeApiCalls.getCount());

    controller.finishApiCall(testContextSuccess, TEST_REQUEST_PATH, true);
    controller.finishApiCall(testContextFailed, TEST_REQUEST_PATH, false);
    controller.finishApiCall(configurationContextSuccess, CONFIGURATON_REQUEST_PATH, true);
    controller.finishApiCall(configurationContextFailed, CONFIGURATON_REQUEST_PATH, false);

    assertEquals(2, timerByApi.get(OTHER_API).getCount());
    assertEquals(2, timerByApi.get(CONFIGURATION_API).getCount());
    assertEquals(1, apiSuccessCounters.get(OTHER_API).getCount());
    assertEquals(1, apiFailCounters.get(OTHER_API).getCount());
    assertEquals(1, apiSuccessCounters.get(CONFIGURATION_API).getCount());
    assertEquals(1, apiFailCounters.get(CONFIGURATION_API).getCount());
    assertEquals(0, activeApiCalls.getCount());
  }
}
