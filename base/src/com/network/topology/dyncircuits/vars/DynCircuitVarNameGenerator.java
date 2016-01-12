/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.dyncircuits.vars;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariablePrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DynCircuitVarNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = VariablePrefixes.DYN_CIRTUIT;

  private static final Logger log = LoggerFactory.getLogger(DynCircuitVarNameGenerator.class);

  private Set<String> vertexVars;

  private int circuitClasses;

  public DynCircuitVarNameGenerator(int circuitClasses, Set<String> vertexVars) {
    //X(n, i, j)
    super(PREFIX, 3);
    if (circuitClasses <=0) {
      log.error("Circuit classes should be a positive integer (>0). Defaulting to 1");
      this.circuitClasses = 1;
    } else {
      this.circuitClasses = circuitClasses;
    }
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("Name generator initialized with empty set of vertices");
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
    //X (n , i, j) indicates if a dynamic circuit of class n is created between nodes i and j
    //a) Prefixes 1 and 2 should be in the vertices
    for (int i=1; i<=2; i++) {
      if (!vertexVars.contains(strings.get(i))) {
        throw new LPNameException("Vertex " + strings.get(i) + "  should to be in the set of vertices for the graph");
      }
    }

    //c) Link indices should be unique (i!=j)
    if (strings.get(1).equals(strings.get(2))) {
      throw new LPNameException("Endpoint of a link for routing cannot be the same");
    }

    try {
      int n = Integer.parseInt(strings.get(0));
      if (! (n>0 && n<= circuitClasses)) {
        throw new LPNameException("Class of optical circuit should be an integer between 1 and " + circuitClasses);
      }
    } catch (NumberFormatException e) {
      throw new LPNameException("class of optical circuit should be an integer");
    }
  }
}