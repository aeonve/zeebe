/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.engine.nwe;

/**
 * Something went wrong during the workflow processing. This kind of exception should not be
 * handled.
 */
public final class BpmnProcessingException extends RuntimeException {

  private static final String CONTEXT_POSTFIX = " [context: %s]";

  /**
   * The failure message of the exception is build from the given context and the message using
   * {@link String#format(String, Object...)}.
   *
   * @param context workflow instance-related data of the element that is executed
   * @param message the failure message (including placeholders for arguments)
   * @param args arguments for the failure message
   */
  public BpmnProcessingException(
      final BpmnElementContext context, final String message, final Object... args) {
    super(String.format(message + CONTEXT_POSTFIX, args, context));
  }
}
