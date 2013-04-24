/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners;


import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.rules.PredicateProvider;

@Listen
public class OwnerPredicateProvider implements PredicateProvider {
  @Override
  public ImmutableSet<String> getPackages() {
    return ImmutableSet.of("gerrit_owners");
  }
}
