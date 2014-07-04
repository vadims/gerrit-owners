/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners.common;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.events.GitReferenceUpdatedListener;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.account.AccountResolver;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.patch.PatchList;
import com.google.gerrit.server.patch.PatchListCache;
import com.google.gerrit.server.patch.PatchListKey;
import com.google.gerrit.server.patch.PatchListNotAvailableException;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Provider;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static com.google.gerrit.reviewdb.client.AccountDiffPreference.Whitespace.IGNORE_NONE;

@Listen
public class GitRefListener implements GitReferenceUpdatedListener {
  private static final Logger logger = LoggerFactory.getLogger(GitRefListener.class);

  private static final String CHANGES_REF = "refs/changes/";

  private final Provider<ReviewDb> db;

  private final PatchListCache patchListCache;

  private final GitRepositoryManager repositoryManager;

  private final AccountResolver accountResolver;

  private final ReviewerManager reviewerManager;

  @Inject
  public GitRefListener(Provider<ReviewDb> db,
                        PatchListCache patchListCache,
                        GitRepositoryManager repositoryManager,
                        AccountResolver accountResolver,
                        ReviewerManager reviewerManager) {
    this.db = db;
    this.patchListCache = patchListCache;
    this.repositoryManager = repositoryManager;
    this.accountResolver = accountResolver;
    this.reviewerManager = reviewerManager;
  }

  @Override
  public void onGitReferenceUpdated(Event event) {
    String projectName = event.getProjectName();
    Repository repository;
    try {
      repository = repositoryManager.openRepository(Project.NameKey.parse(projectName));
      try {
        processEvent(repository, event);
      } finally {
        repository.close();
      }
    } catch (IOException e) {
      logger.warn("Couldn't open repository: {}", projectName, e);
    }
  }

  private void processEvent(Repository repository, Event event) {
    if (event.getRefName().startsWith(CHANGES_REF)) {
      Change.Id id = Change.Id.fromRef(event.getRefName());
      try {
        Change change = db.get().changes().get(id);
        PatchList patchList = getPatchList(event, change);
        if (patchList != null) {
          PathOwners owners = new PathOwners(accountResolver, repository, patchList);
          reviewerManager.addReviewers(change, owners.get().values());
        }
      } catch (OrmException e) {
        logger.warn("Could not open change: {}", id, e);
      } catch (ReviewerManagerException e) {
        logger.warn("Could not add reviewers for change: {}", id, e);
      }
    }
  }

  private PatchList getPatchList(Event event, Change change) {
    ObjectId newId = null;
    if (event.getNewObjectId() != null) {
      newId = ObjectId.fromString(event.getNewObjectId());
    }

    PatchListKey plKey = new PatchListKey(change.getProject(), null, newId, IGNORE_NONE);
    try {
      return patchListCache.get(plKey);
    } catch (PatchListNotAvailableException e) {
      logger.warn("Could not load patch list: {}", plKey, e);
    }
    return null;
  }
}
