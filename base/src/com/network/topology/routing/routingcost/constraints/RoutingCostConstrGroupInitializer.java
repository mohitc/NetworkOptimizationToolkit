package com.network.topology.routing.routingcost.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantGroups;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RoutingCostConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingCostConstrGroupInitializer.class);

  public RoutingCostConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator routingCostNameGenerator = model().getLPVarGroup(VarGroups.ROUTING_COST).getNameGenerator();
      LPNameGenerator routingNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();
      LPNameGenerator linkWeightConstantNameGenerator = model().getLPConstantGroup(ConstantGroups.LINK_WEIGHT).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

      for (String s: vertices) {
        for (String d: vertices) {
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          lhs.addTerm(model().getLPVar(routingCostNameGenerator.getName(s, d)));
          LPExpression rhs = new LPExpression(model());
          for (String i: vertices) {
            for (String j: vertices) {
              if (i.equals(j))
                continue;
              rhs.addTerm(model().getLPConstant(linkWeightConstantNameGenerator.getName(s,d)).getValue(), model().getLPVar(routingNameGenerator.getName(s, d, i, j)));
            }
          }
          model().addConstraint(generator().getName(s, d), lhs, LPOperator.EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
