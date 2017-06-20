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

package org.symphonyoss.integration.webhook.parser.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.model.message.Message;
import org.symphonyoss.integration.webhook.exception.MetadataParserException;

import java.io.IOException;

/**
 * Unit test class for {@link MetadataParser}
 * Created by rsanchez on 03/04/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class MetadataParserTest {

  private static final String INVALID_TEMPLATE_FILE = "invalidTemplate.xml";

  private static final String INEXISTENT_TEMPLATE_FILE = "inexistentTemplate.xml";

  private static final String INVALID_METADATA_FILE = "invalidMetadata.xml";

  private static final String INEXISTENT_METADATA_FILE = "inexistentMetadata.xml";

  private static final String INPUT_FILE = "inputData.json";

  private static final String TEMPLATE = "templateMessageML.xml";

  private static final String SIMPLE_METADATA = "simpleMetadata.xml";

  private static final String SIMPLE_METADATA_WITH_TYPE_FIELD_BOOLEAN = "simpleMetadataWithTypeFieldBoolean.xml";

  private static final String SIMPLE_METADATA_WITH_TYPE_FIELD_INVALID = "simpleMetadataWithTypeFieldInvalid.xml";

  private static final String EXPECTED_ENTITY_JSON_FILE = "expectedEntityJson.json";

  private static final String EXPECTED_ENTITY_JSON_FILE_WITH_TYPE_FIELD_BOOLEAN = "expectedEntityJsonWithTypeFieldBoolean.json";

  private static final String EXPECTED_TEMPLATE = "<messageML>\n"
      + "    <div class=\"entity\">\n"
      + "        <span>Body</span>\n"
      + "    </div>\n"
      + "</messageML>\n";

  @Test
  public void testInexistentTemplateFile() throws IOException {
    MetadataParser parser = new MockMetadataParser(INEXISTENT_TEMPLATE_FILE, SIMPLE_METADATA);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    assertNull(parser.parse(node));
  }

  @Test
  public void testInexistentMetadataFile() throws IOException {
    MetadataParser parser = new MockMetadataParser(TEMPLATE, INEXISTENT_METADATA_FILE);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    assertNull(parser.parse(node));
  }

  @Test
  public void testInvalidTemplateFile() throws IOException {
    MetadataParser parser = new MockMetadataParser(INVALID_TEMPLATE_FILE, SIMPLE_METADATA);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    assertNull(parser.parse(node));
  }

  @Test
  public void testInvalidMetadataFile() throws IOException {
    MetadataParser parser = new MockMetadataParser(TEMPLATE, INVALID_METADATA_FILE);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    assertNull(parser.parse(node));
  }

  @Test
  public void testParser() throws IOException {
    MetadataParser parser = new MockMetadataParser(TEMPLATE, SIMPLE_METADATA);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    JsonNode expectedEntityJson = readJsonFromFile(EXPECTED_ENTITY_JSON_FILE);

    Message result = parser.parse(node);
    assertEquals(EXPECTED_TEMPLATE, result.getMessage());
    assertEquals(JsonUtils.writeValueAsString(expectedEntityJson), result.getData());
  }

  @Test(expected = MetadataParserException.class)
  public void testInvalidTypeInMetadataFile() throws IOException {
    MetadataParser parser = new MockMetadataParser(TEMPLATE, SIMPLE_METADATA_WITH_TYPE_FIELD_INVALID);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    assertNull(parser.parse(node));
  }

  @Test
  public void testParserWithTypeFieldBoolean() throws IOException {
    MetadataParser parser = new MockMetadataParser(TEMPLATE, SIMPLE_METADATA_WITH_TYPE_FIELD_BOOLEAN);
    parser.init();

    JsonNode node = readJsonFromFile(INPUT_FILE);
    JsonNode expectedEntityJson = readJsonFromFile(EXPECTED_ENTITY_JSON_FILE_WITH_TYPE_FIELD_BOOLEAN);

    Message result = parser.parse(node);

    assertEquals(JsonUtils.writeValueAsString(expectedEntityJson), result.getData());
  }

  private JsonNode readJsonFromFile(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return JsonUtils.readTree(classLoader.getResourceAsStream(filename));
  }

}
