/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.linkweight.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.ConstantPrefixes;
import com.network.topology.VariableBoundConstants;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LinkWeightConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkWeightConstrGroupInitializer.class);

  private Set<String> vertexVars;

  private LPNameGenerator linkExistsNameGenerator, linkWeightConstantNameGenerator, linkWeightNameGenerator;

  public LinkWeightConstrGroupInitializer(Set<String> vertexVars, LPNameGenerator linkWeightNameGenerator, LPNameGenerator linkExistsNameGenerator, LPNameGenerator linkWeightConstantNameGenerator) {
    if (vertexVars!=null) {
      this.vertexVars= vertexVars;
    } else {
      log.error("Null topology manager provided for initializing constraints");
      this.vertexVars = Collections.EMPTY_SET;
    }
    if (linkWeightNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkWeightNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkWeightNameGenerator = linkWeightNameGenerator;
    }

    if (linkExistsNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkExistsNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkExistsNameGenerator = linkExistsNameGenerator;
    }
    if (linkWeightConstantNameGenerator==null) {
      log.error("Initialized with empty variable name generator");
      this.linkWeightConstantNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkWeightConstantNameGenerator = linkWeightConstantNameGenerator;
    }
  }

  @Override
  public void run() throws LPModelException {
    //Set<Link> links = manager.getAllElements(Link.class);
    try {

      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

      double wInf = model().getLPConstant(VariableBoundConstants.W_INF).getValue();
      for (String s: vertexVars) {
        for (String d: vertexVars) {
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
