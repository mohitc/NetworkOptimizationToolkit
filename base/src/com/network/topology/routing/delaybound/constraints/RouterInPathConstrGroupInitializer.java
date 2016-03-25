package com.network.topology.routing.delaybound.constraints;

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

public class RouterInPathConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouterInPathConstrGroupInitializer.class);

  public RouterInPathConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator routingVarNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();
      LPNameGenerator routerInPathVarNameGenerator = model().getLPVarGroup(VarGroups.ROUTER_IN_PATH).getNameGenerator();
      //Constraint 11
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          if (s.equals(d))
            continue; //skip self loop case
          for (String i: vertices) {
            LPExpression lhs = new LPExpression(model());
            lhs.addTerm(model().getLPVar(routerInPathVarNameGenerator.getName(s, d, i)));
            LPExpression rhs = new LPExpression(model());
            if (i.equals(s) || i.equals(d)) {
              //RD k (s) sd = 1
              rhs.addTerm(1);
            } else {
              //Intermediate nodes, create constraints
              //Sum over outgoing links from i
              for (String j:vertices) {
                if (j.equals(i))
                  continue;
                rhs.addTerm(model().getLPVar(routingVarNameGenerator.getName(s, d, i, j)));
              }
            }
            model().addConstraint(generator().getName(s, d, i), lhs, LPOperator.EQUAL, rhs, group);
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }

}
