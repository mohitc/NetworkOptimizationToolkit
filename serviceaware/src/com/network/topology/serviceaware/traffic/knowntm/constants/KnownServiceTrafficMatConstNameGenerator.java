package com.network.topology.serviceaware.traffic.knowntm.constants;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstantPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnownServiceTrafficMatConstNameGenerator extends LPNameGeneratorImpl {
  private static final String PREFIX = ServiceAwareConstantPrefixes.SERVICE_TRAFFIC;

  private static final Logger log = LoggerFactory.getLogger(KnownServiceTrafficMatConstNameGenerator.class);

  public KnownServiceTrafficMatConstNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 3);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses<1) {
      log.error("Service classes should be a positive integer (>0) defaulting to 1");
      serviceClasses= 1;
    }
    Set<Integer> serviceSet = new HashSet<>();
    for (int n=1;n<=serviceClasses; n++) {
      serviceSet.add(serviceClasses);
    }
    addValidator(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    addValidator(new LPSetContainmentValidator(0, serviceSet, "Service class not in service set"));

    addValidator(new LPPrefixClassValidator(1, String.class, "Source should be a string variable"));
    addValidator(new LPPrefixClassValidator(2, String.class, "Destination should be a string variable"));

    //both variables should be in the set of vertices
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    //variables should be distincy
    addValidator(new LPDistinctPrefixValidator(1, 2, "Source cannot be equal to destination"));
  }

  @Override
  protected void validatePrefixConstraint(List list) throws LPNameException {
  }
}
