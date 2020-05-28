/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.engine.nwe.behavior;

import io.zeebe.engine.nwe.BpmnElementContext;
import io.zeebe.engine.nwe.BpmnProcessingException;
import io.zeebe.engine.processor.TypedResponseWriter;
import io.zeebe.engine.state.ZeebeState;
import io.zeebe.engine.state.instance.AwaitWorkflowInstanceResultMetadata;
import io.zeebe.engine.state.instance.ElementInstanceState;
import io.zeebe.engine.state.instance.VariablesState;
import io.zeebe.protocol.impl.record.value.workflowinstance.WorkflowInstanceResultRecord;
import io.zeebe.protocol.record.ValueType;
import io.zeebe.protocol.record.intent.WorkflowInstanceResultIntent;
import io.zeebe.protocol.record.value.BpmnElementType;
import io.zeebe.util.buffer.BufferUtil;
import java.util.HashSet;
import java.util.Set;
import org.agrona.DirectBuffer;

public final class BpmnWorkflowResultSenderBehavior {

  final WorkflowInstanceResultRecord resultRecord = new WorkflowInstanceResultRecord();

  private final ElementInstanceState elementInstanceState;
  private final VariablesState variablesState;
  private final TypedResponseWriter responseWriter;

  public BpmnWorkflowResultSenderBehavior(
      final ZeebeState zeebeState, final TypedResponseWriter responseWriter) {
    elementInstanceState = zeebeState.getWorkflowState().getElementInstanceState();
    variablesState = elementInstanceState.getVariablesState();
    this.responseWriter = responseWriter;
  }

  public void sendResult(final BpmnElementContext context) {

    if (context.getBpmnElementType() != BpmnElementType.PROCESS) {
      throw new BpmnProcessingException(
          context,
          "Expected to send the result of the workflow instance but was not called from the process element");
    }

    final AwaitWorkflowInstanceResultMetadata requestMetadata =
        elementInstanceState.getAwaitResultRequestMetadata(context.getWorkflowInstanceKey());

    if (requestMetadata != null) {
      sendResult(context, requestMetadata);
    }
  }

  private void sendResult(
      final BpmnElementContext context, final AwaitWorkflowInstanceResultMetadata requestMetadata) {

    final DirectBuffer variablesAsDocument = collectVariables(context, requestMetadata);

    resultRecord
        .setWorkflowInstanceKey(context.getWorkflowInstanceKey())
        .setWorkflowKey(context.getWorkflowKey())
        .setBpmnProcessId(context.getBpmnProcessId())
        .setVersion(context.getWorkflowVersion())
        .setVariables(variablesAsDocument);

    responseWriter.writeResponse(
        context.getWorkflowInstanceKey(),
        WorkflowInstanceResultIntent.COMPLETED,
        resultRecord,
        ValueType.WORKFLOW_INSTANCE_RESULT,
        requestMetadata.getRequestId(),
        requestMetadata.getRequestStreamId());

    responseWriter.flush();
  }

  private DirectBuffer collectVariables(
      final BpmnElementContext context, final AwaitWorkflowInstanceResultMetadata requestMetadata) {

    final Set<DirectBuffer> variablesToCollect = new HashSet<>();
    if (requestMetadata.fetchVariables().iterator().hasNext()) {
      requestMetadata
          .fetchVariables()
          .forEach(
              variable -> {
                final DirectBuffer name = BufferUtil.cloneBuffer(variable.getValue());
                variablesToCollect.add(name);
              });

      return variablesState.getVariablesAsDocument(
          context.getWorkflowInstanceKey(), variablesToCollect);

    } else {
      // collect all workflow instance variables
      return variablesState.getVariablesAsDocument(context.getWorkflowInstanceKey());
    }
  }
}
