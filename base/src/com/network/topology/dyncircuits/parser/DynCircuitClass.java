package com.network.topology.dyncircuits.parser;

public class DynCircuitClass {

  private final int classType;

  private final double capacity;

  private final double cost;

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
