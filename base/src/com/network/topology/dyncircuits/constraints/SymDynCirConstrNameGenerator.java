package com.network.topology.dyncircuits.constraints;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPNumberRangeValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SymDynCirConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = ConstraintPrefixes.SYM_DYN_CIRCUIT;

  private static final Logger log = LoggerFactory.getLogger(SymDynCirConstrNameGenerator.class);

  public SymDynCirConstrNameGenerator(int circuitClasses, Set<String> vertexVars) {
    //X(n, i, j)
    super(PREFIX, 3);
    if (circuitClasses <=0) {
      log.error("Circuit classes should be a positive integer (>0). Defaulting to 1");
      circuitClasses = 1;
    }
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    //circuit class is an integer in the set circuitClassSet
    addValidator(new LPPrefixClassValidator(0, Integer.class, "Circuit class should be an integer"));
    addValidator(new LPNumberRangeValidator(0, 1, circuitClasses, "Circuit class not in range [1, " + circuitClasses + "]"));
    //validate nodes
    addValidator(new LPPrefixClassValidator(1, String.class, "Vertex should be of type string"));
    addValidator(new LPPrefixClassValidator(2, String.class, "Vertex should be of type string"));
    //b) both vertices should be in the set of vertexes
    addValidator(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    addValidator(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    addValidator(new LPDistinctPrefixValidator(1, 2, "Source and destination cannot be the same"));

  }

  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
