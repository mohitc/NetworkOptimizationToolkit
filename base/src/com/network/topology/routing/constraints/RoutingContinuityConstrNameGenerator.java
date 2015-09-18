package com.network.topology.routing.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RoutingContinuityConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String ROUTING_CONTINUITY_CONSTR_PREFIX = "ROUTING_CONTINUITY_Const";

  private static final String ROUTING_CONTINUITY_CONSTR_LOG_PREFIX = "ROUTING_CONTINUITY:- ";

  private static final Logger log = LoggerFactory.getLogger(RoutingContinuityConstrNameGenerator.class);

  private Set<String> vertexVars;

  public RoutingContinuityConstrNameGenerator(Set<String> vertexVars) {
    super(ROUTING_CONTINUITY_CONSTR_PREFIX, 3);
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("{}Name generator initialized with empty set of vertices", ROUTING_CONTINUITY_CONSTR_LOG_PREFIX);
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
    //because prefixes are validated in existing methods, we can iterate over the ones available and check if
    //a) All prefixes should be in the vertices
    for (String vertex: strings) {
      if (!vertexVars.contains(vertex)) {
        throw new LPNameException("Vertex " + vertex + "  should to be in the set of vertices for the graph");
      }
    }

    //b) Source and Destination should be unique (s!=d)
    if (strings.get(0).equals(strings.get(1))) {
      throw new LPNameException("Source and destination cannot be the same");
    }
 }
}
