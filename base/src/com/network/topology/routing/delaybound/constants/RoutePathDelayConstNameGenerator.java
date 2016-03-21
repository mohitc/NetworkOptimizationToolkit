package com.network.topology.routing.delaybound.constants;

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

public class RoutePathDelayConstNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = ConstantPrefixes.ROUTE_DELAY;

  private static final Logger log = LoggerFactory.getLogger(RoutePathDelayConstNameGenerator.class);

  public RoutePathDelayConstNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    addValidator(new LPSetContainmentValidator(0, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Destination should be in the set of vertices"));
    //S!=D
    addValidator(new LPDistinctPrefixValidator(0, 1, "S!=D"));
  }
  @Override
  protected void validatePrefixConstraint(List<String> list) throws LPNameException {

  }
}
