package com.network.topology.serviceaware.routing.delaybound.constants;


import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPNumberRangeValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstantPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ServiceAwareRoutePathDelayConstNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = ServiceAwareConstantPrefixes.SERVICE_PATH_DELAY;

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRoutePathDelayConstNameGenerator.class);

  public ServiceAwareRoutePathDelayConstNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 3);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses <=0) {
      log.error("Service classes should be a positive integer (>0). Defaulting to 1");
      serviceClasses = 1;
    }

    //add validators
    //service class is an integer in the set serviceClassSet
    addValidator(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    addValidator(new LPNumberRangeValidator(0, 1, serviceClasses, "Not a valid circuit class"));

    //Router should be strings, in the set of vertices, and distinct
    addValidator(new LPPrefixClassValidator(1, String.class, "Source must be a string"));
    addValidator(new LPPrefixClassValidator(2, String.class, "Destination must be a string"));

    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));

    addValidator(new LPDistinctPrefixValidator(1, 2, "s!=d"));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
