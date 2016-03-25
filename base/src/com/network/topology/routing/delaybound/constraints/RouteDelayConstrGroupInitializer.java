package com.network.topology.routing.delaybound.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantGroups;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RouteDelayConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouteDelayConstrGroupInitializer.class);

  public RouteDelayConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator routingVarNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();
      LPNameGenerator routerInPathVarNameGenerator = model().getLPVarGroup(VarGroups.ROUTER_IN_PATH).getNameGenerator();
      LPNameGenerator linkDelayConstNameGenerator = model().getLPConstantGroup(ConstantGroups.LINK_DELAY).getNameGenerator();
      LPNameGenerator routerDelayConstNameGenerator = model().getLPConstantGroup(ConstantGroups.ROUTER_DELAY).getNameGenerator();
      LPNameGenerator routePathDelayConstNameGenerator = model().getLPConstantGroup(ConstantGroups.PATH_DELAY).getNameGenerator();

      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          if (s.equals(d))
            continue;
          //max delay bound on rhs
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(routePathDelayConstNameGenerator.getName(s,d)));
          LPExpression lhs = new LPExpression(model());

          for (String i: vertices) {
            //add router delays
            lhs.addTerm(model().getLPConstant(routerDelayConstNameGenerator.getName(i)).getValue(),
              model().getLPVar(routerInPathVarNameGenerator.getName(s, d, i)));
            //add link delays

            for (String j: vertices) {
              if (i.equals(j))
                continue;


              lhs.addTerm(model().getLPConstant(linkDelayConstNameGenerator.getName(i,j)).getValue(), model().
                getLPVar(routingVarNameGenerator.getName(s, d, i, j)));
            }
          }
          model().addConstraint(generator().getName(s, d), lhs, LPOperator.LESS_EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }

}
