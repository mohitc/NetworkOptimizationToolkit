package com.network.topology.capacity.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariableBoundConstants;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;


public class ActualDemandedCapacityGroupInitializer extends LPGroupInitializer {

	private static final Logger log = LoggerFactory.getLogger(ActualDemandedCapacityGroupInitializer.class);

	private LPNameGenerator capacityVarNameGenerator, demandedCapacityConstNameGenerator,
			routingVarNameGenerator;

	private TopologyManager topo;

	private Set<String> vertexVars;

	public ActualDemandedCapacityGroupInitializer(Set<String> vertexVars, LPNameGenerator capacityVarNameGenerator,
																								LPNameGenerator demandedCapacityConstNameGenerator,
																								LPNameGenerator routingVarNameGenerator,
																								TopologyManager topo) {
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
		if (topo==null) {
			log.error("Initialized with empty circuit variable name generator");
		} else {
			this.topo = topo;
		}
	}

	@Override
	public void run() throws LPModelException {
		try {
			//Constraint 11 //FIXME: no, this relates to demanded capacity... not 11 (not in the formulation at all)
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
			int maxCircuitTypes = (int)model().getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();

			for (String i : vertexVars) {
				for (String j : vertexVars) {
					if (i.equals(j)) {
						continue; //skip self loop case
					}

					LPExpression rhs = new LPExpression(model());
					rhs.addTerm(model().getLPVar(capacityVarNameGenerator.getName(i,j)));

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

	static public double getDynCircuitCapacity(int n){
		switch(n){
			case 1:
				return 2.5;
			case 2:
				return 10;
			case 3:
				return 40;
			case 4:
				return 100;
			default:
				log.error("Unable to convert circuit to capacity: " + Integer.toString(n));
				//TODO: should probably thrown an exception here
		}
		return 0;
	}


}
