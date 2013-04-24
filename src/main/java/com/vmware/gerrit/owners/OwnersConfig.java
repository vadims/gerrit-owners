/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners;

import java.util.Set;

/**
 * OWNERS file model.
 * <p/>
 * Used for de-serializing the OWNERS files.
 */
public class OwnersConfig {
  /**
   * Flag for marking that this OWNERS file inherits from the parent OWNERS.
   */
  private boolean inherited = true;

  /**
   * Set of OWNER email addresses.
   */
  private Set<String> owners;

  public boolean isInherited() {
    return inherited;
  }

  public void setInherited(boolean inherited) {
    this.inherited = inherited;
  }

  public Set<String> getOwners() {
    return owners;
  }

  public void setOwners(Set<String> owners) {
    this.owners = owners;
  }
}
