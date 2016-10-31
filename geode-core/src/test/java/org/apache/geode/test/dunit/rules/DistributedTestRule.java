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
import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import org.apache.geode.test.dunit.standalone.DUnitLauncher;

/**
 * Launches the DUnit framework for a {@code DistributedTest}.
 *
 * <p>
 * Enables use of {@link DistributedRule} annotations on any Rules.
 *
 * <pre>
 * {@literal @}Category(DistributedTest.class)
 * public class QueryDataDUnitTest {
 *
 *   {@literal @}DistributedRule
 *   public UseJacksonForJsonPathRule useJacksonForJsonPathRule = new UseJacksonForJsonPathRule();
 *
 *   {@literal @}Rule
 *   public DistributedTestRule distributedTestRule = DistributedTestRule.builder().build();
 *
 *   ...
 * }
 * </pre>
 * <p>
 * Use the {@code Builder} to specify which {@code VM}s should invoke any {@code Rule} annotated
 * with {@literal @}DistributedRule. By default, {@code controllerVM} is {@code true},
 * {@code everyVM} is {@code true} and {@code locatorVM} is {@code false}.
 */
public class DistributedTestRule implements MethodRule, Serializable {

  public static Builder builder() {
    return new Builder();
  }

  private TestClass testClass;

  private final List<?> rules = new ArrayList<>(); // types are TestRule or MethodRule

  private final RemoteInvoker invoker;

  private final WhichVMs whichVMs;

  // TODO: add ability to specify ordering of DistributedRules

  protected DistributedTestRule(final Builder builder) {
    this(new RemoteInvoker(), builder);
  }

  protected DistributedTestRule(final RemoteInvoker invoker, final Builder builder) {
    this.invoker = invoker;

    this.whichVMs = new WhichVMs();
    if (builder.controllerVM) {
      this.whichVMs.addControllerVM();
    }
    if (builder.everyVM) {
      this.whichVMs.addEveryVM();
    }
    if (builder.locatorVM) {
      this.whichVMs.addLocatorVM();
    }
  }

  @Override
  public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
    this.testClass = new TestClass(target.getClass());
    Statement statement = base;
    statement = withRules(method, target, statement);
    statement = withDUnit(method, target, statement);
    return statement;
  }

  protected Statement withDUnit(final FrameworkMethod method, final Object target,
      final Statement statement) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        setUpDUnit();
        try {
          statement.evaluate();
        } finally {
          tearDownDUnit();
        }
      }
    };
  }

  protected void setUpDUnit() throws Exception {
    DUnitLauncher.launchIfNeeded();
    // TODO: customize based on fields
  }

  protected void tearDownDUnit() throws Exception {}

  protected Statement withRules(final FrameworkMethod method, final Object target,
      final Statement statement) {
    List<TestRule> testRules = this.testRules(target);
    Statement result = statement;
    // result = withMethodRules(method, testRules, target, result);
    result = withTestRules(method, testRules, result);

    return result;
  }

  // protected Statement withMethodRules(final FrameworkMethod method, final List<TestRule>
  // testRules, final Object target, final Statement result) {
  // Statement statement = result;
  // for (MethodRule rule : methodRules(target)) {
  // if (!testRules.contains(rule)) {
  // statement = new DistributedStatement(rule.apply((result, method, target), this.whichVMs);
  // }
  // }
  // return statement;
  // }

  protected Statement withTestRules(final FrameworkMethod method, final List<TestRule> testRules,
      final Statement statement) {
    Description description = Description.createTestDescription(this.testClass.getJavaClass(),
        method.getName(), method.getAnnotations());
    return testRules.isEmpty() ? statement
        : new DistributedRunRules(statement, testRules, description, this.whichVMs);
  }

  protected List<MethodRule> methodRules(final Object target) {
    List<MethodRule> rules =
        this.testClass.getAnnotatedMethodValues(target, DistributedRule.class, MethodRule.class);
    rules.addAll(
        this.testClass.getAnnotatedFieldValues(target, DistributedRule.class, MethodRule.class));
    return rules;
  }

  protected List<TestRule> testRules(final Object target) {
    List<TestRule> result =
        this.testClass.getAnnotatedMethodValues(target, DistributedRule.class, TestRule.class);
    result.addAll(
        this.testClass.getAnnotatedFieldValues(target, DistributedRule.class, TestRule.class));
    return result;
  }

  /**
   * Builds an instance of {@link DistributedTestRule}.
   *
   * <p>
   * By default, {@code controllerVM} is {@code true}, {@code everyVM} is {@code true} and
   * {@code locatorVM} is {@code false}.
   */
  public static class Builder {

    private boolean everyVM = true;
    private boolean locatorVM = false;
    private boolean controllerVM = true;

    protected Builder() {}

    public Builder everyVM(final boolean everyVM) {
      this.everyVM = everyVM;
      return this;
    }

    public Builder locatorVM(final boolean locatorVM) {
      this.locatorVM = locatorVM;
      return this;
    }

    public Builder controllerVM(final boolean locatorVM) {
      this.locatorVM = locatorVM;
      return this;
    }

    public DistributedTestRule build() {
      return new DistributedTestRule(this);
    }
  }

}
