package com.network.topology.linkexists.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FixedLinkExistsConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String LINK_EXISTS_CONSTR_PREFIX = "LE-Const";

  private static final String LINK_EXISTS_CONSTR_LOG_PREFIX = "LE:- ";

  private static final Logger log = LoggerFactory.getLogger(FixedLinkExistsConstrNameGenerator.class);

  private Set<String> vertexVars;

  public FixedLinkExistsConstrNameGenerator(Set<String> vertexVars) {
    super(LINK_EXISTS_CONSTR_PREFIX, 2);
    if (vertexVars==null) {
      log.error("{}Name generator initialized with empty set of vertices", LINK_EXISTS_CONSTR_LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "Source and destination have the same index"));
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
  }
}
