package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPNumberRangeValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ServiceAwareSourceLoopAvoidanceConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String SOURCE_LOOP_AVOIDANCE_CONST_PREFIX = ServiceAwareConstraintPrefixes.SERVICE_LOOP_AVOIDANCE_SOURCE;

  private static final String LOG_PREFIX = "SERVICE_DEST_LOOP_AVOIDANCE:- ";

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareSourceLoopAvoidanceConstrNameGenerator.class);

  public ServiceAwareSourceLoopAvoidanceConstrNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(SOURCE_LOOP_AVOIDANCE_CONST_PREFIX, 3);
    if (vertexVars == null) {
      log.error("{} Name generator initialized with empty set of vertices", LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses <=0) {
      log.error("Service classes should be a positive integer (>0). Defaulting to 1");
      serviceClasses = 1;
    }

    addValidator(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    addValidator(new LPNumberRangeValidator(0, 1, serviceClasses, "Not a valid circuit class"));

    //source and destination should be string variables
    addValidator(new LPPrefixClassValidator(1, String.class, "Source must be a string"));
    addValidator(new LPPrefixClassValidator(2, String.class, "Destination must be a string"));

    //b) all prefixes should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(1, 2, "s!=d"));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }

}
