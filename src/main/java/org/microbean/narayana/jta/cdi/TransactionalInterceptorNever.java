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

import javax.annotation.Priority;

import javax.inject.Inject;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import javax.transaction.InvalidTransactionException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;

import com.arjuna.ats.jta.logging.jtaLogger;

@Interceptor
@Transactional(Transactional.TxType.NEVER)
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 200)
final class TransactionalInterceptorNever extends TransactionalInterceptorBase {

  private static final long serialVersionUID = 1L;

  @Inject
  TransactionalInterceptorNever(final TransactionManager transactionManager) {
    super(transactionManager,true);
  }

  @AroundInvoke
  @Override
  public final Object intercept(final InvocationContext invocationContext) throws Exception {
    return super.intercept(invocationContext);
  }

  @Override
  protected final Object doIntercept(final TransactionManager transactionManager,
                                     final Transaction transaction,
                                     final InvocationContext ic)
    throws Exception {
    if (transaction != null) {
      throw new TransactionalException(jtaLogger.i18NLogger.get_tx_required(), new InvalidTransactionException());
    }
    return invokeInNoTx(ic);
  }
  
}
