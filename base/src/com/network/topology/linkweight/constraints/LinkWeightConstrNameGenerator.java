/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.linkweight.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LinkWeightConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String LINK_WEIGHT_CONSTR_PREFIX = "LW";

  private static final String LINK_WEIGHT_CONSTR_LOG_PREFIX = "LW:- ";

  private static final Logger log = LoggerFactory.getLogger(LinkWeightConstrNameGenerator.class);

  private Set<String> vertexVars;

  public LinkWeightConstrNameGenerator(Set<String> vertexVars) {
    super(LINK_WEIGHT_CONSTR_PREFIX, 2);
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("{}Name generator initialized with empty set of vertices", LINK_WEIGHT_CONSTR_LOG_PREFIX);
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
    //constraint to calculate link weight based on link exists
    //1) W(ij) = LE(ij).hat(w)(ij) + (1-LE(ij).W_INF
    //because prefixes are validated in existing methods, we can iterate over the ones available and check if
    //a) unique because LinkExists x-x is an invalid variable, and
    //b) both a and be should be in the set of vertexes
    if (strings.get(0).equals(strings.get(1))) {
      throw new LPNameException("Both vertices have the same index");
    }
    if (! (vertexVars.contains(strings.get(0)) && (vertexVars.contains(strings.get(1))))) {
      throw new LPNameException("Both vertices have to be in the set of vertices for the graph");
    }
  }
}
