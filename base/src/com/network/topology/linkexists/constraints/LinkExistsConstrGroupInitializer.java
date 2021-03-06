package com.network.topology.linkexists.constraints;

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

/** Initialize a group of 3 constraints to ensure that a Link Exists
 *  1) LE(ij) >=hat(LE)(ij)
 *  2) LE(ij) >=Sum[X(n)(ij)] / M
 *  3) LE(ij) <= hat(LE)(ij) + Sum[X(n)(ij)]
 */
public class LinkExistsConstrGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkExistsConstrGroupInitializer.class);

  public LinkExistsConstrGroupInitializer(Set<String> vertexVars) {
    super(vertexVars);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPNameGenerator linkExistsNameGenerator = model().getLPVarGroup(VarGroups.LINK_EXISTS).getNameGenerator();
      LPNameGenerator fixedLinkExistsNameGenerator = model().getLPConstantGroup(ConstantGroups.LINK_EXISTS).getNameGenerator();
      LPNameGenerator dynCircuitNameGenerator = model().getLPVarGroup(VarGroups.DYN_CIRCUITS).getNameGenerator();

      LPConstraintGroup group = this.getGroup().getModel().getLPConstraintGroup(this.getGroup().getIdentifier());
      double maxCircuits = model().getLPConstant(FixedConstants.DYN_CIRTUITS_MAX).getValue();
      if (maxCircuits<1) {
        log.error("Max number of dynamic circuits not initialized correctly, defaulting to 1");
        maxCircuits = 1;
      }
      int vertexClasses = (int)model().getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();

      for (String i: vertices) {
        for (String j: vertices) {
          if (i.equals(j))
            continue;

          //Constr 1
          //LE(ij) >=hat(LE)(ij)
          LPExpression lhs1 = new LPExpression(model());
          lhs1.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));
          LPExpression rhs1 = new LPExpression(model());
          rhs1.addTerm(model().getLPConstant(fixedLinkExistsNameGenerator.getName(i, j)));
          model().addConstraint(generator().getName(1, i, j), lhs1, LPOperator.GREATER_EQUAL, rhs1, group);

          //Constr 2
          //LE(ij) >=Sum[X(n)(ij)] / M
          LPExpression lhs2 = new LPExpression(model());
          lhs2.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));
          LPExpression rhs2 = new LPExpression(model());
          double coeff = 1/ maxCircuits;
          for (int n=1; n <= vertexClasses; n++) {
            rhs2.addTerm(coeff, model().getLPVar(dynCircuitNameGenerator.getName(n, i, j)));
          }
          model().addConstraint(generator().getName(2, i, j), lhs2, LPOperator.GREATER_EQUAL, rhs2, group);

          LPExpression lhs3 = new LPExpression(model());
          lhs3.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));

          LPExpression rhs3 = new LPExpression(model());
          for (int n=1; n <= vertexClasses; n++) {
            rhs3.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(n, i, j)));
          }
          rhs3.addTerm(model().getLPConstant(fixedLinkExistsNameGenerator.getName(i, j)));
          model().addConstraint(generator().getName(3, i, j), lhs3, LPOperator.LESS_EQUAL, rhs3, group);

        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
