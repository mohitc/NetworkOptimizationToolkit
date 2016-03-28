package com.network.topology.dyncircuits.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPConstantException;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DynCircuitVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(DynCircuitVarGroupInitializer.class);

  public DynCircuitVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    int circuitClasses = 1;
    try {
      circuitClasses = (int) model().getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();
      if (circuitClasses <= 0) {
        log.error("Circuit classes should be a positive integer (>0). Defaulting to 1");
        circuitClasses = 1;
      }
    } catch (LPConstantException e) {
      log.error("Number of dynamic circuit classes not defined. Defaulting to 1");
    }
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (int n=1; n<=circuitClasses; n++) {
        for (String i: vertices) {
          for (String j: vertices) {
            if (i.equals(j))
              continue;
            this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(n, i, j), LPVarType.INTEGER, 0, model().getLPConstant(FixedConstants.DYN_CIRTUITS_MAX).getValue(), group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Dynamic Circuit variable group", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
