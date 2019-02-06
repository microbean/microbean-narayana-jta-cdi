/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2019 microBean.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.narayana.jta.cdi;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.arjuna.ats.jta.common.JTAEnvironmentBean; // for javadoc only

/**
 * A {@link DelegatingTransactionSynchronizationRegistry} in
 * {@linkplain ApplicationScoped application scope} that uses the
 * return value that results from invoking the {@link
 * JTAEnvironmentBean#getTransactionSynchronizationRegistry()} method
 * as its backing implementation.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see JTAEnvironmentBean#getTransactionSynchronizationRegistry()
 */
@ApplicationScoped
public class NarayanaTransactionSynchronizationRegistry extends DelegatingTransactionSynchronizationRegistry {

  /**
   * Creates a new {@link NarayanaTransactionSynchronizationRegistry}.
   *
   * @see JTAEnvironmentBean#getTransactionSynchronizationRegistry()
   */
  public NarayanaTransactionSynchronizationRegistry() {
    super(jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry());
  }
  
}
