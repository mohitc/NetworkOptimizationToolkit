package com.network.topology.traffic.knowntm.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantGroups;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.VarGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public class KnownTmTrafficConstrGroupInitializer extends LPMLGroupInitializer {

	private static final Logger log = LoggerFactory.getLogger(KnownTmTrafficConstrGroupInitializer.class);

	public KnownTmTrafficConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
	}

	@Override
	public void run() throws LPModelException {
		try {
			LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());
			LPNameGenerator capacityVarNameGenerator = model().getLPVarGroup(VarGroups.CAPACITY).getNameGenerator();
			LPNameGenerator trafficMatrixConstNameGenerator = model().getLPConstantGroup(ConstantGroups.TRAFFIC_MAT).getNameGenerator();
			LPNameGenerator routingVarNameGenerator = model().getLPVarGroup(VarGroups.ROUTING).getNameGenerator();

			for (String i : vertices) {
				for (String j : vertices) {
					if (i.equals(j)) {
						continue; //skip self loop case
					}

					LPExpression rhs = new LPExpression(model());
					rhs.addTerm(model().getLPConstant(FixedConstants.ALPHA).getValue(), model().getLPVar(capacityVarNameGenerator.getName(i,j)));

					LPExpression lhs = new LPExpression(model());
					for (String s : vertices) {
						for (String d : vertices) {
							if (s.equals(d))
								continue;
							lhs.addTerm(model().getLPConstant(trafficMatrixConstNameGenerator.getName(s, d)).getValue(), model().getLPVar(routingVarNameGenerator.getName(s, d, i, j)));
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
