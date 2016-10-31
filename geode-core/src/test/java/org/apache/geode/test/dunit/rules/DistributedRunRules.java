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

import java.io.Serializable;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.apache.geode.test.dunit.Host;
import org.apache.geode.test.dunit.SerializableRunnable;

/**
 * Runs Rules in specified DUnit VMs.
 */
public class DistributedRunRules extends Statement implements Serializable {
  private final Statement statement;
  private final WhichVMs whichVMs;

  public DistributedRunRules(final Statement base, final Iterable<TestRule> rules,
      final Description description, final WhichVMs whichVMs) {
    this.statement = applyAll(base, rules, description);
    this.whichVMs = whichVMs;
  }

  @Override
  public void evaluate() throws Throwable {
    if (this.whichVMs.controllerVM()) {
      this.statement.evaluate();
    }
    if (this.whichVMs.everyVM()) {
      for (int i = 0; i < Host.getHost(0).getVMCount(); i++) {
        Host.getHost(0).getVM(i).invoke(runnable());
      }
    }
    if (this.whichVMs.locatorVM()) {
      Host.getHost(0).getLocator().invoke(runnable());
    }
  }

  private Statement applyAll(Statement result, final Iterable<TestRule> rules,
      final Description description) {
    for (TestRule each : rules) {
      result = each.apply(result, description);
    }
    return result;
  }

  private SerializableRunnable runnable() {
    return new SerializableRunnable() {
      @Override
      public void run() {
        try {
          DistributedRunRules.this.statement.evaluate();
        } catch (Error | RuntimeException e) {
          throw e;
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
    };
  }
}
