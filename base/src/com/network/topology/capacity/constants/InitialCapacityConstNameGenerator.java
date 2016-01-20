package com.network.topology.capacity.constants;

import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantPrefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by fpederzolli on 16/01/16.
 */
public class InitialCapacityConstNameGenerator extends LPNameGeneratorImpl<String> {
    private static final String PREFIX = ConstantPrefixes.INITIAL_CAPACITY;

    private static final Logger log = LoggerFactory.getLogger(InitialCapacityConstNameGenerator.class);

    private Set<String> vertexVars;

    public InitialCapacityConstNameGenerator(Set<String> vertexVars) {
        super(PREFIX, 2);
        if (vertexVars!=null) {
            this.vertexVars = Collections.unmodifiableSet(vertexVars);
        } else {
            log.error("Name generator initialized with empty set of vertices");
            this.vertexVars = Collections.EMPTY_SET;
        }
    }

    @Override
    protected void validatePrefixConstraint(List<String> list) throws LPNameException {
        //C(i,j) indicates minimum capacity to be supported between i and j
        //a) All prefixes should be in the vertices
        for (String vertex: list) {
            if (!vertexVars.contains(vertex)) {
                throw new LPNameException("Vertex " + vertex + "  should be in the set of vertices for the graph");
            }
        }

        //b) Source and Destination should be unique (i!=j)
        if (list.get(0).equals(list.get(1))) {
            throw new LPNameException("Endpoint of a link cannot be the same");
        }
    }














}
