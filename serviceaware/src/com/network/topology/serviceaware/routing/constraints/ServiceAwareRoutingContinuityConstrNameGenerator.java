package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceAwareRoutingContinuityConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String ROUTING_CONTINUITY_CONSTR_PREFIX = ServiceAwareConstraintPrefixes.SERVICE_ROUTING_CONTINUITY;

  private static final String LOG_PREFIX = "SERVICE_ROUTING_CONTINUITY:- ";

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRoutingContinuityConstrNameGenerator.class);

  public ServiceAwareRoutingContinuityConstrNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(ROUTING_CONTINUITY_CONSTR_PREFIX, 4);
    if (vertexVars == null) {
      log.error("{} Name generator initialized with empty set of vertices", LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses <=0) {
      log.error("Service classes should be a positive integer (>0). Defaulting to 1");
      serviceClasses = 1;
    }

    //add validators
    Set<Integer> serviceClassSet = new HashSet<>();
    for (int i=1;i<=serviceClasses;i++)
      serviceClassSet.add(i);
    //service class is an integer in the set serviceClassSet
    addValidator(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    addValidator(new LPSetContainmentValidator(0, serviceClassSet, "Not a valid circuit class"));

    //a check for parameter class types
    addValidator(new LPPrefixClassValidator(1, String.class, "source should be a string"));
    addValidator(new LPPrefixClassValidator(2, String.class, "destination should be a string"));
    addValidator(new LPPrefixClassValidator(3, String.class, "Intermediate vertex should be a string"));

    //b) all prefixes should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(3, vertexVars, "prefix j should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(1, 2, "s!=d"));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {

  }
}
