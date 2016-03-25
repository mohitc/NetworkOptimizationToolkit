package com.network.topology.linkweight.constraints;

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

public class LinkWeightConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkWeightConstrGroupInitializer.class);

  public LinkWeightConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    //Set<Link> links = manager.getAllElements(Link.class);
    try {
      LPNameGenerator linkExistsNameGenerator = model().getLPVarGroup(VarGroups.LINK_EXISTS).getNameGenerator();
      LPNameGenerator linkWeightNameGenerator = model().getLPVarGroup(VarGroups.LINK_WEIGHT).getNameGenerator();
      LPNameGenerator linkWeightConstantNameGenerator = model().getLPConstantGroup(ConstantGroups.LINK_WEIGHT).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

      double wInf = model().getLPConstant(FixedConstants.W_INF).getValue();
      for (String s: vertices) {
        for (String d: vertices) {
          if (s.equals(d))
            continue;
          LPExpression lhs = new LPExpression(model());
          lhs.addTerm(model().getLPVar(linkWeightNameGenerator.getName(s, d)));
          LPExpression rhs = new LPExpression(model());
          rhs.addTerm(model().getLPConstant(linkWeightConstantNameGenerator.getName(s,d)).getValue() - wInf, model().getLPVar(linkExistsNameGenerator.getName(s, d)));
          rhs.addTerm(wInf);
          model().addConstraint(generator().getName(s, d), lhs, LPOperator.EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
