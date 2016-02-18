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

public class MinRoutingCostConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = ConstraintPrefixes.MIN_ROUTING_COST;

  private static final String LOG_PREFIX = "MIN_ROUTING_COST:- ";

  private static final Logger log = LoggerFactory.getLogger(MinRoutingCostConstrNameGenerator.class);

  public MinRoutingCostConstrNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 3);
    if (vertexVars == null) {
      log.error("Name generator initialized with empty set of vertices", LOG_PREFIX);
      vertexVars = Collections.EMPTY_SET;
    }
    //b) all prefixes should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "intermediate vertex should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    //Min Routing Cost (s,x,d)
    addValidator(new LPDistinctPrefixValidator(0, 2, "s!=d"));
    addValidator(new LPDistinctPrefixValidator(1, 2, "x!=d"));
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
  }
}
