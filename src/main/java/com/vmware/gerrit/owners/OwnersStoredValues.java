/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners;

import com.google.gerrit.rules.PrologEnvironment;
import com.google.gerrit.rules.StoredValue;
import com.google.gerrit.rules.StoredValues;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gerrit.server.patch.PatchList;
import com.googlecode.prolog_cafe.lang.Prolog;
import org.eclipse.jgit.lib.Repository;

/**
 * StoredValues for the Gerrit OWNERS plugin.
 */
public class OwnersStoredValues {

  public static StoredValue<PathOwners> PATH_OWNERS = new StoredValue<PathOwners>() {
    @Override
    protected PathOwners createValue(Prolog engine) {
      PatchList patchList = StoredValues.PATCH_LIST.get(engine);
      Repository repository = StoredValues.REPOSITORY.get(engine);

      PrologEnvironment env = (PrologEnvironment) engine.control;
      AccountResolver resolver = env.getInjector().getInstance(AccountResolver.class);

      return new PathOwners(repository, resolver, patchList);
    }
  };

  private OwnersStoredValues() {
  }
}
