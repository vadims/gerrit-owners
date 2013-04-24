/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PathOwnersEntry {
  private String ownersPath;
  private Set<String> owners;

  public PathOwnersEntry() {
    owners = new HashSet<String>();
  }

  public String getOwnersPath() {
    return ownersPath;
  }

  public void setOwnersPath(String ownersPath) {
    this.ownersPath = ownersPath;
  }

  public Set<String> getOwners() {
    return owners;
  }

  public void addOwners(Collection<String> owners) {
    this.owners.addAll(owners);
  }
}
