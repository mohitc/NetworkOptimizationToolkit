package com.network.topology.serviceaware.traffic.knowntm.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class KnownServiceTmTrafficConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(KnownServiceTmTrafficConstrGroupInitializer.class);

  private LPNameGenerator capacityVarNameGenerator, knownServiceTrafficMatConstNameGenerator,
    serviceRoutingVarNameGenerator;

  private Set<String> vertexVars;

  public KnownServiceTmTrafficConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator capacityVarNameGenerator,
                                              LPNameGenerator knownServiceTrafficMatConstNameGenerator,
                                              LPNameGenerator serviceRoutingVarNameGenerator) {
    if (capacityVarNameGenerator==null) {
      log.error("Initialized with empty capacity variable name generator");
      this.capacityVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.capacityVarNameGenerator = capacityVarNameGenerator;
    }
    if (knownServiceTrafficMatConstNameGenerator==null) {
      log.error("Initialized with empty circuit variable name generator");
      this.knownServiceTrafficMatConstNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.knownServiceTrafficMatConstNameGenerator = knownServiceTrafficMatConstNameGenerator;
    }
    if (serviceRoutingVarNameGenerator==null) {
      log.error("Initialized with empty circuit variable name generator");
      this.serviceRoutingVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.serviceRoutingVarNameGenerator = serviceRoutingVarNameGenerator;
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
      int serviceClasses = (int)model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();

      for (String i : vertexVars) {
        for (String j : vertexVars) {
          if (i.equals(j)) {
            continue; //skip self loop case
          }

          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(FixedConstants.ALPHA).getValue(), model().getLPVar(capacityVarNameGenerator.getName(i,j)));

          //iterate for traffic between all s/d pairs and all traffic classes
          LPExpression lhs = new LPExpression(model());
          for (int n=1; n<=serviceClasses;n++) {
            for (String s : vertexVars) {
              if (s.equals(j))
                continue;
              for (String d : vertexVars) {
                if (i.equals(d) || s.equals(d))
                  continue;
                lhs.addTerm(model().getLPConstant(knownServiceTrafficMatConstNameGenerator.getName(n, s, d)), model().getLPVar(serviceRoutingVarNameGenerator.getName(n, s, d, i, j)));
              }
            }
          }

          model().addConstraint(generator().getName(i,j), lhs, LPOperator.LESS_EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }

}
