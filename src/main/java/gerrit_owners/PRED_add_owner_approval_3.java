/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package gerrit_owners;

import com.vmware.gerrit.owners.PathOwnersStoredValue;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.gerrit.reviewdb.client.Account;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.rules.PrologEnvironment;
import com.google.gerrit.rules.StoredValues;
import com.google.gerrit.server.IdentifiedUser;
import com.google.inject.util.Providers;
import com.googlecode.prolog_cafe.lang.IntegerTerm;
import com.googlecode.prolog_cafe.lang.ListTerm;
import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.PrologException;
import com.googlecode.prolog_cafe.lang.StructureTerm;
import com.googlecode.prolog_cafe.lang.SymbolTerm;
import com.googlecode.prolog_cafe.lang.Term;
import com.googlecode.prolog_cafe.lang.VariableTerm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Predicate for add_owner_approval/3.
 * <p/>
 * Adds OWNERS file approval checks.
 */
public class PRED_add_owner_approval_3 extends Predicate.P3 {

  private static final SymbolTerm OWNER_APPROVAL = SymbolTerm.intern("Owner-Approval");

  // Cached path owners.
  private static PathOwnersStoredValue PATH_OWNERS = new PathOwnersStoredValue();

  public PRED_add_owner_approval_3(Term a1, Term a2, Term a3, Operation n) {
    arg1 = a1;
    arg2 = a2;
    arg3 = a3;
    cont = n;
  }

  @Override
  public Operation exec(Prolog engine) throws PrologException {
    engine.setB0();

    SetMultimap<String, String> pathOwners = PATH_OWNERS.get(engine);

    Term a1 = arg1.dereference();
    Term a2 = arg2.dereference();
    Term a3 = arg3.dereference();

    if (!a3.isVariable()) {
      return engine.fail();
    }

    Set<String> emails;
    if (a1.isList()) {
      emails = getReviewerEmails(engine, (ListTerm) a1);
    } else {
      emails = Collections.emptySet();
    }

    if (!isOwnerApproved(pathOwners, emails)) {
      a2 = appendLabel(a2, "need", new VariableTerm(engine));
    }

    ((VariableTerm) a3).bind(a2, engine.trail);

    return cont;
  }

  /**
   * Append commit label.
   *
   * @param list     commit labels list
   * @param status   ok, need, maybe, etc.
   * @param userTerm commit label user
   * @return resulting list
   */
  private ListTerm appendLabel(Term list, String status, Term userTerm) {
    StructureTerm label = new StructureTerm("label", OWNER_APPROVAL, new StructureTerm(status, userTerm));
    return new ListTerm(label, list);
  }

  /**
   * Returns if the change has been approved by owners.
   *
   * @param pathOwners     map of paths to a set of owners for the path
   * @param reviewerEmails set of reviewer emails who gave a CR +2
   * @return true iff all the paths were approved
   */
  private boolean isOwnerApproved(SetMultimap<String, String> pathOwners, Set<String> reviewerEmails) {
    for (String path : pathOwners.keySet()) {
      Set<String> requiredReviewers = pathOwners.get(path);
      Sets.SetView<String> intersection = Sets.intersection(requiredReviewers, reviewerEmails);
      if (intersection.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a set of all the reviewer emails that gave a CR +2.
   *
   * @param engine prolog engine
   * @param list   commit labels list
   * @return set of reviewer emails
   */
  private static Set<String> getReviewerEmails(Prolog engine, ListTerm list) {
    VariableTerm userIdTerm = new VariableTerm(engine);
    StructureTerm userTerm = new StructureTerm("user", userIdTerm);

    Set<String> result = new HashSet<String>();
    while (true) {
      StructureTerm term = (StructureTerm) list.car();
      if (userTerm.unify(term, engine.trail)) {
        int userId = ((IntegerTerm) userIdTerm.dereference()).intValue();
        IdentifiedUser user = getUser(engine, userId);
        result.addAll(user.getEmailAddresses());
        userIdTerm.undo();
      }

      if (list.cdr().isList()) {
        list = (ListTerm) list.cdr();
      } else {
        return result;
      }
    }
  }

  /**
   * Get user from cache or ReviewDb by id.
   *
   * @param engine prolog engine
   * @param id     user id
   * @return user that matches the given id
   */
  private static IdentifiedUser getUser(Prolog engine, int id) {
    Map<Account.Id, IdentifiedUser> cache = StoredValues.USERS.get(engine);
    Account.Id accountId = new Account.Id(id);
    IdentifiedUser user = cache.get(accountId);
    if (user == null) {
      ReviewDb db = StoredValues.REVIEW_DB.getOrNull(engine);
      IdentifiedUser.GenericFactory userFactory = userFactory(engine);
      IdentifiedUser who;
      if (db != null) {
        who = userFactory.create(Providers.of(db), accountId);
      } else {
        who = userFactory.create(accountId);
      }
      cache.put(accountId, who);
      user = who;
    }
    return user;
  }

  private static IdentifiedUser.GenericFactory userFactory(Prolog engine) {
    PrologEnvironment env = (PrologEnvironment) engine.control;
    return env.getInjector().getInstance(IdentifiedUser.GenericFactory.class);
  }
}
