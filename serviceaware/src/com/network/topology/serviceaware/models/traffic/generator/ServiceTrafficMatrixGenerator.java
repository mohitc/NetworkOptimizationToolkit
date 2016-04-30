package com.network.topology.serviceaware.models.traffic.generator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficClassEntry;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficMatrixSplit;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficMatrixEntry;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.importers.ImportTopology;
import com.topology.primitives.NetworkElement;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceTrafficMatrixGenerator {

  private static final Logger log = LoggerFactory.getLogger(ServiceTrafficMatrixGenerator.class);

  public ServiceTrafficMatrixSplit parseXml(String inFile) {
    XmlMapper mapper = new XmlMapper();
    File file = new File(inFile);
    try {
      return mapper.readValue(file, ServiceTrafficMatrixSplit.class);
    } catch (IOException e) {
      log.error("Error while parsing input file", e);
    }
    return null;
  }

  public Map<String, Double> generateServiceTrafficMatrix(Map<String, Double> trafficMatrix, ServiceTrafficMatrixSplit split) {
    Map<String, Double> serviceTrafficMatrix = new HashMap<>();
    for (ServiceTrafficMatrixEntry entry: split.getTrafficMatrixEntry()) {
      String tmKey = "{" + entry.getSource()  + "}{" + entry.getDestination()+ "}";
      if (trafficMatrix.containsKey(tmKey)) {
        final double val = trafficMatrix.get(tmKey);
        entry.getServiceClassSplit().forEach(v ->
        serviceTrafficMatrix.put("[" + v.getServiceClass() + "]" + tmKey, val * v.getFraction()));
      }
    }
    return serviceTrafficMatrix;
  }


  public void generateReferenceMatrix (List<String> vertices, Map<Integer, Double> split, String outFile) {

    List<ServiceTrafficMatrixEntry> recordList = new ArrayList<>();
    for (String s: vertices) {
      for (String d: vertices) {
        if (s.equals(d))
          continue;
        ServiceTrafficMatrixEntry entry = new ServiceTrafficMatrixEntry();
        entry.setSource(s);
        entry.setDestination(d);
        entry.setServiceClassSplit(
        split.entrySet().stream().map(v -> new ServiceTrafficClassEntry(v.getKey(), v.getValue())).collect(Collectors.toList()));
        recordList.add(entry);
      }
    }
    ServiceTrafficMatrixSplit matrix = new ServiceTrafficMatrixSplit();
    matrix.setTrafficMatrixEntry(recordList);
    XmlMapper mapper = new XmlMapper();
    File resultFile = new File(outFile);
    try {
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      mapper.writeValue(resultFile, matrix);
    } catch (IOException e) {
      log.error("Error while writing to file", e);
    }
  }

  public static void main (String[] args) {
    ImportTopology importTopology = new SNDLibImportTopology();
    TopologyManager manager = new TopologyManagerImpl("test");
    try {
      importTopology.importFromFile("../base/conf/nobel-us.xml", manager);
      List<String> vertices = manager.getAllElements(NetworkElement.class).stream().
          map(v -> v.getLabel()).collect(Collectors.toList());
      Map<Integer, Double> serviceSplit = new HashMap<>();
      serviceSplit.put(1, 0.8);
      serviceSplit.put(2, 0.2);
      ServiceTrafficMatrixGenerator generator = new ServiceTrafficMatrixGenerator();
      generator.generateReferenceMatrix(vertices, serviceSplit, "out.xml");
      log.info("Parsed Matrix" + generator.parseXml("out.xml"));
    } catch (Exception e) {
      log.error("Error while importing topology: ", e);
    }
  }
}
