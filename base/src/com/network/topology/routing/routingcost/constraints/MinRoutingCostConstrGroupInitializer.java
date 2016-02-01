package com.network.topology.routing.routingcost.constraints;

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

public class MinRoutingCostConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(MinRoutingCostConstrGroupInitializer.class);

  private Set<String> vertexVars;

  private LPNameGenerator routingCostNameGenerator, linkWeightNameGenerator;

  public MinRoutingCostConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator routingCostNameGenerator, LPNameGenerator linkWeightNameGenerator) {
    if (vertexVars!=null) {
      this.vertexVars= vertexVars;
    } else {
      log.error("Null topology manager provided for initializing constraints");
      this.vertexVars = Collections.EMPTY_SET;
    }
    if (routingCostNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.routingCostNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingCostNameGenerator = routingCostNameGenerator;
    }

    if (linkWeightNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkWeightNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkWeightNameGenerator = linkWeightNameGenerator;
    }
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s: vertexVars) {
        for (String d: vertexVars) {
          if (s.equals(d))
            continue;
          for (String x: vertexVars) {
            if (x.equals(d))
              continue;
            LPExpression lhs = new LPExpression(model());
            lhs.addTerm(model().getLPVar(routingCostNameGenerator.getName(s, d)));
            LPExpression rhs = new LPExpression(model());
            rhs.addTerm(model().getLPVar(linkWeightNameGenerator.getName(x,d)));
            if (!x.equals(s)) { //RC ss == 0
              rhs.addTerm(model().getLPVar(routingCostNameGenerator.getName(s,x)));
            }
            model().addConstraint(generator().getName(s, x, d), lhs, LPOperator.LESS_EQUAL, rhs, group);
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
