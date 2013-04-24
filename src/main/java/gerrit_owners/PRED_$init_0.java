/*
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved.
 */
package gerrit_owners;

import com.googlecode.prolog_cafe.lang.Operation;
import com.googlecode.prolog_cafe.lang.Predicate;
import com.googlecode.prolog_cafe.lang.Prolog;
import com.googlecode.prolog_cafe.lang.PrologException;

/**
 * Prolog gerrit_owners predicate initialization.
 */
public class PRED_$init_0 extends Predicate.P1 {

  public PRED_$init_0(Operation n) {
    cont = n;
  }

  @Override
  public Operation exec(Prolog engine) throws PrologException {
    engine.setB0();
    return cont;
  }
}
