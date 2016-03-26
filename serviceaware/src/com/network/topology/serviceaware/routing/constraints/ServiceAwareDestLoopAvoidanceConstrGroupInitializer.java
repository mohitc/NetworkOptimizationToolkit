package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServiceAwareDestLoopAvoidanceConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareDestLoopAvoidanceConstrGroupInitializer.class);

  public ServiceAwareDestLoopAvoidanceConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      LPNameGenerator serviceRoutingNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTING).getNameGenerator();
      int serviceClasses = (int)model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int n=1;n<=serviceClasses;n++) {
        for (String s : vertices) {
          for (String d : vertices) {
            if (s.equals(d))
              continue;
            LPExpression lhs = new LPExpression(model());
            for (String i : vertices) {
              if (i.equals(d))
                continue;
              lhs.addTerm(model().getLPVar(serviceRoutingNameGenerator.getName(n, s, d, d, i)));
            }
            LPExpression rhs = new LPExpression(model());
            rhs.addTerm(0);
            model().addConstraint(generator().getName(s, d), lhs, LPOperator.EQUAL, rhs, group);
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Constr name not found: " + e.getMessage());
      throw new LPModelException("Constr name not found: " + e.getMessage());
    }
  }
}
