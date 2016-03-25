package com.network.topology.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RouterDelayConstGroupInitializer extends LPMLGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(RouterDelayConstGroupInitializer.class);

  private TopologyManager topo;

  public RouterDelayConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
    super(vertices);

    if (topo==null) {
      log.error("Initialized with empty variable topology manager");
      this.topo = new TopologyManagerImpl("New");
    } else {
      this.topo = topo;
    }
  }

  @Override
  public void run() throws LPModelException {
    //TODO extract from topology
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
