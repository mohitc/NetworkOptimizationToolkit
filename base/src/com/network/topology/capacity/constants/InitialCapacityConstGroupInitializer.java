package com.network.topology.capacity.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.NetworkLayer;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.connresource.BandwidthConnectionResource;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InitialCapacityConstGroupInitializer extends LPMLGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(InitialCapacityConstGroupInitializer.class);

  private TopologyManager topo;

  public InitialCapacityConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
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
    try {
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      for (String i : vertices) {
        for (String j : vertices) {
          if (i.equals(j))
            continue;

          ConnectionPoint cp1 = topo.getSingleElementByLabel(i, ConnectionPoint.class);
          ConnectionPoint cp2 = topo.getSingleElementByLabel(j, ConnectionPoint.class);

          List<Link> ipConns = cp1.getConnections(NetworkLayer.IP, Link.class).stream().filter(v -> v.getaEnd()==cp2 || v.getzEnd() == cp2).collect(Collectors.toList());
          if (ipConns!=null && ipConns.size()==1) {
            model().createLpConstant(generator().getName(i, j), ((BandwidthConnectionResource)ipConns.get(0).getTotalResources()).getBandwidth(), group);
          } else {
            model().createLpConstant(generator().getName(i, j), 0, group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Constant name not found: " + e.getMessage());
      throw new LPModelException("Constant name not found: " + e.getMessage());
    } catch (TopologyException e) {
      log.error("Connection point could not be found", e);
      throw new LPModelException("Connection point could not be found " + e.getMessage());
    }
  }
}
