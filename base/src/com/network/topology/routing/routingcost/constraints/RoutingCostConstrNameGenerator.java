package com.network.topology.routing.routingcost.constraints;

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

public class RoutingCostConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = ConstraintPrefixes.ROUTING_COST;

  private static final String LOG_PREFIX = "ROUTING_COST:- ";

  private static final Logger log = LoggerFactory.getLogger(RoutingCostConstrNameGenerator.class);

  public RoutingCostConstrNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars == null) {
      log.error("Name generator initialized with empty set of vertices", LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    //b) all prefixes should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(0, 1, "s!=d"));
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
  }
}
