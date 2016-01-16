package com.network.topology.dyncircuits.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariableBoundConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class DynCircuitBoundConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DynCircuitBoundConstrGroupInitializer.class);

  private LPNameGenerator dynCircuitNameGenerator;

  private Set<String> vertexVars;

  public DynCircuitBoundConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator dynCircuitNameGenerator) {
    if (dynCircuitNameGenerator==null) {
      log.error("Initialized with empty dynamic circuit variable name generator");
      this.dynCircuitNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.dynCircuitNameGenerator = dynCircuitNameGenerator;
    }
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("Constraint generator initialized with empty set of vertices");
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  public void run() throws LPModelException {
    try {
      int vertexClasses = (int)model().getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertexVars) {
        for (String d : vertexVars) {
          //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          for (int n=1; n <= vertexClasses; n++) {
            lhs.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(s, d, n)));
          }
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(VariableBoundConstants.DYN_CIRTUITS_MAX));
          model().addConstraint(generator().getName(s,d), lhs, LPOperator.LESS_EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
