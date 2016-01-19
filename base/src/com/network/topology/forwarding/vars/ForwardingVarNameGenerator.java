package com.network.topology.forwarding.vars;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariablePrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ForwardingVarNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = VariablePrefixes.FORWARDING;

  private static final Logger log = LoggerFactory.getLogger(ForwardingVarNameGenerator.class);

  public ForwardingVarNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 3);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    //b) all prefixes should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "d should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "i should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "j should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "i!=d"));
    addValidator(new LPDistinctPrefixValidator(1, 2, "i != j"));

  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
  }
}
