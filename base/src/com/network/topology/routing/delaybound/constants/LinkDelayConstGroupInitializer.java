package com.network.topology.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.topology.algorithm.PathComputationAlgorithm;
import com.topology.algorithm.constraint.PathConstraint;
import com.topology.algorithm.filters.ConnectionFilter;
import com.topology.dto.PathDTO;
import com.topology.primitives.Connection;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.NetworkLayer;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.TopologyException;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.TEPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class LinkDelayConstGroupInitializer extends LPMLGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(LinkDelayConstGroupInitializer.class);

  private static final double MAX_DELAY = 100000;

  private TopologyManager topo;

  public LinkDelayConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
    super(vertices);
    if (topo==null) {
      log.error("Initialized with empty variable topology manager");
    } else {
      this.topo = topo;
    }
  }

  //Link delay is computed based on the delay on the path in the physical topology
  //To compute the delay, we compute the physical path between the nodes, and sum the link delays
  //of all links in path.
  @Override
  public void run() throws LPModelException {
    if (topo==null) {
      throw new LPModelException("Initialized with empty topology manager");
    }
    try {
      TEPropertyKey delayKey = topo.getKey("Delay");
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      for (String i : vertices) {
        for (String j: vertices) {
          if (i.equals(j))
            continue;
          double delay = MAX_DELAY;
          try {
            ConnectionPoint vertI = topo.getSingleElementByLabel(i, ConnectionPoint.class);
            ConnectionPoint vertJ = topo.getSingleElementByLabel(j, ConnectionPoint.class);
            PathComputationAlgorithm algorithm = new PhysicalPathComputationAlgorithm(delayKey);
            PathDTO dto = algorithm.computePath(topo, vertI, vertJ, new PathConstraint(false, true));
            delay = computeDelay(topo, dto.getForwardConnectionSequence(), delayKey);
          } catch (TopologyException e) {
            log.error("Error while computing path from " + i + " to " + j, e);
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

  private double computeDelay(TopologyManager manager, List<Integer> conns, TEPropertyKey delayProp) {
    double delay = 0;
    try {
      for (Integer connID : conns) {
        delay = delay + manager.getElementByID(connID, Connection.class).getProperty(delayProp, Double.class);
      }
      return delay;
    } catch (Exception e) {
      log.error("Error while computing delay. Defaulting to max value", e);
      return MAX_DELAY;
    }
  }

  private class PhysicalPathComputationAlgorithm extends PathComputationAlgorithm {

    private TEPropertyKey delay;

    public PhysicalPathComputationAlgorithm(TEPropertyKey delay) {
      this.delay= delay;
    }

    protected List<ConnectionFilter> getConnectionFilters(ConnectionPoint cEnd, List<Connection> path, PathConstraint constraint) {
      List filterList = super.getConnectionFilters(cEnd, path, constraint);
      filterList.add((ConnectionFilter) connection -> !((connection.getLayer()!=null) && (connection.getLayer()== NetworkLayer.PHYSICAL)));
      return filterList;
    }

    /**Comparator used in path computation to identify position of path that is extended first for computation
     * If value is less than 0, newPath is selected before oldPath, and vice versa
     *
     * @param newPath
     * @param oldPath
     * @return
     */
    public int comparePath(List<Connection> newPath, List<Connection> oldPath) {
      double out = getPathDelay(newPath) - getPathDelay(oldPath);
      return (out<0) ? -1:(out==0)?0:1;
    }

    private double getPathDelay(List<Connection> sequence) {
      return sequence.stream().mapToDouble(v -> {
        try {
          return v.getProperty(delay, Double.class);
        } catch (PropertyException e) {
          log.error("Delay not set on link, return large value");
          return MAX_DELAY;
        }
      }).sum();

    }

  }
}
