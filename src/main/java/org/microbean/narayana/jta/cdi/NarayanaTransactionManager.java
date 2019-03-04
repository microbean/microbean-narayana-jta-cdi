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
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;

import javax.enterprise.event.Event;

import javax.inject.Inject;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager; // for javadoc only
import javax.transaction.TransactionScoped;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * A {@link DelegatingTransactionManager} in {@linkplain
 * ApplicationScoped application scope} that uses the return value
 * that results from invoking the {@link
 * JTAEnvironmentBean#getTransactionManager()} method as its backing
 * implementation.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see com.arjuna.ats.jta.common.JTAEnvironmentBean#getTransactionManager()
 */
@ApplicationScoped
class NarayanaTransactionManager extends DelegatingTransactionManager {

  private final Event<Transaction> transactionScopeInitializedBroadcaster;

  private final Event<Object> transactionScopeDestroyedBroadcaster;

  /**
   * Creates a new, <strong>nonfunctional</strong> {@link
   * NarayanaTransactionManager}.
   *
   * <p>This constructor exists only to conform with section 3.15 of
   * the CDI specification.</p>
   *
   * @deprecated This constructor exists only to conform with section
   * 3.15 of the CDI specification; please use the {@link
   * #NarayanaTransactionManager(JTAEnvironmentBean, Event, Event)}
   * constructor instead.
   *
   * @see #NarayanaTransactionManager(JTAEnvironmentBean, Event,
   * Event)
   *
   * @see <a
   * href="http://docs.jboss.org/cdi/spec/1.2/cdi-spec.html#unproxyable">Section
   * 3.15 of the CDI 2.0 specification</a>
   */
  @Deprecated
  NarayanaTransactionManager() {
    this(null, null, null);
  }
  
  /**
   * Creates a new {@link NarayanaTransactionManager}.
   *
   * @param jtaEnvironmentBean a {@link JTAEnvironmentBean} used to
   * acquire this {@link NarayanaTransactionManager}'s delegate; may
   * be {@code null} but then a {@link SystemException} will be thrown
   * by every method in this class when invoked
   *
   * @param transactionScopeInitializedBroadcaster an {@link Event}
   * capable of {@linkplain Event#fire(Object) firing} {@link
   * Transaction} instances; may be {@code null}
   *
   * @param transactionScopeDestroyedBroadcaster an {@link Event}
   * capable of {@linkplain Event#fire(Object) firing} {@link Object}
   * instances; may be {@code null}
   *
   * @see #begin()
   *
   * @see #commit()
   *
   * @see #rollback()
   */
  @Inject
  private NarayanaTransactionManager(final JTAEnvironmentBean jtaEnvironmentBean,
                                     @Initialized(TransactionScoped.class) final Event<Transaction> transactionScopeInitializedBroadcaster,
                                     @Destroyed(TransactionScoped.class) final Event<Object> transactionScopeDestroyedBroadcaster) {
    super(jtaEnvironmentBean == null ? null : jtaEnvironmentBean.getTransactionManager());
    this.transactionScopeInitializedBroadcaster = transactionScopeInitializedBroadcaster;
    this.transactionScopeDestroyedBroadcaster = transactionScopeDestroyedBroadcaster;
  }

  /**
   * Overrides {@link DelegatingTransactionManager#begin()} to
   * additionally {@linkplain Event#fire(Object) fire} an {@link
   * Object} representing the {@linkplain Initialized initialization}
   * of the {@linkplain TransactionScoped transaction scope}.
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
   * @see Event#fire(Object)
   *
   * @see Initialized
   *
   * @see TransactionScoped
   */
  @Override
  public void begin() throws NotSupportedException, SystemException {
    super.begin();
    if (this.transactionScopeInitializedBroadcaster != null) {
      this.transactionScopeInitializedBroadcaster.fire(this.getTransaction());
    }
  }

  /**
   * Overrides {@link DelegatingTransactionManager#commit()} to
   * additionally {@linkplain Event#fire(Object) fire} an {@link
   * Object} representing the {@linkplain Destroyed destruction}
   * of the {@linkplain TransactionScoped transaction scope}.
   *
   * @exception RollbackException if the transaction has been rolled
   * back rather than committed
   *
   * @exception HeuristicMixedException if a heuristic decision was
   * made and that some relevant updates have been committed while
   * others have been rolled back
   *
   * @exception HeuristicRollbackException if a heuristic decision was
   * made and all relevant updates have been rolled back
   *
   * @exception SecurityException if the thread is not allowed to
   * commit the transaction
   *
   * @exception IllegalStateException if the current thread is not
   * associated with a transaction
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   *
   * @see DelegatingTransactionManager#commit()
   *
   * @see Event#fire(Object)
   *
   * @see Destroyed
   *
   * @see TransactionScoped
   */
  @Override
  public void commit() throws HeuristicMixedException, HeuristicRollbackException, RollbackException, SystemException {
    try {
      super.commit();
    } finally {
      if (this.transactionScopeDestroyedBroadcaster != null) {
        this.transactionScopeDestroyedBroadcaster.fire(this.toString());
      }
    }
  }

  /**
   * Overrides {@link DelegatingTransactionManager#rollback()} to
   * additionally {@linkplain Event#fire(Object) fire} an {@link
   * Object} representing the {@linkplain Destroyed destruction}
   * of the {@linkplain TransactionScoped transaction scope}.
   *
   * @exception SecurityException if the thread is not allowed to roll
   * back the transaction
   *
   * @exception IllegalStateException if the current thread is not
   * associated with a transaction
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   *
   * @see DelegatingTransactionManager#rollback()
   *
   * @see Event#fire(Object)
   *
   * @see Destroyed
   *
   * @see TransactionScoped
   */
  @Override
  public void rollback() throws SystemException {
    try {
      super.rollback();
    } finally {
      if (this.transactionScopeDestroyedBroadcaster != null) {
        this.transactionScopeDestroyedBroadcaster.fire(this.toString());
      }
    }
  }

}