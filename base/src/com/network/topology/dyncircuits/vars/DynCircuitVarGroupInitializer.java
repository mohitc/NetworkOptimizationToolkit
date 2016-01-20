package com.network.topology.dyncircuits.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPConstantException;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariableBoundConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class DynCircuitVarGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DynCircuitVarGroupInitializer.class);

  private Set<String> vertices;

  private int circuitClasses;

  public DynCircuitVarGroupInitializer(Set<String> vertices) {
    if (vertices == null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }
  }

  @Override
  public void run() throws LPModelException {
    try {
      int circuitClasses = (int) model().getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();
      if (circuitClasses <= 0) {
        log.error("Circuit classes should be a positive integer (>0). Defaulting to 1");
        this.circuitClasses = 1;
      } else {
        this.circuitClasses = circuitClasses;
      }
    } catch (LPConstantException e) {
      log.error("Number of dynamic circuit classes not defined. Defaulting to 1");
      this.circuitClasses = 1;
    }
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (int n=1; n<=circuitClasses; n++) {
        for (String i: vertices) {
          for (String j: vertices) {
            if (i.equals(j))
              continue;
            this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(n, i, j), LPVarType.INTEGER, 0, model().getLPConstant(VariableBoundConstants.DYN_CIRTUITS_MAX).getValue(), group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Dynamic Circuit variable group", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
