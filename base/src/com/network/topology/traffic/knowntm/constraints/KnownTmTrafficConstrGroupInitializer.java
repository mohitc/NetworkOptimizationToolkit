package com.network.topology.traffic.knowntm.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;


public class KnownTmTrafficConstrGroupInitializer extends LPGroupInitializer {

	private static final Logger log = LoggerFactory.getLogger(KnownTmTrafficConstrGroupInitializer.class);

	private LPNameGenerator capacityVarNameGenerator, demandedCapacityConstNameGenerator,
			routingVarNameGenerator;

	private Set<String> vertexVars;

	public KnownTmTrafficConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator capacityVarNameGenerator,
																							LPNameGenerator demandedCapacityConstNameGenerator,
																							LPNameGenerator routingVarNameGenerator) {
		if (capacityVarNameGenerator==null) {
			log.error("Initialized with empty capacity variable name generator");
			this.capacityVarNameGenerator = new LPEmptyNameGenratorImpl<>();
		} else {
			this.capacityVarNameGenerator = capacityVarNameGenerator;
		}
		if (demandedCapacityConstNameGenerator==null) {
			log.error("Initialized with empty circuit variable name generator");
			this.demandedCapacityConstNameGenerator = new LPEmptyNameGenratorImpl<>();
		} else {
			this.demandedCapacityConstNameGenerator = demandedCapacityConstNameGenerator;
		}
		if (routingVarNameGenerator==null) {
			log.error("Initialized with empty circuit variable name generator");
			this.routingVarNameGenerator = new LPEmptyNameGenratorImpl<>();
		} else {
			this.routingVarNameGenerator = routingVarNameGenerator;
		}
		if (vertexVars!=null) {
			this.vertexVars = Collections.unmodifiableSet(vertexVars);
		} else {
			log.error("Constraint generator initialized with empty set of vertices");
			this.vertexVars = Collections.EMPTY_SET;
		}
	}

	@Override
	public void run() throws LPModelException {
		try {
			//Constraint 11 //FIXME: no, this relates to demanded capacity... not 11 (not in the formulation at all)
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

			for (String i : vertexVars) {
				for (String j : vertexVars) {
					if (i.equals(j)) {
						continue; //skip self loop case
					}

					LPExpression rhs = new LPExpression(model());
					rhs.addTerm(model().getLPConstant(FixedConstants.ALPHA).getValue(), model().getLPVar(capacityVarNameGenerator.getName(i,j)));

					LPExpression lhs = new LPExpression(model());
					for (String s : vertexVars) {
						if (s.equals(j))
							continue;
						for (String d : vertexVars) {
							if (i.equals(d) || s.equals(d))
								continue;
							lhs.addTerm(model().getLPConstant(demandedCapacityConstNameGenerator.getName(s, d)), model().getLPVar(routingVarNameGenerator.getName(s, d, i, j)));
						}
					}

					model().addConstraint(generator().getName(i,j), lhs, LPOperator.LESS_EQUAL, rhs, group);
				}
			}
		} catch (LPNameException e) {
			log.error("Variable name not found: " + e.getMessage());
			throw new LPModelException("Variable name not found: " + e.getMessage());
		}
	}

}
