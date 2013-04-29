/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners.autoassign;

public class ReviewerManagerException extends Exception {
  public ReviewerManagerException(String message) {
    super(message);
  }

  public ReviewerManagerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReviewerManagerException(Throwable cause) {
    super(cause);
  }
}
