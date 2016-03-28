package com.network.topology.serviceaware;

public interface SAConstantGroups {
  String SERVICE_ROUTER_DELAY = "D(S, R)";
  String SERVICE_ROUTER_DELAY_DESC = "Constants to store delay of routers based on type of service";

  String SERVICE_PATH_DELAY = "D(S, Path)";
  String SERVICE_PATH_DELAY_DESC = "Constants to store max delay on path based on the type of service";

  String SA_TRAFFIC_MAT = "lambda(service)";
  String SA_TRAFFIC_MAT_DESC = "Constants to indicate requested capacity between two nodes for a given service";

}
