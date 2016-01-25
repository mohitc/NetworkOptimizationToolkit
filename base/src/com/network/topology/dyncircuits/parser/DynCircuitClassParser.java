package com.network.topology.dyncircuits.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DynCircuitClassParser {

  private String fileName;

  private static final Logger log = LoggerFactory.getLogger(DynCircuitClassParser.class);

  public DynCircuitClassParser(String fileName) {
    this.fileName = fileName;
  }

  private Map<Integer, DynCircuitClass> result = null;

  public Map<Integer, DynCircuitClass> getResult() {
    if (result==null) {
      result = parse();
    }
    return result;
  }

  private Map<Integer, DynCircuitClass> parse() {
    //List<DynCircuitClass> outList = new ArrayList<>();
    if (fileName==null) {
      log.error("Null file name not allowed. Defaulting to empty capacity list");
      return Collections.EMPTY_MAP;
    }
    log.info("Starting scan of circuit capacities from the file " + (fileName));
    File capFile = new File(fileName);
    if (!(capFile.exists() && capFile.isFile())) {
      log.error("Invalid path provided to file for dynamic circuit capacities. Defaulting to empty list.");
      return Collections.EMPTY_MAP;
    }
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document doc = null;
    try {
      builder = factory.newDocumentBuilder();
      doc = builder.parse(capFile);
      if (doc==null) {
        log.error("No document found. Please check XML file. Defaulting to empty list");
        return Collections.EMPTY_MAP;
      }
      //Normalize document
      doc.normalizeDocument();
      return parseDynCircuitClasses(doc);
    } catch (Exception e) {
      log.error("Error while setting up XML parser", e);
    }
    //In case of exception, return empty List
    return Collections.EMPTY_MAP;
  }

  private Map<Integer, DynCircuitClass> parseDynCircuitClasses(Document doc) {
    NodeList list = doc.getElementsByTagName("circuitclasses");
    if (list.getLength()!=1) {
      log.error("The document should only have one tag with the list of all circuit classes. Defaulting to empty list");
      return Collections.EMPTY_MAP;
    }

    Map<Integer, DynCircuitClass> outList = new HashMap<>();
    //Parse circuits
    NodeList nodeList = list.item(0).getChildNodes();
    for (int i=0;i<nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element parent = (Element) node;
        //Populate coordinates
        try {
          int classType = extractIntVal(parent, "classtype");
          double capacity = extractDoubleVal(parent, "cap");
          double cost  = extractDoubleVal(parent, "cost");
          //no exception, create object
          DynCircuitClass circuitClass = new DynCircuitClass(classType, capacity, cost);
          if (outList.containsKey(classType)) {
            log.error("Circuit classtype clash. Object " + circuitClass + " ignored");
          } else {
            outList.put(classType, circuitClass);
            log.info("Circuit Class Parsed: " + circuitClass);
          }
        } catch (IOException e) {
          log.error("Error while parsing element. Skipping class creation: " + e.getMessage());
        }
      }
    }
    return outList;
  }

  private double extractDoubleVal (Element parentNode, String childTagName) throws IOException {
    NodeList entityList = parentNode.getElementsByTagName(childTagName);
    if ((entityList!=null) && (entityList.getLength()>0)) {
      try {
        return Double.parseDouble(entityList.item(0).getTextContent());
      } catch (NumberFormatException e) {
        throw new IOException("Error while parsing value : " + e.getMessage());
      }
    }
    throw new IOException("Invalid format for XML config file. Tag " + childTagName + " not found.");
  }

  private int extractIntVal (Element parentNode, String childTagName) throws IOException {
    NodeList entityList = parentNode.getElementsByTagName(childTagName);
    if ((entityList!=null) && (entityList.getLength()>0)) {
      try {
        return Integer.parseInt(entityList.item(0).getTextContent());
      } catch (NumberFormatException e) {
        throw new IOException("Error while parsing value : " + e.getMessage());
      }
    }
    throw new IOException("Invalid format for XML config file. Tag " + childTagName + " not found.");
  }

}
