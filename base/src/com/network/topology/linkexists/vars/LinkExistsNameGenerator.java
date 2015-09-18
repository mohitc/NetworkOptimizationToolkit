package com.network.topology.linkexists.vars;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariablePrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LinkExistsNameGenerator extends LPNameGeneratorImpl<String> {

  private static final String PREFIX = VariablePrefixes.LINK_EXISTS;

  private static final String LOG_PREFIX = "LE:- ";

  private static final Logger log = LoggerFactory.getLogger(LinkExistsNameGenerator.class);

  private Set<String> vertexVars;

  public LinkExistsNameGenerator(Set<String> vertexVars) {
    super(PREFIX, 2);
    if (vertexVars!=null) {
      this.vertexVars = Collections.unmodifiableSet(vertexVars);
    } else {
      log.error("{}Name generator initialized with empty set of vertices", LOG_PREFIX);
      this.vertexVars = Collections.EMPTY_SET;
    }
  }

  @Override
  protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
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
