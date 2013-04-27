/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package gerrit_owners;

import com.vmware.gerrit.owners.PathOwnersStoredValue;

import com.google.common.collect.SetMultimap;
import com.google.gerrit.reviewdb.client.Account;
import com.googlecode.prolog_cafe.lang.IntegerTerm;
import com.googlecode.prolog_cafe.lang.JavaObjectTerm;
import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.PrologException;
import com.googlecode.prolog_cafe.lang.StructureTerm;
import com.googlecode.prolog_cafe.lang.SymbolTerm;
import com.googlecode.prolog_cafe.lang.Term;

import java.util.Iterator;
import java.util.Map;

/**
 * 'owner'(-Path, -User)
 */
public class PRED_owner_2 extends Predicate.P2 {

  private static final PRED_owner_check OWNER_CHECK = new PRED_owner_check();
  private static final PRED_owner_empty OWNER_EMPTY = new PRED_owner_empty();
  private static final PRED_owner_next OWNER_NEXT = new PRED_owner_next();

  public PRED_owner_2(Term a1, Term a2, Operation n) {
    this.arg1 = a1;
    this.arg2 = a2;
    this.cont = n;
  }

  @Override
  public Operation exec(Prolog engine) throws PrologException {
    engine.cont = cont;
    engine.setB0();

    SetMultimap<String, Account.Id> owners = PathOwnersStoredValue.VALUE.get(engine);
    engine.areg1 = arg1;
    engine.areg2 = arg2;
    engine.areg3 = new JavaObjectTerm(owners.entries().iterator());
    return engine.jtry3(OWNER_CHECK, OWNER_NEXT);
  }

  private static class PRED_owner_check extends Operation {

    @Override
    public Operation exec(Prolog engine) throws PrologException {
      Term a1 = engine.areg1;
      Term a2 = engine.areg2;
      Term a3 = engine.areg3;

      @SuppressWarnings("unchecked")
      Iterator<Map.Entry<String, Account.Id>> iter =
          (Iterator<Map.Entry<String, Account.Id>>) ((JavaObjectTerm) a3).object();
      while (iter.hasNext()) {
        Map.Entry<String, Account.Id> entry = iter.next();

        SymbolTerm path = SymbolTerm.create(entry.getKey());
        if (!a1.unify(path, engine.trail)) {
          continue;
        }

        StructureTerm user = new StructureTerm("user", new IntegerTerm(entry.getValue().get()));
        if (!a2.unify(user, engine.trail)) {
          continue;
        }

        return engine.cont;
      }
      return engine.fail();
    }
  }

  private static class PRED_owner_next extends Operation {

    @Override
    public Operation exec(Prolog engine) throws PrologException {
      return engine.trust(OWNER_EMPTY);
    }
  }

  private static class PRED_owner_empty extends Operation {

    @Override
    public Operation exec(Prolog engine) throws PrologException {
      Term a3 = engine.areg3;

      @SuppressWarnings("unchecked")
      Iterator<Map.Entry<String, Account.Id>> iter =
          (Iterator<Map.Entry<String, Account.Id>>) ((JavaObjectTerm) a3).object();
      if (!iter.hasNext()) {
        return engine.fail();
      }

      return engine.jtry3(OWNER_CHECK, OWNER_NEXT);
    }
  }

}
