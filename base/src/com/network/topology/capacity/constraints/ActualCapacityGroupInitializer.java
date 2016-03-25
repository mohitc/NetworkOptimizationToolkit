package com.network.topology.capacity.constraints;

import java.util.*;

import com.network.topology.ConstantGroups;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import com.network.topology.dyncircuits.parser.DynCircuitClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;


public class ActualCapacityGroupInitializer extends LPMLGroupInitializer {

	private static final Logger log = LoggerFactory.getLogger(ActualCapacityGroupInitializer.class);

	private Map<Integer, DynCircuitClass> circuitClassMap;

	public ActualCapacityGroupInitializer(Set<String> vertexVars, Map<Integer, DynCircuitClass> circuitClassMap) {
		super(vertexVars);
		if (circuitClassMap==null) {
			log.error("Circuit class map provided is empty. Defaulting to Empty Map");
			this.circuitClassMap = Collections.EMPTY_MAP;
		} else {
			this.circuitClassMap = circuitClassMap;
		}

	}

	@Override
	public void run() throws LPModelException {
		try {
			LPNameGenerator dynCircuitNameGenerator = model().getLPVarGroup(VarGroups.DYN_CIRCUITS).getNameGenerator();
			LPNameGenerator capacityVarNameGenerator = model().getLPVarGroup(VarGroups.CAPACITY).getNameGenerator();
			LPNameGenerator initialCapacityConstNameGenerator = model().getLPConstantGroup(ConstantGroups.INITIAL_CAP).getNameGenerator();
			//Constraint 10
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
			int maxCircuitTypes = (int)model().getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();

			for (String i : vertices) {
				for (String j : vertices) {
					if (i.equals(j)) {
						continue; //skip self loop case
					}
					LPExpression lhs = new LPExpression(model());
					lhs.addTerm(model().getLPVar(capacityVarNameGenerator.getName(i,j)));
					LPExpression rhs = new LPExpression(model());
					rhs.addTerm(model().getLPConstant(initialCapacityConstNameGenerator.getName(i,j)));
					for (int n = 1; n <= maxCircuitTypes; n++){
						double cn = getDynCircuitCapacity(n);
						rhs.addTerm(cn,model().getLPVar(dynCircuitNameGenerator.getName(n,i,j)));
					}
					model().addConstraint(generator().getName(i,j), lhs, LPOperator.EQUAL, rhs, group);
				}
			}
		} catch (LPNameException e) {
			log.error("Variable name not found: " + e.getMessage());
			throw new LPModelException("Variable name not found: " + e.getMessage());
		}
	}

	public double getDynCircuitCapacity(int n){
		if (circuitClassMap.containsKey(n)) {
			return circuitClassMap.get(n).getCapacity();
		} else {
			log.error("Invalid circuit class used: " + n);
			return 0;
		}
	}


}
