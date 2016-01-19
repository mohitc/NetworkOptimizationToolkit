package com.network.topology.capacity.vars;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariablePrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CapacityVarNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = VariablePrefixes.CAPACITY;

  private static final String LOG_PREFIX = "C:- ";

  private static final Logger log = LoggerFactory.getLogger(CapacityVarNameGenerator.class);


  public CapacityVarNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("{}Name generator initialized with empty set of vertices", LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    addValidator(new LPPrefixClassValidator(0, String.class, "Vertex should be of type string"));
    addValidator(new LPPrefixClassValidator(1, String.class, "Vertex should be of type string"));
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Index 0 should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Index 1 should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "Both vertices have the same index"));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}