package com.network.topology.capacity.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class CapacityVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(CapacityVarGroupInitializer.class);

  public CapacityVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      double capInf = model().getLPConstant(FixedConstants.CAP_MAX).getValue();

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
