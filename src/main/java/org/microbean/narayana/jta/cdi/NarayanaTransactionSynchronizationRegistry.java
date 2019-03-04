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

import javax.inject.Inject;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

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
class NarayanaTransactionSynchronizationRegistry extends DelegatingTransactionSynchronizationRegistry {

  /**
   * Creates a new, <strong>nonfunctional</strong> {@link
   * NarayanaTransactionSynchronizationRegistry}.
   *
   * <p>This constructor exists only to conform with section 3.15 of
   * the CDI specification.</p>
   *
   * @deprecated This constructor exists only to conform with section
   * 3.15 of the CDI specification; please use the {@link
   * #NarayanaTransactionSynchronizationRegistry(JTAEnvironmentBean)}
   * constructor instead.
   *
   * @see #NarayanaTransactionSynchronizationRegistry(JTAEnvironmentBean)
   *
   * @see <a
   * href="http://docs.jboss.org/cdi/spec/1.2/cdi-spec.html#unproxyable">Section
   * 3.15 of the CDI 2.0 specification</a>
   */
  @Deprecated
  NarayanaTransactionSynchronizationRegistry() {
    this(null);
  }
  
  /**
   * Creates a new {@link NarayanaTransactionSynchronizationRegistry}.
   *
   * @see JTAEnvironmentBean#getTransactionSynchronizationRegistry()
   */
  @Inject
  private NarayanaTransactionSynchronizationRegistry(final JTAEnvironmentBean jtaEnvironmentBean) {
    super(jtaEnvironmentBean == null ? null : jtaEnvironmentBean.getTransactionSynchronizationRegistry());
  }
  
}
