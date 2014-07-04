/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners;

import com.vmware.gerrit.owners.common.PathOwners;

import com.google.gerrit.rules.PrologEnvironment;
import com.google.gerrit.rules.StoredValue;
import com.google.gerrit.rules.StoredValues;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gerrit.server.patch.PatchList;
import com.google.gwtorm.server.OrmException;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.SystemException;
import org.eclipse.jgit.lib.Repository;

/**
 * StoredValues for the Gerrit OWNERS plugin.
 */
public class OwnersStoredValues {

  public static StoredValue<PathOwners> PATH_OWNERS;

  synchronized
  public static void initialize(final AccountResolver resolver) {
    if (PATH_OWNERS != null) {
      return;
    }
    PATH_OWNERS = new StoredValue<PathOwners>() {
      @Override
      protected PathOwners createValue(Prolog engine) {
        PatchList patchList = StoredValues.PATCH_LIST.get(engine);
        Repository repository = StoredValues.REPOSITORY.get(engine);

        PrologEnvironment env = (PrologEnvironment) engine.control;

        try {
          return new PathOwners(resolver, repository, patchList);
        } catch (OrmException e) {
          throw new SystemException(e.getMessage());
        }
      }
    };
  }

  private OwnersStoredValues() {
  }
}
