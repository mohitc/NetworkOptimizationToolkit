package com.network.topology.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.topology.primitives.Connection;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class LinkDelayConstGroupInitializer extends LPGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(LinkDelayConstGroupInitializer.class);

  private Set<String> vertices;

  private TopologyManager topo;

  public LinkDelayConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
    if (vertices==null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }

    if (topo==null) {
      log.error("Initialized with empty variable topology manager");
    } else {
      this.topo = topo;
    }
  }

  @Override
  public void run() throws LPModelException {
    if (topo==null) {
      throw new LPModelException("Initialized with empty topology manager");
    }
    try {
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      for (String i : vertices) {
        ConnectionPoint vertI = topo.getSingleElementByLabel(i, ConnectionPoint.class);
        Set<Connection> connections = vertI.getConnections();
        for (String j : vertices) {
          if (i.equals(j))
            continue;

          double delay = 1; //D INF
          for (Connection conn: connections) {
            if (conn.getaEnd().getLabel().equals(j) || conn.getzEnd().getLabel().equals(j)) {
              delay = 1;
            }
          }
          model().createLpConstant(generator().getName(i, j), delay, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    } catch (TopologyException e) {
      log.error("Exception while parsing the topology:", e);
      throw new LPModelException("Exception while parsing the topology: " + e.getMessage());
    }
  }
}
