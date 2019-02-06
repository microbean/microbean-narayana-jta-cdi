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

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.event.Event;

import javax.inject.Inject;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager; // for javadoc only

/**
 * A {@link DelegatingTransactionManager} in {@linkplain
 * ApplicationScoped application scope} that uses the return value
 * that results from invoking the {@link
 * com.arjuna.ats.jta.TransactionManager#transactionManager()} method
 * as its backing implementation.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see com.arjuna.ats.jta.TransactionManager#transactionManager()
 */
@ApplicationScoped
public class NarayanaTransactionManager extends DelegatingTransactionManager {

  private final Event<Transaction> broadcaster;

  /**
   * Creates a new {@link NarayanaTransactionManager}.
   *
   * @param broadcaster an {@link Event} capable of {@linkplain
   * Event#fire(Object) firing} {@link Transaction} instances; may be
   * {@code null}
   *
   * @see #begin()
   */
  @Inject
  public NarayanaTransactionManager(final Event<Transaction> broadcaster) {
    super(com.arjuna.ats.jta.TransactionManager.transactionManager());
    this.broadcaster = broadcaster;
  }

  /**
   * Overrides the {@link DelegatingTransactionManager#begin()} method
   * to additionally {@linkplain Event#fire(Object) fire} the return
   * value of the {@link #getTransaction()} method immediately after a
   * transaction is begun.
   *
   * @exception NotSupportedException if the thread is already
   * associated with a transaction and this {@link TransactionManager}
   * implementation does not support nested transactions
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   *
   * @see DelegatingTransactionManager#begin()
   *
   * @see Transaction
   */
  @Override
  public void begin() throws NotSupportedException, SystemException {
    super.begin();
    if (this.broadcaster != null) {
      final Transaction transaction = this.getTransaction();
      assert transaction != null;
      this.broadcaster.fire(transaction);
    }
  }

}
