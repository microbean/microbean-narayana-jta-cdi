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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status; // for javadoc only
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * An {@code abstract} {@link TransactionManager} implementation that
 * delegates all method invocations to another {@link
 * TransactionManager}.
 *
 * @author <a href="https://about.me/lairdnelson" target="_parent">Laird Nelson</a>
 *
 * @see TransactionManager
 */
public abstract class DelegatingTransactionManager implements TransactionManager {

  private final TransactionManager delegate;

  /**
   * Creates a new, <strong>nonfunctional</strong> {@link
   * DelegatingTransactionManager}.
   *
   * <p>This constructor exists only to conform with section 3.11 of
   * the CDI specification.</p>
   *
   * @deprecated This constructor exists only to conform with section
   * 3.11 of the CDI specification; please use the {@link
   * #DelegatingTransactionManager(TransactionManager)} constructor
   * instead.
   */
  @Deprecated
  protected DelegatingTransactionManager() {
    super();
    this.delegate = null;
  }
  
  /**
   * Creates a new {@link DelegatingTransactionManager}.
   *
   * @param delegate the {@link TransactionManager} to which all
   * method invocations will be delegated; must not be {@code null}
   *
   * @exception NullPointerException if {@code delegate} is {@code null}
   */
  protected DelegatingTransactionManager(final TransactionManager delegate) {
    super();
    this.delegate = Objects.requireNonNull(delegate);
  }

  /**
   * Creates a new transaction and associates it with the current thread.
   *
   * @exception NotSupportedException if the thread is already
   * associated with a transaction and this {@link TransactionManager}
   * implementation does not support nested transactions
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public void begin() throws NotSupportedException, SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.begin();
  }

  /**
   * Completes the transaction associated with the current thread.
   *
   * <p>When this method completes, the thread is no longer associated
   * with a transaction.</p>
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
   */
  @Override
  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.commit();
  }

  /**
   * Returns the status of the transaction associated with the current
   * thread.
   *
   * @return the transaction status expressed as the value of one of
   * the {@code int} constants in the {@link Status} class; if no
   * transaction is associated with the current thread, this method
   * returns {@link Status#STATUS_NO_TRANSACTION}
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   *
   * @see Status
   */
  @Override
  public int getStatus() throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    return this.delegate.getStatus();
  }

  /**
   * Returns the {@link Transaction} object that represents the
   * transaction context of the calling thread.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return the {@link Transaction} object representing the
   * transaction associated with the calling thread; never {@code
   * null}
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public Transaction getTransaction() throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    return this.delegate.getTransaction();
  }

  /**
   * Resumes the transaction context association of the calling thread
   * with the transaction represented by the supplied {@link
   * Transaction} object.
   *
   * <p>When this method returns, the calling thread is associated
   * with the transaction context specified.</p>
   *
   * @param transaction the {@link Transaction} representing the
   * transaction to be resumed; must not be {@code null}
   *
   * @exception InvalidTransactionException if {@code transaction} is
   * invalid
   *
   * @exception IllegalStateException if the thread is already
   * associated with another transaction
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public void resume(final Transaction transaction) throws InvalidTransactionException, SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.resume(transaction);
  }

  /**
   * Rolls back the transaction associated with the current thread.
   *
   * <p>When this method completes, the thread is no longer associated
   * with a transaction.</p>
   *
   * @exception SecurityException if the thread is not allowed to roll
   * back the transaction
   *
   * @exception IllegalStateException if the current thread is not
   * associated with a transaction
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public void rollback() throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.rollback();
  }

  /**
   * Irrevocably modifies the transaction associated with the current
   * thread such that the only possible outcome is for it to
   * {@linkplain #rollback() roll back}.
   *
   * @exception IllegalStateException if the current thread is not
   * associated with a transaction
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public void setRollbackOnly() throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.setRollbackOnly();
  }

  /**
   * Sets the timeout value that is associated with transactions
   * started by the current thread with the {@link #begin()} method.
   *
   * <p>If an application has not called this method, the transaction
   * service uses some default value for the transaction timeout.</p>
   *
   * @param seconds the timeout in seconds; if the value is zero, the
   * transaction service restores the default value; if the value is
   * negative a {@link SystemException} is thrown
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition or if {@code seconds} is
   * less than zero
   */
  @Override
  public void setTransactionTimeout(final int seconds) throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    this.delegate.setTransactionTimeout(seconds);
  }

  /**
   * Suspends the transaction currently associated with the calling
   * thread and returns a {@link Transaction} that represents the
   * transaction context being suspended, or {@code null} if the
   * calling thread is not associated with a transaction.
   *
   * <p>This method may return {@code null}.</p>
   *
   * <p>When this method returns, the calling thread is no longer
   * associated with a transaction.</p>
   *
   * @return a {@link Transaction} representing the suspended
   * transaction, or {@code null}
   *
   * @exception SystemException if this {@link TransactionManager}
   * encounters an unexpected error condition
   */
  @Override
  public Transaction suspend() throws SystemException {
    if (this.delegate == null) {
      throw new SystemException("delegate == null");
    }
    return this.delegate.suspend();
  }

}
