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

import org.junit.runners.model.Statement;

import org.apache.geode.test.dunit.Host;
import org.apache.geode.test.dunit.SerializableRunnable;
import org.apache.geode.test.junit.rules.serializable.SerializableStatement;

/**
 * Invokes Statement in specified DUnit VMs.
 */
public class DistributedStatement extends SerializableStatement {
  private final SerializableStatement next;
  private final WhichVMs whichVMs;

  /**
   * Construct a new {@code DistributedStatement} statement.
   * 
   * @param next the next {@code Statement} in the execution chain
   * @param whichVMs specifies which VMs should invoke the statement
   */
  public DistributedStatement(final SerializableStatement next, final WhichVMs whichVMs) {
    this.next = next;
    this.whichVMs = whichVMs;
  }

  /**
   * Invoke the {@link Statement} in the specified VMs.
   */
  @Override
  public void evaluate() throws Throwable {
    if (this.whichVMs.controllerVM()) {
      this.next.evaluate();
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

  private SerializableRunnable runnable() {
    return new SerializableRunnable() {
      @Override
      public void run() {
        try {
          next.evaluate();
        } catch (Error | RuntimeException e) {
          throw e;
        } catch (Throwable t) {
          throw new RuntimeException(t);
        }
      }
    };
  }
}
