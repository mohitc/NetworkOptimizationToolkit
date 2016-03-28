package com.network.topology.linkexists.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.NetworkLayer;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class LinkExistsConstantGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkExistsConstantGroupInitializer.class);

  private TopologyManager manager;

  public LinkExistsConstantGroupInitializer(TopologyManager manager) {
    if (manager!=null) {
      this.manager= manager;
    } else {
      log.error("Null topology manager provided for initializing constraints");
    }
  }

  @Override
  public void run() throws LPModelException {
    if (manager==null) {
      throw new LPModelException("Provided Topology manager for initializing link exists constraints is null");
    }
    try {
      Set<ConnectionPoint> cps = manager.getAllElements(ConnectionPoint.class);

      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());

      for (ConnectionPoint cp1: cps) {
        Set<ConnectionPoint> remoteCPs = new HashSet<>();
        Set<Link> connections = cp1.getConnections(NetworkLayer.IP, Link.class);
        for (Link link: connections) {
          if (link.isDirected()) {
            //Link is unidirectional
            if (link.getaEnd().equals(cp1)) {
              remoteCPs.add(link.getzEnd());
            }
          } else {
            //link is bidirectional
            if (link.getaEnd().equals(cp1)) {
              remoteCPs.add(link.getzEnd());
            } else {
              remoteCPs.add(link.getaEnd());
            }
          }
        }
        for (ConnectionPoint cp2: cps) {
          //link does not exist between the same cps
          if (cp1.equals(cp2))
            continue;
          if (remoteCPs.contains(cp2))
            //hat(LE) ij = 1 if link exists in manager
            model().createLpConstant(generator().getName(cp1.getLabel(), cp2.getLabel()), 1, group);
          else
            //hat(LE) ij = 0 otherwise
            model().createLpConstant(generator().getName(cp1.getLabel(), cp2.getLabel()), 0, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
