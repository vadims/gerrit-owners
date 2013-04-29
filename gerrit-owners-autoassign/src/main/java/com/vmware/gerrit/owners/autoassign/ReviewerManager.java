/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package com.vmware.gerrit.owners.autoassign;

import com.google.gerrit.common.errors.EmailException;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.client.Change;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.change.ChangeResource;
import com.google.gerrit.server.change.PostReviewers;
import com.google.gerrit.server.project.ChangeControl;
import com.google.gerrit.server.project.NoSuchChangeException;
import com.google.gwtorm.server.OrmException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class ReviewerManager {

  private final Provider<CurrentUser> currentUserProvider;

  private final Provider<PostReviewers> postReviewersProvider;

  private final ChangeControl.GenericFactory changeControlFactory;

  @Inject
  public ReviewerManager(Provider<CurrentUser> currentUserProvider,
                         Provider<PostReviewers> postReviewersProvider,
                         ChangeControl.GenericFactory changeControlFactory) {
    this.currentUserProvider = currentUserProvider;
    this.postReviewersProvider = postReviewersProvider;
    this.changeControlFactory = changeControlFactory;
  }

  public void addReviewers(Change change, Collection<Account.Id> reviewers) throws ReviewerManagerException {
    try {
      PostReviewers postReviewers = postReviewersProvider.get();
      ChangeControl changeControl = changeControlFactory.controlFor(change, currentUserProvider.get());
      ChangeResource changeResource = new ChangeResource(changeControl);

      // HACK(vspivak): Using PostReviewers is probably inefficient here, however it has all the hook/notification
      // logic, so it's easier to call it then to mimic/copy the logic here.
      for (Account.Id accountId : reviewers) {
        PostReviewers.Input input = new PostReviewers.Input();
        input.reviewer = accountId.toString();
        postReviewers.apply(changeResource, input);
      }
    } catch (RestApiException e) {
      throw new ReviewerManagerException(e);
    } catch (NoSuchChangeException e) {
      throw new ReviewerManagerException(e);
    } catch (EmailException e) {
      throw new ReviewerManagerException(e);
    } catch (OrmException e) {
      throw new ReviewerManagerException(e);
    }
  }
}
