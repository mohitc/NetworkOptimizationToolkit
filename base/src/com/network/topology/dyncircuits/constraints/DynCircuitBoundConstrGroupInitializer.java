package com.network.topology.dyncircuits.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DynCircuitBoundConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DynCircuitBoundConstrGroupInitializer.class);

  public DynCircuitBoundConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      int vertexClasses = (int)model().getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();
      LPNameGenerator dynCircuitNameGenerator = model().getLPVarGroup(VarGroups.DYN_CIRCUITS).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          for (int n=1; n <= vertexClasses; n++) {
            lhs.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(n, s, d)));
          }
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(FixedConstants.DYN_CIRTUITS_MAX).getValue());
          model().addConstraint(generator().getName(s,d), lhs, LPOperator.LESS_EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
