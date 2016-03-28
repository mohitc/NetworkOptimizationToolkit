package com.network.topology.serviceaware.routing.delaybound.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantGroups;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.serviceaware.SAConstantGroups;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServiceRouteDelayConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ServiceRouteDelayConstrGroupInitializer.class);

  public ServiceRouteDelayConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator serviceRoutingVarNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTING).getNameGenerator();
      LPNameGenerator routerInServicePathVarNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTER_IN_PATH).getNameGenerator();
      LPNameGenerator linkDelayConstNameGenerator = model().getLPConstantGroup(ConstantGroups.LINK_DELAY).getNameGenerator();
      LPNameGenerator serviceRouterDelayConstNameGenerator = model().getLPConstantGroup(SAConstantGroups.SERVICE_ROUTER_DELAY).getNameGenerator();
      LPNameGenerator serviceRoutePathDelayConstNameGenerator = model().getLPConstantGroup(SAConstantGroups.SERVICE_PATH_DELAY).getNameGenerator();

      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int t=1;t<=serviceClasses; t++) {

        for (String s : vertices) {
          for (String d : vertices) {
            if (s.equals(d))
              continue;
            //max delay bound on rhs
            LPExpression rhs = new LPExpression(model());
            rhs.addTerm(model().getLPConstant(serviceRoutePathDelayConstNameGenerator.getName(t, s, d)));
            LPExpression lhs = new LPExpression(model());

            for (String i : vertices) {
              //add router delays
              lhs.addTerm(model().getLPConstant(serviceRouterDelayConstNameGenerator.getName(t, i)).getValue(),
                  model().getLPVar(routerInServicePathVarNameGenerator.getName(t, s, d, i)));
              //add link delays

              for (String j : vertices) {
                if (i.equals(j))
                  continue;


                lhs.addTerm(model().getLPConstant(linkDelayConstNameGenerator.getName(i, j)).getValue(), model().
                    getLPVar(serviceRoutingVarNameGenerator.getName(t, s, d, i, j)));
              }
            }
            model().addConstraint(generator().getName(t, s, d), lhs, LPOperator.LESS_EQUAL, rhs, group);
          }
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }

}
