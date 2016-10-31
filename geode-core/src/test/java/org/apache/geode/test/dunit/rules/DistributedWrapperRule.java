/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.dunit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.apache.geode.test.junit.rules.UseJacksonForJsonPathRule;
import org.apache.geode.test.junit.rules.serializable.SerializableExternalResource;
import org.apache.geode.test.junit.rules.serializable.SerializableStatement;
import org.apache.geode.test.junit.rules.serializable.SerializableTestRule;

public class DistributedWrapperRule implements SerializableTestRule {

  private static SerializableTestRule instance;

  private final RemoteInvoker invoker;
  private final WhichVMs whichVMs;

  public DistributedWrapperRule(final SerializableTestRule testRule) {
    this(testRule, new WhichVMs().addControllerVM().addEveryVM());
  }

  public DistributedWrapperRule(final SerializableTestRule testRule, final WhichVMs whichVMs) {
    this(new RemoteInvoker(), testRule, whichVMs);
  }

  public DistributedWrapperRule(final RemoteInvoker invoker, final SerializableTestRule testRule,
      final WhichVMs whichVMs) {
    this.invoker = invoker;
    instance = testRule;
    this.whichVMs = whichVMs;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new DistributedStatement((SerializableStatement) base, whichVMs);
  }
}
