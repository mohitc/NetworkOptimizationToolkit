package com.network.topology.forwarding.constraints;

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

public class ForwardingBasedRoutingConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ForwardingBasedRoutingConstrGroupInitializer.class);

  public ForwardingBasedRoutingConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator forwardingNameGenerator = model().getLPVarGroup(VarGroups.FORWARDING).getNameGenerator();
      LPNameGenerator routingNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
          if (s.equals(d))
            continue;
          for (String i : vertices) {
            for (String j : vertices) {
              if (i.equals(j) || i.equals(d))
                continue;
              LPExpression lhs = new LPExpression(model());
              lhs.addTerm(model().getLPVar(routingNameGenerator.getName(s, d, i, j)));
              LPExpression rhs = new LPExpression(model());
              rhs.addTerm(model().getLPVar(forwardingNameGenerator.getName(d, i, j)));
              model().addConstraint(generator().getName(s,d,i,j), lhs, LPOperator.LESS_EQUAL, rhs, group);
            }
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
