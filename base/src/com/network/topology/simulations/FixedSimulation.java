package com.network.topology.simulations;

import com.lpapi.exception.LPModelException;
import com.network.topology.models.delayconstrainedmlspf.DelayConstrainedMLSpfTopologyModel;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.fixedtopology.FixedTopologyModel;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import com.topology.primitives.properties.TEPropertyKey;
import com.topology.primitives.properties.converters.impl.MapConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FixedSimulation {

  private static final Logger log = LoggerFactory.getLogger(FixedSimulation.class);

  public static void main(String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      FixedTopologyModel lpModel = new FixedTopologyModel("conf/circuit-cap.xml", manager);
      lpModel.init();
      lpModel.compute();
      lpModel.postCompute();

      TopologyManager newTopology = lpModel.getExtractedModel();

      TEPropertyKey demands = newTopology.registerKey("Demands", "Demands for the topology", Map.class, MapConverter.class);


      Map<String, Double> oldDemands = manager.getProperty(demands, Map.class);
      Map<String, Double> newDemands = new HashMap<>();
      for (Map.Entry<String, Double> entry: oldDemands.entrySet()) {
        newDemands.put(entry.getKey(), entry.getValue() * 10);
      }
      newTopology.addProperty(demands, newDemands);

/*
      MultiLayerSpfTopologyModel newLpModel = new MultiLayerSpfTopologyModel("conf/circuit-cap.xml", newTopology);
      newLpModel .init();
      newLpModel .compute();
      newLpModel .postCompute();


      TopologyManager finalTopology = newLpModel.getExtractedModel();
*/

      DelayConstrainedMLSpfTopologyModel newLpModel = new DelayConstrainedMLSpfTopologyModel("conf/circuit-cap.xml", newTopology);
      newLpModel .init();
      newLpModel .compute();
      newLpModel .postCompute();


      TopologyManager finalTopology = newLpModel.getExtractedModel();


      log.info("Done");

    } catch (LPModelException e) {
      log.error("Error initializing model", e);
    } catch (TopologyException e) {
      log.error("Error initializing topology", e);
    } catch (FileFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      log.error("Error initializing model file", e);
    } catch (ModelExtractionException e) {
      log.error("Error while extracting topology from solved model");
    }
  }
}
