package com.network.topology.capacity.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.TEPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class InitialCapacityConstGroupInitializer extends LPGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(InitialCapacityConstGroupInitializer.class);

  private Set<String> vertices;

  private TopologyManager topo;

  public InitialCapacityConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
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
        for (String j : vertices) {
          if (i.equals(j))
            continue;
          //current default initialization to 0
          model().createLpConstant(generator().getName(i, j), 0, group);
          //TODO add check to read capacity from topology links
        }
      }
    }catch (LPNameException e) {
      log.error("Constant name not found: " + e.getMessage());
      throw new LPModelException("Constant name not found: " + e.getMessage());
    }
  }
}
