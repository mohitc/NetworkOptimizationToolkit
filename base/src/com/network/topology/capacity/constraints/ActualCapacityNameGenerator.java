package com.network.topology.capacity.constraints;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import com.network.topology.ConstraintPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;

public class ActualCapacityNameGenerator extends LPNameGeneratorImpl<String> {
	
	private static final String PREFIX = ConstraintPrefixes.ACTUAL_CAPACITY;

	private static final String LOG_PREFIX = "Capacity:-";

	private static final Logger log = LoggerFactory.getLogger(ActualCapacityNameGenerator.class);

	public ActualCapacityNameGenerator(Set<String> vertexVars) {
		super(PREFIX, 2);
		if (vertexVars==null) {
			log.error("{}Name generator initialized with empty set of vertices", LOG_PREFIX);
			vertexVars = Collections.EMPTY_SET;
		}
		//b) both vertices should be in the set of vertexes
		addValidator(new LPSetContainmentValidator(0, vertexVars, "Index 0 should be in the set of vertices"));
		addValidator(new LPSetContainmentValidator(1, vertexVars, "Index 1 should be in the set of vertices"));
		//a) unique because LinkExists x-x is an invalid variable, and
		addValidator(new LPDistinctPrefixValidator(0, 1, "Both vertices have the same index"));
	}

	@Override
	protected void validatePrefixConstraint(List<String> strings) throws LPNameException {
	}
}
