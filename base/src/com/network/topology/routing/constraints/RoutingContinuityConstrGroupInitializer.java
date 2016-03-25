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

public class RoutingContinuityConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingContinuityConstrGroupInitializer.class);

  public RoutingContinuityConstrGroupInitializer(Set<String> vertexVars) {
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
          for (String k : vertices) {
            //Generate constraint
            LPExpression sum1 = new LPExpression(model());
            LPExpression sum2 = new LPExpression(model());
            for (String i: vertices) {
              if (i.equals(k))
                continue;
              sum1.addTerm(model().getLPVar(routingNameGenerator.getName(s,d,k,i)));
              sum2.addTerm(model().getLPVar(routingNameGenerator.getName(s, d, i, k)));
            }
            LPExpression constExpr = new LPExpression(model());
            constExpr.addTerm(1);
            LPExpression lhs, rhs;
            if (k.equals(s)) {
              //only look at outgoing links;
              lhs = sum1;
              rhs = constExpr;
            } else if (k.equals(d)) {
              //only look at incoming links
              lhs = sum2;
              rhs = constExpr;
            } else {
              lhs = sum1;
              rhs = sum2;
            }
            model().addConstraint(generator().getName(s,d,k), lhs, LPOperator.EQUAL, rhs, group);
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
