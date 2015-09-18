package com.network.topology.routing.constraints;

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

public class RoutingConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingConstrGroupInitializer.class);

  private LPNameGenerator linkExistsNameGenerator, routingNameGenerator;

  private Set<String> vertexVars;

  public RoutingConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator linkExistsNameGenerator, LPNameGenerator routingNameGenerator) {
    if (linkExistsNameGenerator==null) {
      log.error("Initialized with empty link exists variable name generator");
      this.linkExistsNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkExistsNameGenerator = linkExistsNameGenerator;
    }
    if (routingNameGenerator==null) {
      log.error("Initialized with empty routing variable name generator");
      this.routingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingNameGenerator = routingNameGenerator;
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
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertexVars) {
        for (String d : vertexVars) {
          //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
          if (s.equals(d))
            continue;
          for (String i : vertexVars) {
            for (String j : vertexVars) {
              if (i.equals(j))
                continue;

              LPExpression lhs = new LPExpression(model());
              lhs.addTerm(model().getLPVar(routingNameGenerator.getName(s, d, i, j)));
              LPExpression rhs = new LPExpression(model());
              rhs.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i,j)));
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
