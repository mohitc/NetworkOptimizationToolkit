/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.linkexists.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariableBoundConstants;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Initialize a group of 3 constraints to ensure that a Link Exists
 *  1) LE(ij) >=hat(LE)(ij)
 *  2) LE(ij) >=Sum[X(n)(ij)] / M
 *  3) LE(ij) <= hat(LE)(ij) + Sum[X(n)(ij)]
 */
public class LinkExistsConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkExistsConstrGroupInitializer.class);

  private Set<String> vertexVars;

  private LPNameGenerator linkExistsNameGenerator, fixedLinkExistsNameGenerator, dynCircuitNameGenerator;

  public LinkExistsConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator linkExistsNameGenerator, LPNameGenerator fixedLinkExistsNameGenerator,
                                          LPNameGenerator dynCircuitNameGenerator) {
    if (vertexVars!=null) {
      this.vertexVars= vertexVars;
    } else {
      log.error("Null topology manager provided for initializing constraints");
      vertexVars = Collections.EMPTY_SET;
    }
    if (linkExistsNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkExistsNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkExistsNameGenerator = linkExistsNameGenerator;
    }
    if (fixedLinkExistsNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.fixedLinkExistsNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.fixedLinkExistsNameGenerator = fixedLinkExistsNameGenerator;
    }
    if (dynCircuitNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.dynCircuitNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.dynCircuitNameGenerator = dynCircuitNameGenerator;
    }
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstraintGroup group = this.getGroup().getModel().getLPConstraintGroup(this.getGroup().getIdentifier());
      double maxCircuits = model().getLPConstant(VariableBoundConstants.DYN_CIRTUITS_MAX).getValue();
      if (maxCircuits<1) {
        log.error("Max number of dynamic circuits not initialized correctly, defaulting to 1");
        maxCircuits = 1;
      }
      int vertexClasses = (int)model().getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();

      for (String i: vertexVars) {
        for (String j: vertexVars) {
          if (i.equals(j))
            continue;

          //Constr 1
          //LE(ij) >=hat(LE)(ij)
          LPExpression lhs1 = new LPExpression(model());
          lhs1.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));
          LPExpression rhs1 = new LPExpression(model());
          rhs1.addTerm(model().getLPConstant(fixedLinkExistsNameGenerator.getName(i, j)));
          model().addConstraint(generator().getName("1", i, j), lhs1, LPOperator.GREATER_EQUAL, rhs1, group);

          //Constr 2
          //LE(ij) >=Sum[X(n)(ij)] / M
          LPExpression lhs2 = new LPExpression(model());
          lhs2.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));
          LPExpression rhs2 = new LPExpression(model());
          double coeff = 1/ maxCircuits;
          for (int n=1; n <= vertexClasses; n++) {
            rhs2.addTerm(coeff, model().getLPVar(dynCircuitNameGenerator.getName(i, j, n)));
          }
          model().addConstraint(generator().getName("2", i, j), lhs2, LPOperator.GREATER_EQUAL, rhs2, group);

          LPExpression lhs3 = new LPExpression(model());
          lhs3.addTerm(model().getLPVar(linkExistsNameGenerator.getName(i, j)));

          LPExpression rhs3 = new LPExpression(model());
          for (int n=1; n <= vertexClasses; n++) {
            rhs3.addTerm(model().getLPVar(dynCircuitNameGenerator.getName(i, j, n)));
          }
          rhs3.addTerm(model().getLPConstant(fixedLinkExistsNameGenerator.getName(i, j)));
          model().addConstraint(generator().getName("3", i, j), lhs3, LPOperator.LESS_EQUAL, rhs3, group);

        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
