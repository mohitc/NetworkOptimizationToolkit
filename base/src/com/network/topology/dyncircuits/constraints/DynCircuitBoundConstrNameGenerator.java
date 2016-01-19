package com.network.topology.dyncircuits.constraints;


import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DynCircuitBoundConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = "DYN-CIR-BOUND";

  private static final Logger log = LoggerFactory.getLogger(DynCircuitBoundConstrNameGenerator.class);

  public DynCircuitBoundConstrNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    addValidator(new LPPrefixClassValidator(0, String.class, "Source should be of type string"));
    addValidator(new LPPrefixClassValidator(1, String.class, "Destination should be of type string"));
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "Source and destination cannot be the same"));

  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
