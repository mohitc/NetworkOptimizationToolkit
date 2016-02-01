package com.network.topology.serviceaware.traffic.knowntm.constraints;

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

public class KnownServiceTmTrafficConstrNameGenerator extends LPNameGeneratorImpl<String> {
  private static final String PREFIX = ServiceAwareConstantPrefixes.SERVICE_TRAFFIC;

  private static final Logger log = LoggerFactory.getLogger(KnownServiceTmTrafficConstrNameGenerator.class);

  public KnownServiceTmTrafficConstrNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    //both variables should be in the set of vertices
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Destination should be in the set of vertices"));
    //variables should be distincy
    addValidator(new LPDistinctPrefixValidator(0, 1, "Source cannot be equal to destination"));
  }

  @Override
  protected void validatePrefixConstraint(List<String> list) throws LPNameException {
  }
}
