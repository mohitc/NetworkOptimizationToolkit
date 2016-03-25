package com.network.topology.dyncircuits.constraints;

import com.lpapi.entities.*;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPConstantException;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class SymDynCirConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(SymDynCirConstrGroupInitializer.class);

  public SymDynCirConstrGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    int circuitClasses;
    try {
      circuitClasses = (int) model().getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();
      if (circuitClasses <= 0) {
        log.error("Circuit classes should be a positive integer (>0). Defaulting to 1");
        circuitClasses = 1;
      }
    } catch (LPConstantException e) {
      log.error("Number of dynamic circuit classes not defined. Defaulting to 1");
      circuitClasses = 1;
    }
    try {
      LPNameGenerator dynCircuitNameGenerator = model().getLPVarGroup(VarGroups.DYN_CIRCUITS).getNameGenerator();
      LPConstraintGroup group = this.getGroup().getModel().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (int n=1; n<=circuitClasses; n++) {
        for (String i: vertices) {
          for (String j: vertices) {
            //reduce duplicates
            if (i.compareTo(j)<=0)
              continue;
            LPExpression lhs = new LPExpression(model());
            LPExpression rhs = new LPExpression(model());
            lhs.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(n, i, j)));
            rhs.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(n, j, i)));
            model().addConstraint(generator().getName(n, i, j), lhs, LPOperator.EQUAL, rhs, group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Dynamic Circuit variable group", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
