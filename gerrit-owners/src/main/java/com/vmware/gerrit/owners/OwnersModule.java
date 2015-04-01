package com.vmware.gerrit.owners;

import com.google.gerrit.rules.PredicateProvider;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;


public class OwnersModule extends AbstractModule {
  @Override
  protected void configure() {
    DynamicSet.bind(binder(), PredicateProvider.class)
        .to(OwnerPredicateProvider.class);
  }
}
