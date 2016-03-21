package com.network.topology.routing.delaybound.constraints;

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

public class RouteDelayConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouteDelayConstrGroupInitializer.class);

  private LPNameGenerator routingVarNameGenerator, routerInPathVarNameGenerator, linkDelayConstNameGenerator,
    routerDelayConstNameGenerator, routePathDelayConstNameGenerator;

  private Set<String> vertexVars;

  public RouteDelayConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator routingVarNameGenerator, LPNameGenerator routerInPathVarNameGenerator,
                                          LPNameGenerator linkDelayConstNameGenerator, LPNameGenerator routerDelayConstNameGenerator,
                                          LPNameGenerator routePathDelayConstNameGenerator) {
    if (routingVarNameGenerator==null) {
      log.error("Initialized with empty routing variable name generator");
      this.routingVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingVarNameGenerator = routingVarNameGenerator;
    }
    if (routerInPathVarNameGenerator==null) {
      log.error("Initialized with empty routing in path name generator");
      this.routerInPathVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routerInPathVarNameGenerator = routerInPathVarNameGenerator;
    }
    if (linkDelayConstNameGenerator==null) {
      log.error("Initialized with empty link delay constant name generator");
      this.linkDelayConstNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkDelayConstNameGenerator = linkDelayConstNameGenerator;
    }
    if (routerDelayConstNameGenerator==null) {
      log.error("Initialized with empty router delay constant name generator");
      this.routerDelayConstNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routerDelayConstNameGenerator = routerDelayConstNameGenerator;
    }
    if (routePathDelayConstNameGenerator==null) {
      log.error("Initialized with empty route path delay bound constant name generator");
      this.routePathDelayConstNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routePathDelayConstNameGenerator = routePathDelayConstNameGenerator;
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
      //Constraint 11
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      for (String s : vertexVars) {
        for (String d : vertexVars) {
          if (s.equals(d))
            continue;
          //max delay bound on rhs
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(routePathDelayConstNameGenerator.getName(s,d)));
          LPExpression lhs = new LPExpression(model());

          for (String i: vertexVars) {
            //add router delays
            lhs.addTerm(model().getLPConstant(routerDelayConstNameGenerator.getName(i)).getValue(),
              model().getLPVar(routerInPathVarNameGenerator.getName(s, d, i)));
            //add link delays

            for (String j: vertexVars) {
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
