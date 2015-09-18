package com.network.topology.forwarding.constraints;

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

public class UniqueForwardingConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(UniqueForwardingConstrGroupInitializer.class);

  private LPNameGenerator forwardingNameGenerator;

  private Set<String> vertexVars;

  public UniqueForwardingConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator forwardingNameGenerator) {
    if (forwardingNameGenerator==null) {
      log.error("Initialized with empty forwarding variable name generator");
      this.forwardingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.forwardingNameGenerator = forwardingNameGenerator;
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
      for (String d : vertexVars) {
        //for all distinct pair of vertices, only single forwarding entry can exists: sum_j [F(d,i,j)] = 1
        for (String i : vertexVars) {
          if (d.equals(i))
            continue;
          LPExpression lhs = new LPExpression(model());


          for (String j : vertexVars) {
            if (i.equals(j))
              continue;
            lhs.addTerm(model().getLPVar(forwardingNameGenerator.getName(d, i, j)));
          }
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(1);
          model().addConstraint(generator().getName(d,i), lhs, LPOperator.EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
