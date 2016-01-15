/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.linkexists.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LinkExistsConstrNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String LINK_EXISTS_CONSTR_PREFIX = "LE";

  private static final String LINK_EXISTS_CONSTR_LOG_PREFIX = "LE:- ";

  private static final Logger log = LoggerFactory.getLogger(LinkExistsConstrNameGenerator.class);

  private Set<String> vertexVars;

  public LinkExistsConstrNameGenerator(Set<String> vertexVars) {
    super(LINK_EXISTS_CONSTR_PREFIX, 3);
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("{}Name generator initialized with empty set of vertices", LINK_EXISTS_CONSTR_LOG_PREFIX);
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
    //first prefix is the type of constranit
    //1) LE(ij) >=hat(LE)(ij)
    //2) LE(ij) >=Sum[X(n)(ij)] / M
    //3) LE(ij) <= hat(LE)(ij) + Sum[X(n)(ij)]
    //because prefixes are validated in existing methods, we can iterate over the ones available and check if
    //a) unique because LinkExists x-x is an invalid variable, and
    //b) both a and be should be in the set of vertexes
    if (strings.get(1).equals(strings.get(2))) {
      throw new LPNameException("Both vertices have the same index");
    }
    if (! (vertexVars.contains(strings.get(1)) && (vertexVars.contains(strings.get(2))))) {
      throw new LPNameException("Both vertices have to be in the set of vertices for the graph");
    }
    try {
      int eqType = Integer.parseInt(strings.get(0));
      if (eqType<1 || eqType >3)
        throw new LPNameException("First index should be an integer between [1,3]");
    } catch (NumberFormatException e) {
      throw new LPNameException("First index should be an integer between [1,3]");
    }
  }
}
