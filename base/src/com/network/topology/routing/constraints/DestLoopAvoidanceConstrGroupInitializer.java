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

public class DestLoopAvoidanceConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DestLoopAvoidanceConstrGroupInitializer.class);

  private LPNameGenerator routingNameGenerator;

  private Set<String> vertexVars;

  public DestLoopAvoidanceConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator routingNameGenerator) {
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
          LPExpression lhs = new LPExpression(model());
          for (String i: vertexVars) {
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