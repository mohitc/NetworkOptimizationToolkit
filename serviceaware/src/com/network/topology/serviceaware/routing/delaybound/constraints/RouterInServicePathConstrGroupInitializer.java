package com.network.topology.serviceaware.routing.delaybound.constraints;

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

public class RouterInServicePathConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouterInServicePathConstrGroupInitializer.class);

  public RouterInServicePathConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator serviceRoutingVarNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTING).getNameGenerator();
      LPNameGenerator routerInServicePathVarNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTER_IN_PATH).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int t=1;t<=serviceClasses; t++) {
        for (String s : vertices) {
          for (String d : vertices) {
            if (s.equals(d))
              continue; //skip self loop case
            for (String i : vertices) {
              LPExpression lhs = new LPExpression(model());
              lhs.addTerm(model().getLPVar(routerInServicePathVarNameGenerator.getName(t, s, d, i)));
              LPExpression rhs = new LPExpression(model());
              if (i.equals(s) || i.equals(d)) {
                //RD k (s) sd = 1
                rhs.addTerm(1);
              } else {
                //Intermediate nodes, create constraints
                //Sum over outgoing links from i
                for (String j : vertices) {
                  if (j.equals(i))
                    continue;
                  rhs.addTerm(model().getLPVar(serviceRoutingVarNameGenerator.getName(t, s, d, i, j)));
                }
              }
              model().addConstraint(generator().getName(t, s, d, i), lhs, LPOperator.EQUAL, rhs, group);
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
