package com.network.topology.routing.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DestLoopAvoidanceConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DestLoopAvoidanceConstrGroupInitializer.class);

  public DestLoopAvoidanceConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator routingNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          for (String i: vertices) {
            if (i.equals(d))
              continue;
            lhs.addTerm(model().getLPVar(routingNameGenerator.getName(s,d,d,i)));
          }
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(0);
          model().addConstraint(generator().getName(s,d), lhs, LPOperator.EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Constr name not found: " + e.getMessage());
      throw new LPModelException("Constr name not found: " + e.getMessage());
    }
  }
}
