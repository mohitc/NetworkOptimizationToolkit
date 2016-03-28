package com.network.topology.serviceaware.traffic.knowntm.constraints;

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

public class KnownServiceTmTrafficConstrNameGenerator extends LPNameGeneratorImpl {
  private static final String PREFIX = ServiceAwareConstraintPrefixes.SERVICE_TM_TRAFFIC;

  private static final Logger log = LoggerFactory.getLogger(KnownServiceTmTrafficConstrNameGenerator.class);

  public KnownServiceTmTrafficConstrNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses<1) {
      log.error("Service classes should be a positive integer (>0) defaulting to 1");
      serviceClasses= 1;
    }

    addValidator(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    addValidator(new LPNumberRangeValidator(0, 1, serviceClasses, "Not a valid circuit class"));

    addValidator(new LPPrefixClassValidator(1, String.class, "ingress should be a string variable"));
    addValidator(new LPPrefixClassValidator(2, String.class, "egress should be a string variable"));

    //both variables should be in the set of vertices
    addValidator(new LPSetContainmentValidator(1, vertexVars, "ingress should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "egress should be in the set of vertices"));
    //variables should be distincy
    addValidator(new LPDistinctPrefixValidator(1, 2, "i!=j"));
  }

  @Override
  protected void validatePrefixConstraint(List list) throws LPNameException {
  }
}
