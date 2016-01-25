package com.network.topology.dyncircuits.parser;

public class DynCircuitClass {

  private int classType;

  private double capacity;

  private double cost;

  public DynCircuitClass(int classType, double capacity, double cost) {
    this.classType = classType;
    this.capacity = capacity;
    this.cost = cost;
  }

  public int getClassType() {
    return classType;
  }

  public double getCapacity() {
    return capacity;
  }

  public double getCost() {
    return cost;
  }

  public String toString() {
    return "Class Type: " + classType + ", Capacity: " + capacity + ", Cost: " + cost;
  }
}
