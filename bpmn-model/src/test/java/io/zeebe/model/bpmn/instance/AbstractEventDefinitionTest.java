/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.model.bpmn.instance;

import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.Query;
import io.zeebe.model.bpmn.impl.QueryImpl;
import java.io.InputStream;
import java.util.Collection;
import org.camunda.bpm.model.xml.impl.util.ReflectUtil;
import org.junit.Before;

public abstract class AbstractEventDefinitionTest extends BpmnModelElementInstanceTest {

  protected Query<EventDefinition> eventDefinitionQuery;

  @Override
  public TypeAssumption getTypeAssumption() {
    return new TypeAssumption(EventDefinition.class, false);
  }

  @Override
  public Collection<ChildElementAssumption> getChildElementAssumptions() {
    return null;
  }

  @Override
  public Collection<AttributeAssumption> getAttributesAssumptions() {
    return null;
  }

  @Before
  public void getEvent() {
    final InputStream inputStream =
        ReflectUtil.getResourceAsStream("io/zeebe/model/bpmn/EventDefinitionsTest.xml");
    final IntermediateThrowEvent event =
        Bpmn.readModelFromStream(inputStream).getModelElementById("event");
    eventDefinitionQuery = new QueryImpl<>(event.getEventDefinitions());
  }
}
