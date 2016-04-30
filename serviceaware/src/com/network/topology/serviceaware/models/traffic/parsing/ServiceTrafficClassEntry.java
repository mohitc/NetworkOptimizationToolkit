package com.network.topology.serviceaware.models.traffic.parsing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ServiceTrafficClassEntry {

  @JacksonXmlProperty(isAttribute = true)
  private int serviceClass;

  @JacksonXmlProperty(isAttribute = true)
  private double fraction;

  public ServiceTrafficClassEntry() {}

  public ServiceTrafficClassEntry(int serviceClass, double fraction) {
    this.serviceClass = serviceClass;
    this.fraction = fraction;
  }

  public int getServiceClass() {
    return serviceClass;
  }

  public void setServiceClass(int serviceClass) {
    this.serviceClass = serviceClass;
  }

  public double getFraction() {
    return fraction;
  }

  public void setFraction(double fraction) {
    this.fraction = fraction;
  }

  public String toString() {
    return "Class: " + serviceClass + ", fraction: " + fraction;
  }
}
