package com.network.topology.capacity.constraints;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;

public class ActualCapacityNameGenerator extends LPNameGeneratorImpl<String> {
	
	private static final String PREFIX = "CAPACITY";

	private static final Logger log = LoggerFactory.getLogger(ActualCapacityNameGenerator.class);

	private Set<String> vertexVars;
	
	public ActualCapacityNameGenerator(String prefix, int indexCount) {
		super(PREFIX, 2);
	    if (vertexVars!=null) {
		      this.vertexVars = Collections.unmodifiableSet(vertexVars);
	    } else {
	      log.error("Name generator initialized with empty set of vertices");
	      this.vertexVars = Collections.EMPTY_SET;
	    }
	}

	@Override
	protected void validatePrefixConstraint(List<String> strings)
			throws LPNameException {
		// TODO Auto-generated method stub
		//because prefixes are validated in existing methods, we can iterate over the ones available and check if for R (s,d,i,j)
	    //a) All prefixes should be in the vertices
	    for (String vertex: strings) {
	      if (!vertexVars.contains(vertex)) {
	        throw new LPNameException("Vertex " + vertex + "  should to be in the set of vertices for the graph");
	      }
	    }

	    //b) Source and Destination should be unique (i!=j)
	    if (strings.get(0).equals(strings.get(1))) {
	      throw new LPNameException("Source and destination cannot be the same");
	    }
	}
}
