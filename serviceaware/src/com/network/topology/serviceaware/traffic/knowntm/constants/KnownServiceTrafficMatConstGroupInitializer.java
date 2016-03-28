package com.network.topology.serviceaware.traffic.knowntm.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.TEPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class KnownServiceTrafficMatConstGroupInitializer extends LPMLGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(KnownServiceTrafficMatConstGroupInitializer.class);

  private TopologyManager topo;

  public KnownServiceTrafficMatConstGroupInitializer(Set<String> vertices, TopologyManager topo) {
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
      TEPropertyKey demandStoreKey;
      Map<String, Double> demandStore;
      try {
        demandStoreKey = topo.getKey("Demands");
        demandStore = (Map<String, Double>) topo.getProperty(demandStoreKey);
      } catch (PropertyException e) {
        throw new LPModelException("Demand store not found: " + e.getMessage());
      }
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int n=1;n<=serviceClasses;n++) {
        int count =0;
        for (String i : vertices) {
          for (String j : vertices) {
            if (i.equals(j))
              continue;
            String label = "[" + n + "]{" + i + "}{" + j + "}";
            if (demandStore.containsKey(label)) {
              model().createLpConstant(generator().getName(i, j), demandStore.get(label), group);
              count++;
            } else {
              log.info("No demand found for nodes (" + i + " -> " + j + "), defaulting to 0");
              model().createLpConstant(generator().getName(i, j), 0, group);
            }
          }
        }
        log.info("For service class {}, {} out of {} vars were populated from the topology, others were defaulted to 0", n, count, (vertices.size() * (vertices.size()-1)));
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
