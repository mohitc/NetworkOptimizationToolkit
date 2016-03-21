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

public class RouterDelayConstGroupInitializer extends LPGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(RouterDelayConstGroupInitializer.class);

  private Set<String> vertices;

  private TopologyManager topo;

  public RouterDelayConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
    if (vertices==null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }

    if (topo==null) {
      log.error("Initialized with empty variable topology manager");
      this.topo = new TopologyManagerImpl("New");
    } else {
      this.topo = topo;
    }
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      for (String i : vertices) {
        double delay = 10;
        model().createLpConstant(generator().getName(i), delay, group);
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
