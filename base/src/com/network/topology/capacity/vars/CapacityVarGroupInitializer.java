package com.network.topology.capacity.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VariableBoundConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class CapacityVarGroupInitializer extends LPGroupInitializer {

    private static final Logger log = LoggerFactory.getLogger(CapacityVarGroupInitializer.class);

    private Set<String> vertices;

    public CapacityVarGroupInitializer(Set<String> vertices) {
    if (vertices==null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }

  }

    @Override
    public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      double capInf = model().getLPConstant(VariableBoundConstants.CAP_MAX).getValue();

      for (String vertex1 : vertices) {
        for (String vertex2 : vertices) {
          //for all distinct pair of vertices, a capacity can possibly have a value between 0 and C_MAX
          if (vertex1.equals(vertex2))
            continue;
          this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(vertex1, vertex2), LPVarType.DOUBLE, 0, capInf, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Link Exists variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}