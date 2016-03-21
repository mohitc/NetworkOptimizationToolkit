package com.network.topology.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RoutePathDelayConstGroupInitializer extends LPGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(RoutePathDelayConstGroupInitializer.class);

  private Set<String> vertices;

  public RoutePathDelayConstGroupInitializer(Set<String> vertices) {
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
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      double delay = 4000000;
      for (String s : vertices) {
        for (String d : vertices) {
          if (s.equals(d))
            continue;
          model().createLpConstant(generator().getName(s, d), delay, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
