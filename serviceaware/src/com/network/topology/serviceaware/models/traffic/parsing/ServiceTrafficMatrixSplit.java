package com.network.topology.serviceaware.models.traffic.parsing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement
public class ServiceTrafficMatrixSplit {

  private List<ServiceTrafficMatrixEntry> trafficMatrixEntry;

  public List<ServiceTrafficMatrixEntry> getTrafficMatrixEntry() {
    return trafficMatrixEntry;
  }

  public void setTrafficMatrixEntry(List<ServiceTrafficMatrixEntry> trafficMatrixEntry) {
    this.trafficMatrixEntry = trafficMatrixEntry;
  }

  public String toString() {
    return trafficMatrixEntry.toString();
  }
}
