package com.network.topology.linkweight.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LinkWeightConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String LINK_WEIGHT_CONSTR_PREFIX = ConstraintPrefixes.LINK_WEIGHT;

  private static final String LINK_WEIGHT_CONSTR_LOG_PREFIX = "LW:- ";

  private static final Logger log = LoggerFactory.getLogger(LinkWeightConstrNameGenerator.class);

  public LinkWeightConstrNameGenerator(Set<String> vertexVars) {
    super(LINK_WEIGHT_CONSTR_PREFIX, 2);
    if (vertexVars==null) {
      log.error("{} Name generator initialized with empty set of vertices", LINK_WEIGHT_CONSTR_LOG_PREFIX);
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
