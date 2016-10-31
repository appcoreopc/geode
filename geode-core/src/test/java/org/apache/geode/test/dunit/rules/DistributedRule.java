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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Rule;

/**
 * Annotates a field or method as a type of {@link Rule} that can be invoked across multiple VMs in
 * a {@code DistributedTest}.
 *
 * If there are multiple annotated {@code DistributedRule}s on a class, they will be applied in
 * order of fields first, then methods. Furthermore, if there are multiple fields (or methods) they
 * will be applied in an order that depends on your JVM's implementation of the reflection API,
 * which is undefined. Rules defined by fields will always be applied before Rules defined by
 * methods. You can use a {@link org.junit.rules.RuleChain} or
 * {@link org.apache.geode.test.junit.rules.RuleList} if you want to have control over the order in
 * which the Rules are applied.
 *
 * <p>
 * For example, here is a test class that makes a unique {@link org.junit.rules.TemporaryFolder}
 * available to each DUnit VM:
 * 
 * <pre>
 * {@literal @}Category(DistributedTest.class)
 * public class EachVMHasItsOwnTemporaryFolder {
 *
 *   {@literal @}DistributedRule
 *   public TemporaryFolder folder = new TemporaryFolder();
 *
 *   {@literal @}Rule
 *   public DistributedTestRule distributedTestRule = DistributedTestRule.builder().build();
 *
 *   {@literal @}Test
 *   public void eachVMHasItsOwnTemporaryFolder() throws Exception {
 *     Host.getHost(0).getVM(0).invoke(() -> {
 *       File gemfireProps = folder.newFile({@literal "}gemfire.properties{@literal "});
 *       File diskDirs = folder.newFolder({@literal "}diskDirs{@literal "});
 *       ...
 *     }
 *   }
 * }
 * </pre>
 *
 * @see org.apache.geode.test.junit.rules.serializable.SerializableTestRule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DistributedRule {
}
