package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class ServiceAwareSymmetricRoutingConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareSymmetricRoutingConstrGroupInitializer.class);

  private LPNameGenerator serviceRoutingNameGenerator;

  private Set<String> vertexVars;

  public ServiceAwareSymmetricRoutingConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator serviceRoutingNameGenerator) {
    if (serviceRoutingNameGenerator==null) {
      log.error("Initialized with empty routing variable name generator");
      this.serviceRoutingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.serviceRoutingNameGenerator = serviceRoutingNameGenerator;
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
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int n=1;n<=serviceClasses;n++) {
        for (String s : vertexVars) {
          for (String d : vertexVars) {
            //for all distinct pair of vertices, routing can only use a link that exists : r(s,d,i,j) <= LE (i,j)
            if (s.compareTo(d) <= 0)
              continue;
            for (String i : vertexVars) {
              if (i.equals(d))
                continue;
              for (String j : vertexVars) {
                if (i.compareTo(j) <= 0)
                  continue;
                if (j.equals(s))
                  continue;
                ;
                LPExpression lhs = new LPExpression(model());
                lhs.addTerm(model().getLPVar(serviceRoutingNameGenerator.getName(n, s, d, i, j)));
                LPExpression rhs = new LPExpression(model());
                rhs.addTerm(model().getLPVar(serviceRoutingNameGenerator.getName(n, d, s, j, i)));
                model().addConstraint(generator().getName(n, s, d, i, j), lhs, LPOperator.EQUAL, rhs, group);
              }
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
