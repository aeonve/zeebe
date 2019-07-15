/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.gateway.impl.broker.response;

public class BrokerRejectionResponse<T> extends BrokerResponse<T> {

  private final BrokerRejection rejection;

  public BrokerRejectionResponse(BrokerRejection rejection) {
    super();
    this.rejection = rejection;
  }

  @Override
  public boolean isRejection() {
    return true;
  }

  @Override
  public BrokerRejection getRejection() {
    return rejection;
  }

  @Override
  public String toString() {
    return "BrokerRejectionResponse{" + "rejection=" + rejection + '}';
  }
}
