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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.event.Observes;

import javax.enterprise.inject.CreationException;

import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionScoped;
import javax.transaction.TransactionSynchronizationRegistry;

import com.arjuna.ats.jta.cdi.TransactionContext;

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


  private final void afterBeanDiscovery(@Observes final AfterBeanDiscovery event) {
    if (event != null) {

      event.addBean()
        .types(TransactionManager.class)
        .scope(ApplicationScoped.class)
        .createWith(cc -> com.arjuna.ats.jta.TransactionManager.transactionManager());

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

      event.addBean()
        .types(TransactionSynchronizationRegistry.class)
        .scope(ApplicationScoped.class)
        .createWith(cc -> jtaPropertyManager.getJTAEnvironmentBean().getTransactionSynchronizationRegistry());

    }
  }

  private final void onTypeDiscovery(@Observes final ProcessAnnotatedType<? extends com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptorBase> event) {
    if (event != null &&
        event.getAnnotatedType().getBaseType().getTypeName().startsWith("com.arjuna.ats.jta.cdi.transactional.TransactionalInterceptor")) {
      // Programmatically remove the interceptors installed by the
      // stock Narayana extension.  These will trigger a JNDI
      // lookup which will fail, since we are not guaranteed the
      // presence of a JNDI implementation in the microBean
      // environment.  We replace them with similar almost-clones
      // present in this package that bypass JNDI machinery.
      // See also: https://github.com/jbosstm/narayana/pull/1344
      event.veto();
    }
  }

  private final void afterDeploymentValidation(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) throws ReflectiveOperationException {

    if (event != null && beanManager != null) {

      final Bean<?> transactionManagerBean =
        beanManager.resolve(beanManager.getBeans(TransactionManager.class));
      assert transactionManagerBean != null;
      final TransactionManager transactionManager =
        (TransactionManager)beanManager.getReference(transactionManagerBean,
                                                     TransactionManager.class,
                                                     beanManager.createCreationalContext(transactionManagerBean));
      assert transactionManager != null;

      final Bean<?> transactionSynchronizationRegistryBean = beanManager.resolve(beanManager.getBeans(TransactionSynchronizationRegistry.class));
      assert transactionSynchronizationRegistryBean != null;
      final TransactionSynchronizationRegistry transactionSynchronizationRegistry =
        (TransactionSynchronizationRegistry)beanManager.getReference(transactionSynchronizationRegistryBean,
                                                                     TransactionSynchronizationRegistry.class,
                                                                     beanManager.createCreationalContext(transactionSynchronizationRegistryBean));
      assert transactionSynchronizationRegistry != null;

      // Hack the TransactionContext class to not require JNDI.

      Field field = TransactionContext.class.getDeclaredField("transactionManager");
      assert field != null;
      assert Modifier.isStatic(field.getModifiers());
      assert Modifier.isPrivate(field.getModifiers());
      assert TransactionManager.class.equals(field.getType());
      field.setAccessible(true);
      field.set(null, transactionManager);

      field = TransactionContext.class.getDeclaredField("transactionSynchronizationRegistry");
      assert field != null;
      assert Modifier.isStatic(field.getModifiers());
      assert Modifier.isPrivate(field.getModifiers());
      assert TransactionSynchronizationRegistry.class.equals(field.getType());
      field.setAccessible(true);
      field.set(null, transactionSynchronizationRegistry);

    }

  }

}
