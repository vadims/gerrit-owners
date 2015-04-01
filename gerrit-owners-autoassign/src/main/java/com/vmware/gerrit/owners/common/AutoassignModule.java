package com.vmware.gerrit.owners.common;

import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.AbstractModule;

public class AutoassignModule extends AbstractModule {
  @Override
  protected void configure() {
    DynamicSet.bind(binder(), GitReferenceUpdatedListener.class)
        .to(GitRefListener.class);
  }
}
