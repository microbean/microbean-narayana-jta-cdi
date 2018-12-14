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

import javax.enterprise.event.Observes;

import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Produces;

import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionScoped;
import javax.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.common.jtaPropertyManager;

/**
 * A <a
 * href="http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#spi">CDI 2.0
 * portable extension</a> that adapts the <a
 * href="https://narayana.io/">Narayana transaction engine</a> to a <a
 * href="http://docs.jboss.org/cdi/spec/2.0/cdi-spec.html#part_2">CDI
 * 2.0 SE environment</a>.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public final class NarayanaExtension implements Extension {


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link NarayanaExtension}.
   */
  public NarayanaExtension() {
    super();
  }


  /*
   * Instance methods.
   */


  /**
   * Adds a synthetic bean that creates a {@link Transaction} in
   * {@linkplain TransactionScoped transaction scope}.
   *
   * @param event the {@link AfterBeanDiscovery} event fired by the
   * CDI container; may be {@code null} in which case no action will
   * be taken
   */
  private final void afterBeanDiscovery(@Observes final AfterBeanDiscovery event) {
    if (event != null) {

      event.addBean()
        .types(Transaction.class)
        .scope(TransactionScoped.class)
        .createWith(cc -> {
            try {
              return CDI.current().select(TransactionManager.class).get().getTransaction();
            } catch (final SystemException systemException) {
              throw new CreationException(systemException.getMessage(), systemException);
            }
          });

    }
  }

}
