package com.network.topology.serviceaware.models.traffic.parsing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.Collections;
import java.util.List;

public class ServiceTrafficMatrixEntry {

  @JacksonXmlProperty(isAttribute = true)
  private String source;

  @JacksonXmlProperty(isAttribute = true)
  private String destination;

  private List<ServiceTrafficClassEntry> serviceClassSplit;

  public String getSource() {
    return (source!=null)?source:"";
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getDestination() {
    return destination!=null?destination:"";
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public List<ServiceTrafficClassEntry> getServiceClassSplit() {
    return serviceClassSplit!=null?serviceClassSplit: Collections.EMPTY_LIST;
  }

  public void setServiceClassSplit(List<ServiceTrafficClassEntry> serviceClassSplit) {
    this.serviceClassSplit = serviceClassSplit;
  }

  public String toString() {
    return "{Source: " + getSource() + ", Destination: " + getDestination() + ", Split = " + getServiceClassSplit();
  }
}
