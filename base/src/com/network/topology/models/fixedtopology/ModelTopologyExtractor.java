package com.network.topology.models.fixedtopology;

import com.lpapi.entities.LPModel;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.*;
import com.topology.primitives.connresource.BandwidthConnectionResource;
import com.topology.primitives.exception.TopologyException;
import com.topology.primitives.properties.TEPropertyKey;
import com.topology.primitives.properties.converters.impl.DoubleConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelTopologyExtractor implements ModelExtractor<TopologyManager> {

  private static final Logger log = LoggerFactory.getLogger(ModelTopologyExtractor.class);

  private Set<String> vertexLabels;

  LPNameGenerator linkExistsNameGenerator;

  LPNameGenerator capacityVarNameGenerator;

  TopologyManager physicalTopology;

  public ModelTopologyExtractor(Set<String> vertexLabels, LPNameGenerator linkExistsNameGenerator, LPNameGenerator capacityVarNameGenerator, TopologyManager physicalTopology) {
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
    if (capacityVarNameGenerator==null) {
      this.capacityVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.capacityVarNameGenerator = capacityVarNameGenerator;
    }
    this.physicalTopology = physicalTopology;
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

      if (physicalTopology!=null) {
        //Copy properties from original topology onto IP topology
        TEPropertyKey XCOORD = manager.registerKey("X", "X coordinate", Double.class, DoubleConverter.class);
        TEPropertyKey YCOORD = manager.registerKey("Y", "Y coordinate", Double.class, DoubleConverter.class);

        for (String vertex: vertexLabels) {
          try {
            NetworkElement oldNe = physicalTopology.getSingleElementByLabel(vertex, NetworkElement.class);
            NetworkElement newNe = manager.getSingleElementByLabel(vertex, NetworkElement.class);
            if (oldNe.hasProperty(XCOORD)) {
              newNe.addProperty(XCOORD, oldNe.getProperty(XCOORD));
            }
            if (oldNe.hasProperty(YCOORD)) {
              newNe.addProperty(YCOORD, oldNe.getProperty(YCOORD));
            }
          } catch (TopologyException e) {
            log.error("Error while trying to copy parameters from the physical topology", e);
          }
        }

        //Copy links
        Set<Link> phyLinks = physicalTopology.getAllElements(Link.class).stream().filter(v -> v.getLayer() == NetworkLayer.PHYSICAL).collect(Collectors.toSet());
        TEPropertyKey DELAY = manager.registerKey("Delay", "Delay (ms)", Double.class, DoubleConverter.class);
        for (Link link: phyLinks) {
          //create physical links in the new topology
          try {
            ConnectionPoint aEndCp = manager.getSingleElementByLabel(link.getaEnd().getLabel(), ConnectionPoint.class);
            ConnectionPoint zEndCp = manager.getSingleElementByLabel(link.getzEnd().getLabel(), ConnectionPoint.class);

            Link newLink = manager.createLink(aEndCp.getID(), zEndCp.getID());
            newLink.setLayer(NetworkLayer.PHYSICAL);
            if (link.hasProperty(DELAY)) {
              newLink.addProperty(DELAY, link.getProperty(DELAY));
            }
          } catch (TopologyException e) {
            log.error("Error while creating physical link", e);
          }
        }

      } else {
        log.info("Empty physical topology provided. Properties of physical topology will not be copied");
      }


      //Create Links
      for (String i: vertexLabels) {
        for (String j: vertexLabels) {
          if (i.compareTo(j)<=0)
            continue;
          if (model.getLPVar(linkExistsNameGenerator.getName(i,j)).getResult().intValue()==1) {
            log.info("Creating link between " + i + " and " + j);
            Link link = manager.createLink(manager.getSingleElementByLabel(i, ConnectionPoint.class).getID(), manager.getSingleElementByLabel(j, ConnectionPoint.class).getID());
            link.setLayer(NetworkLayer.IP);
            link.setTotalResources(new BandwidthConnectionResource(model.getLPVar(capacityVarNameGenerator.getName(i, j)).getResult().doubleValue()));
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
