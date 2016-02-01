package com.network.topology.linkexists.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LinkExistsConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String LINK_EXISTS_CONSTR_PREFIX = ConstraintPrefixes.LINK_EXISTS;

  private static final Logger log = LoggerFactory.getLogger(LinkExistsConstrNameGenerator.class);

  public LinkExistsConstrNameGenerator(Set<String> vertexVars) {
    super(LINK_EXISTS_CONSTR_PREFIX, 3);
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    //add validators
    Set<Integer> constrTypes = new HashSet<>();
    for (int i=1;i<=3;i++)
      constrTypes.add(i);
    //circuit class is an integer in the set circuitClassSet
    addValidator(new LPPrefixClassValidator(0, Integer.class, "Constraint type should be an integer between 1 and 3"));
    addValidator(new LPSetContainmentValidator(0, constrTypes, "Constraint type should be an integer between 1 and 3"));
    //validate nodes
    addValidator(new LPPrefixClassValidator(1, String.class, "Vertex should be of type string"));
    addValidator(new LPPrefixClassValidator(2, String.class, "Vertex should be of type string"));
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(1, 2, "Source and destination cannot be the same"));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
