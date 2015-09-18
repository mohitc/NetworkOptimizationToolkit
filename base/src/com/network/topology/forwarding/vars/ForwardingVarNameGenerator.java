package com.network.topology.forwarding.vars;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
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

  private Set<String> vertexVars;

  public ForwardingVarNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 3);
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("Name generator initialized with empty set of vertices");
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
    //F (d , i, j) indicates if at node i, route to destination d uses the next hop j
    //a) All prefixes should be in the vertices
    for (String vertex: strings) {
      if (!vertexVars.contains(vertex)) {
        throw new LPNameException("Vertex " + vertex + "  should to be in the set of vertices for the graph");
      }
    }

    //b) Source and Destination should be unique (i!=d)
    if (strings.get(0).equals(strings.get(1))) {
      throw new LPNameException("Source and destination cannot be the same");
    }

    //c) Link indices should be unique (i!=j)
    if (strings.get(1).equals(strings.get(2))) {
      throw new LPNameException("Endpoint of a link for routing cannot be the same");
    }

  }
}
