package com.network.topology.serviceaware.models.traffic.generator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficClassEntry;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficMatrix;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficMatrixEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceTrafficMatrixGenerator {

  private static final Logger log = LoggerFactory.getLogger(ServiceTrafficMatrixGenerator.class);

  private ServiceTrafficMatrix parseXml(String inFile) {
    XmlMapper mapper = new XmlMapper();
    File file = new File(inFile);
    try {
      return mapper.readValue(file, ServiceTrafficMatrix.class);
    } catch (IOException e) {
      log.error("Error while parsing input file", e);
    }
    return null;
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
    ServiceTrafficMatrix matrix = new ServiceTrafficMatrix();
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
    Map<Integer, Double> serviceSplit = new HashMap<>();
    serviceSplit.put(1, 0.8);
    serviceSplit.put(2, 0.2);
    List<String> vertices = new ArrayList<>();
    vertices.add("A");
    vertices.add("B");
    vertices.add("C");
    ServiceTrafficMatrixGenerator generator = new ServiceTrafficMatrixGenerator();
    generator.generateReferenceMatrix(vertices, serviceSplit, "out.xml");
    log.info("Parsed Matrix" + generator.parseXml("out.xml"));
  }
}
