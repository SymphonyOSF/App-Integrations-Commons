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

package org.symphonyoss.integration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.symphonyoss.integration.model.stream.StreamType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to deal with the instance configurations.
 * Created by mquilzini on 31/05/16.
 */
public class WebHookConfigurationUtils {

  public static final String STREAMS = "streams";

  public static final String REMOVED_STREAMS = "removed_streams";

  public static final String OWNER = "owner";

  public static final String LAST_POSTED_DATE = "lastPostedDate";

  public static final String NOTIFICATIONS = "notifications";

  public static final String STREAM_TYPE = "streamType";

  private static final ObjectMapper mapper = new ObjectMapper();

  private WebHookConfigurationUtils() {
  }

  /**
   * Retrieve the streams configured by the user
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @return List of streams configured by the user
   */
  public static List<String> getStreams(String optionalProperties) throws IOException {
    return getList(optionalProperties, STREAMS);
  }

  /**
   * Set the list of streams configured by the user
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @param streams List of streams configured by the user
   * @return JSON Object optional properties
   */
  public static ObjectNode setStreams(String optionalProperties, List<String> streams)
      throws IOException {
    ObjectNode optionalPropertiesNode = (ObjectNode) mapper.readTree(optionalProperties);
    ArrayNode streamArray = mapper.createArrayNode();

    for (String stream : streams) {
      streamArray.add(stream);
    }

    optionalPropertiesNode.set(STREAMS, streamArray);
    return optionalPropertiesNode;
  }

  /**
   * Set the list of streams removed by the Integration Bridge
   *
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @param streams List of streams removed by the Integration Bridge
   * @return JSON Object optional properties
   */
  public static ObjectNode setRemovedStreams(JsonNode optionalProperties, List<String> streams)
      throws IOException {
    ObjectNode optionalPropertiesNode = (ObjectNode) optionalProperties;
    ArrayNode streamArray = mapper.createArrayNode();

    for (String stream : streams) {
      streamArray.add(stream);
    }

    optionalPropertiesNode.set(REMOVED_STREAMS, streamArray);
    return optionalPropertiesNode;
  }

  /**
   * Retrieve the stream type configured by user
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @return Stream type configured by user or StreamType.NONE if have no stream type configured.
   */
  public static StreamType getStreamType(String optionalProperties) throws IOException {
    if (optionalProperties == null) {
      return StreamType.NONE;
    }

    JsonNode optionalPropertiesNode = mapper.readTree(optionalProperties);
    String streamType = optionalPropertiesNode.path(STREAM_TYPE).asText();

    try {
      return StreamType.valueOf(streamType);
    } catch (IllegalArgumentException e) {
      return StreamType.NONE;
    }
  }

  /**
   * Retrieve the owner configured by the user
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @return Owner configured by the user
   */
  public static Long getOwner(String optionalProperties) throws IOException {
    if (optionalProperties == null) {
      return null;
    }

    JsonNode optionalPropertiesNode = mapper.readTree(optionalProperties);
    return optionalPropertiesNode.path(OWNER).asLong();
  }

  /**
   * Retrieve the notifications configured by the user
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @return List of notifications configured by the user
   */
  public static List<String> getSupportedNotifications(String optionalProperties)
      throws IOException {
    return getList(optionalProperties, NOTIFICATIONS);
  }

  /**
   * Retrieve the list of strings based on JSON Array
   * @param optionalProperties JSON Object that contains the information configured by the user
   * @param nodeName Name of JSON array node
   * @return List of strings
   */
  private static List<String> getList(String optionalProperties, String nodeName)
      throws IOException {
    if (optionalProperties == null) {
      return Collections.emptyList();
    }

    List<String> notifications = new ArrayList<>();
    JsonNode optionalPropertiesNode = mapper.readTree(optionalProperties);
    JsonNode arrayNode = optionalPropertiesNode.path(nodeName);

    if (arrayNode.isArray()) {
      for (JsonNode node : arrayNode) {
        notifications.add(node.asText());
      }
    }

    return notifications;
  }

  /**
   * Converts a {@link JsonNode} object into a Json string.
   * @param jsonCandidate
   * @return the json string representation of the jsonCandidate.
   * @throws JsonProcessingException
   */
  public static String toJsonString(JsonNode jsonCandidate) throws JsonProcessingException {
    return mapper.writeValueAsString(jsonCandidate);
  }

  /**
   * Converts a json string into a {@link ObjectNode} object.
   * @param json
   * @return the {@link ObjectNode} object.
   * @throws IOException
   */
  public static ObjectNode fromJsonString(String json) throws IOException {
    return (ObjectNode) mapper.readTree(json);
  }

}
