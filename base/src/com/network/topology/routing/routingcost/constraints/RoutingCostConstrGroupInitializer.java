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

public class RoutingCostConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingCostConstrGroupInitializer.class);

  private Set<String> vertexVars;

  private LPNameGenerator routingCostNameGenerator, routingNameGenerator, linkWeightConstantNameGenerator;

  public RoutingCostConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator routingCostNameGenerator, LPNameGenerator routingNameGenerator, LPNameGenerator linkWeightConstantNameGenerator) {
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

    if (routingNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.routingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingNameGenerator = routingNameGenerator;
    }
    if (linkWeightConstantNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkWeightConstantNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkWeightConstantNameGenerator = linkWeightConstantNameGenerator;
    }
  }

  @Override
  public void run() throws LPModelException {
    //Set<Link> links = manager.getAllElements(Link.class);
    try {

      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

      for (String s: vertexVars) {
        for (String d: vertexVars) {
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          lhs.addTerm(model().getLPVar(routingCostNameGenerator.getName(s, d)));
          LPExpression rhs = new LPExpression(model());
          for (String i: vertexVars) {
            if (i.equals(d))
              continue;
            for (String j: vertexVars) {
              if (j.equals(s) || j.equals(i))
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
