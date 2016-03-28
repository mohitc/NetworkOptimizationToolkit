package com.network.topology.serviceaware.traffic.knowntm.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import com.network.topology.serviceaware.SAConstantGroups;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class KnownServiceTmTrafficConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(KnownServiceTmTrafficConstrGroupInitializer.class);

  public KnownServiceTmTrafficConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      //Constraint 11
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
      int serviceClasses = (int)model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      LPNameGenerator capacityVarNameGenerator = model().getLPVarGroup(VarGroups.CAPACITY).getNameGenerator();
      LPNameGenerator serviceRoutingVarNameGenerator = model().getLPVarGroup(SAVarGroups.SA_ROUTING).getNameGenerator();
      LPNameGenerator knownServiceTrafficMatConstNameGenerator = model().getLPConstantGroup(SAConstantGroups.SA_TRAFFIC_MAT).getNameGenerator();

      for (String i : vertices) {
        for (String j : vertices) {
          if (i.equals(j)) {
            continue; //skip self loop case
          }

          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(FixedConstants.ALPHA).getValue(), model().getLPVar(capacityVarNameGenerator.getName(i,j)));

          //iterate for traffic between all s/d pairs and all traffic classes
          LPExpression lhs = new LPExpression(model());
          for (int t=1; t<=serviceClasses;t++) {
            for (String s : vertices) {
              if (s.equals(j))
                continue;
              for (String d : vertices) {
                if (i.equals(d) || s.equals(d))
                  continue;
                lhs.addTerm(model().getLPConstant(knownServiceTrafficMatConstNameGenerator.getName(t, s, d)),
                    model().getLPVar(serviceRoutingVarNameGenerator.getName(t, s, d, i, j)));
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
