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

public class UniqueForwardingConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(UniqueForwardingConstrGroupInitializer.class);

  public UniqueForwardingConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator forwardingNameGenerator = model().getLPVarGroup(VarGroups.FORWARDING).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String d : vertices) {
        //for all distinct pair of vertices, only single forwarding entry can exists: sum_j [F(d,i,j)] = 1
        for (String i : vertices) {
          if (d.equals(i))
            continue;
          LPExpression lhs = new LPExpression(model());


          for (String j : vertices) {
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
