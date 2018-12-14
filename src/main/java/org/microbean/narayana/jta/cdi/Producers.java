/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2018 microBean.
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

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.inject.Produces;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.common.jtaPropertyManager;

/**
 * A class housing various producer methods.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
@ApplicationScoped
final class Producers {


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Producers}.
   */
  private Producers() {
    super();
  }


  /*
   * Static methods.
   */


  /**
   * Produces a {@link TransactionManager} in {@linkplain
   * ApplicationScoped application scope} by returning the return
   * value of invoking {@link
   * com.arjuna.ats.jta.TransactionManager#transactionManager()}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a {@link TransactionManager}; never {@code null}
   *
   * @see com.arjuna.ats.jta.TransactionManager#transactionManager()
   */
  @Produces
  @ApplicationScoped
  private static final TransactionManager produceTransactionManager() {
    return com.arjuna.ats.jta.TransactionManager.transactionManager();
  }

  /**
   * Produces a {@link TransactionSynchronizationRegistry} in
   * {@linkplain ApplicationScoped application scope} by returning the
   * return value of invoking the {@link
   * com.arjuna.ats.jta.common.JTAEnvironmentBean#getTransactionSynchronizationRegistry()}
   * method on the return value of invoking {@link
   * com.arjuna.ats.jta.common.jtaPropertyManager#getJTAEnvironmentBean()}.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a {@link TransactionManager}; never {@code null}
   *
   * @see com.arjuna.ats.jta.common.jtaPropertyManager#getJTAEnvironmentBean()
   *
   * @see com.arjuna.ats.jta.common.JTAEnvironmentBean#getTransactionSynchronizationRegistry()
   */
  @Produces
  @ApplicationScoped
  private static final TransactionSynchronizationRegistry produceTransactionSynchronizationRegistry() {
    return jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry();
  }
  
}
