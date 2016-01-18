package com.network.topology.capacity.constraints;

import java.util.*;

import com.network.topology.VariableBoundConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;


public class ActualCapacityGroupInitializer extends LPGroupInitializer {
	
	private static final Logger log = LoggerFactory.getLogger(ActualCapacityGroupInitializer.class);

	private LPNameGenerator capacityVarNameGenerator, initialCapacityConstNameGenerator,
			dynCircuitVarnameGenerator;

	private Set<String> vertexVars;
	
	public ActualCapacityGroupInitializer(Set<String> vertexVars, LPNameGenerator capacityVarNameGenerator,
										  LPNameGenerator initialCapacityConstNameGenerator,
										  LPNameGenerator dynCircuitVarnameGenerator) {
		if (capacityVarNameGenerator==null) {
	      log.error("Initialized with empty capacity variable name generator");
	      this.capacityVarNameGenerator = new LPEmptyNameGenratorImpl<>();
	    } else {
	      this.capacityVarNameGenerator = capacityVarNameGenerator;
	    }
	    if (initialCapacityConstNameGenerator==null) {
	      log.error("Initialized with empty circuit variable name generator");
		  this.initialCapacityConstNameGenerator = new LPEmptyNameGenratorImpl<>();
	    } else {
	      this.initialCapacityConstNameGenerator = initialCapacityConstNameGenerator;
	    }
		if (dynCircuitVarnameGenerator==null) {
			log.error("Initialized with empty circuit variable name generator");
			this.dynCircuitVarnameGenerator = new LPEmptyNameGenratorImpl<>();
		} else {
			this.dynCircuitVarnameGenerator = dynCircuitVarnameGenerator;
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
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
			int maxCircuitTypes = (int)model().getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();

			for (String i : vertexVars) {
				for (String j : vertexVars) {
					if (i.equals(j)) {
						continue; //skip self loop case
					}
					LPExpression lhs = new LPExpression(model());
					lhs.addTerm(model().getLPVar(capacityVarNameGenerator.getName(i,j)));
					LPExpression rhs = new LPExpression(model());
					rhs.addTerm(model().getLPConstant(initialCapacityConstNameGenerator.getName(i,j)));
					for (int n = 1; n <= maxCircuitTypes; n++){
						double cn = getDynCircuitCapacity(n);
						rhs.addTerm(cn,model().getLPVar(dynCircuitVarnameGenerator.getName(n,i,j)));
					}
					model().addConstraint(generator().getName(i,j), lhs, LPOperator.EQUAL, rhs, group);
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
