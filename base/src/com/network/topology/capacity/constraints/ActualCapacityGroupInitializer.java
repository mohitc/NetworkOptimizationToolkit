package com.network.topology.capacity.constraints;

import java.lang.reflect.Field;
import java.util.*;

import com.network.topology.models.fixedtopology.FixedTopologyModel;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.TEPropertyKey;
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
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrGroupInitializer;


public class ActualCapacityGroupInitializer extends LPGroupInitializer {
	
	private static final Logger log = LoggerFactory.getLogger(ActualCapacityGroupInitializer.class);

	private LPNameGenerator capacityNameGenerator, circuitNameGenerator;

	private Set<String> vertexVars;
	
	public ActualCapacityGroupInitializer(Set<String> vertexVars, LPNameGenerator capacityNameGenerator, LPNameGenerator circuitNameGenerator) {
		if (capacityNameGenerator==null) {
	      log.error("Initialized with empty capacity variable name generator");
	      this.capacityNameGenerator = new LPEmptyNameGenratorImpl<>();
	    } else {
	      this.capacityNameGenerator = capacityNameGenerator;
	    }
	    if (circuitNameGenerator==null) {
	      log.error("Initialized with empty circuit variable name generator");
//	      this.circuitNameGenerator = new circuitNameGenerator<>();
	    } else {
	      this.circuitNameGenerator = circuitNameGenerator;
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
		// TODO Auto-generated method stub
		try {
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
			TopologyManager topo;
			try {
				Field this$0 = model().getClass().getDeclaredField("this$0");
				FixedTopologyModel outer = (FixedTopologyModel) this$0.get(model().getClass()); //TODO: this is an ugly hack
				topo = outer._instance;
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			TEPropertyKey demandStoreKey;
			Map<String, Map<String, String>> demandStore;
			try {
				demandStoreKey = topo.getKey("Demands");
				demandStore = (Map<String, Map<String, String>>) topo.getProperty(demandStoreKey);
			} catch (PropertyException e) {
				e.printStackTrace();
				throw new LPModelException("Demand store not found: " + e.getMessage());
			}

			//for each demand, we want capacity between two vertices to be greater than the demand
			for (String i : vertexVars) {
				for (String j : vertexVars) {
					String label = i.concat(j);
					if (demandStore.containsKey(label)){
						Map<String, String> demand = demandStore.get(label);
						LPExpression rhs = new LPExpression(model());
						//actual capacity grater than demanded
						rhs.addTerm(model().getLPVar(capacityNameGenerator.getName(i, j)));

					}
				}
			}


//			for (String i : vertexVars) {
//				for (String j : vertexVars) {
//					if (i.equals(j)){
//						continue;
//					}
//					//For each couple of vertexes, capacity equals existing capacity (starts at 0) plus the sum of the circuits established betweent hose endpoints;
//					LPExpression lhs = new LPExpression(model());
//					lhs.addTerm(model().getLPVar(capacityNameGenerator.getName(i, j)));
//
//					LPExpression rhs = new LPExpression(model());
//					for (String n : TODO){
//
//					}
//		              rhs.addTerm(model().getLPVar(forwardingNameGenerator.getName(d, i, j)));
//		              model().addConstraint(generator().getName(s,d,i,j), lhs, LPOperator.LESS_EQUAL, rhs, group);
//				}
//			}
		} catch (LPNameException e) {
			log.error("Variable name not found: " + e.getMessage());
		    throw new LPModelException("Variable name not found: " + e.getMessage());
		}
	 }


}
