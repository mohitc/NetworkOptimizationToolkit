package com.network.topology.routing.routingcost.constraints;

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

public class MinRoutingCostConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(MinRoutingCostConstrGroupInitializer.class);

  public MinRoutingCostConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator routingCostNameGenerator = model().getLPVarGroup(VarGroups.ROUTING_COST).getNameGenerator();
      LPNameGenerator linkWeightNameGenerator = model().getLPVarGroup(VarGroups.LINK_WEIGHT).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s: vertices) {
        for (String d: vertices) {
          if (s.equals(d))
            continue;
          for (String x: vertices) {
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
