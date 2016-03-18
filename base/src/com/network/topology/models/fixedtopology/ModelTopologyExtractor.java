package com.network.topology.models.fixedtopology;

import com.lpapi.entities.LPModel;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.NetworkElement;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class ModelTopologyExtractor implements ModelExtractor<TopologyManager> {

  private static final Logger log = LoggerFactory.getLogger(ModelTopologyExtractor.class);

  private Set<String> vertexLabels;

  LPNameGenerator linkExistsNameGenerator;

  public ModelTopologyExtractor(Set<String> vertexLabels, LPNameGenerator linkExistsNameGenerator) {
    if (vertexLabels==null) {
      this.vertexLabels = Collections.EMPTY_SET;
    } else {
      this.vertexLabels = vertexLabels;
    }
    if (linkExistsNameGenerator==null) {
      this.linkExistsNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.linkExistsNameGenerator = linkExistsNameGenerator;
    }
  }

  @Override
  public TopologyManager extractModel(LPModel model) throws ModelExtractionException {
    try {
      log.info("Starting extraction of topology to a TopologyManager instance");
      TopologyManager manager = new TopologyManagerImpl("result");

      //Create nodes based on vertex labels
      for (String vertex: vertexLabels) {
        NetworkElement ne = manager.createNetworkElement();
        ne.setLabel(vertex);
        //create connection point inside the NE
        ConnectionPoint cp = manager.createConnectionPoint(ne);
        cp.setLabel(vertex);
      }

      //Create Links
      for (String i: vertexLabels) {
        for (String j: vertexLabels) {
          if (i.compareTo(j)<=0)
            continue;
          if (model.getLPVar(linkExistsNameGenerator.getName(i,j)).getResult().intValue()==1) {
            log.info("Creating link between " + i + " and " + j);
            Link link = manager.createLink(manager.getSingleElementByLabel(i, ConnectionPoint.class).getID(), manager.getSingleElementByLabel(j, ConnectionPoint.class).getID());
          }
        }
      }

      return manager;
    } catch (Exception e) {
      log.error("Error while generating topology", e);
      throw new ModelExtractionException("Error while generating topology", e);
    }
  }
}
