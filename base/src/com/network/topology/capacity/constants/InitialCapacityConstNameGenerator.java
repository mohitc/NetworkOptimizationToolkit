package com.network.topology.capacity.constants;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class InitialCapacityConstNameGenerator extends LPNameGeneratorImpl<String> {
  private static final String PREFIX = ConstantPrefixes.INITIAL_CAPACITY;

  private static final Logger log = LoggerFactory.getLogger(InitialCapacityConstNameGenerator.class);

  public InitialCapacityConstNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Index 0 should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Index 1 should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "Both vertices have the same index"));
  }

    @Override
  protected void validatePrefixConstraint(List<String> list) throws LPNameException {
  }
}
