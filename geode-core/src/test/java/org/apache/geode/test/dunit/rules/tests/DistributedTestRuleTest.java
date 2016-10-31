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
package org.apache.geode.test.dunit.rules.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.test.dunit.rules.DistributedRule;
import org.apache.geode.test.dunit.rules.DistributedTestRule;
import org.apache.geode.test.junit.categories.DistributedTest;
import org.apache.geode.test.junit.rules.serializable.SerializableExternalResource;

// TODO: add assertions to all dunit VMs
@Category(DistributedTest.class)
public class DistributedTestRuleTest {

  @DistributedRule
  public SimpleRule simpleRule = new SimpleRule();

  @Rule
  public DistributedTestRule distributedTestRule = DistributedTestRule.builder().build();

  @Test
  public void test() throws Exception {
    System.out.println("KIRK:test");
  }

  private static class SimpleRule extends SerializableExternalResource {
    @Override
    protected void before() throws Throwable {
      System.out.println("KIRK:before");
    }

    @Override
    protected void after() {
      System.out.println("KIRK:after");
    }
  }

}
