package com.network.topology.routing.delaybound.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RouterInPathConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouterInPathConstrGroupInitializer.class);

  private LPNameGenerator routingVarNameGenerator, routerInPathVarNameGenerator;

  private Set<String> vertexVars;

  public RouterInPathConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator routingVarNameGenerator, LPNameGenerator routerInPathVarNameGenerator) {
    if (routingVarNameGenerator==null) {
      log.error("Initialized with empty routing variable name generator");
      this.routingVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingVarNameGenerator = routingVarNameGenerator;
    }
    if (routerInPathVarNameGenerator==null) {
      log.error("Initialized with empty routing in path name generator");
      this.routerInPathVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routerInPathVarNameGenerator = routerInPathVarNameGenerator;
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
      //Constraint 11
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertexVars) {
        for (String d : vertexVars) {
          if (s.equals(d))
            continue; //skip self loop case
          for (String i: vertexVars) {
            LPExpression lhs = new LPExpression(model());
            lhs.addTerm(model().getLPVar(routerInPathVarNameGenerator.getName(s, d, i)));
            LPExpression rhs = new LPExpression(model());
            if (i.equals(s) || i.equals(d)) {
              //RD k (s) sd = 1
              rhs.addTerm(1);
            } else {
              //Intermediate nodes, create constraints
              //Sum over outgoing links from i
              for (String j:vertexVars) {
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
