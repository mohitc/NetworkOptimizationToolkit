package com.network.topology;

public interface VarGroups {
  String LINK_EXISTS = "LinkExists";
  String LINK_EXISTS_DESC = "Variables to indicate if link exists";

  String CAPACITY = "LinkCapacity";
  String CAPACITY_DESC = "Variables to indicate link capacity";

  String DYN_CIRCUITS = "DynCircuits";
  String DYN_CIRCUITS_DESC = "Dynamic circuits variables";

  String ROUTING = "Routing";
  String ROUTING_DESC = "Variables to indicate route";

  String FORWARDING = "Forwarding";
  String FORWARDING_DESC = "Variables to constrain forwarding";

  String ROUTING_COST = "RoutingCost";
  String ROUTING_COST_DESC = "Routing Cost variables";

  String LINK_WEIGHT = "LinkWeight";
  String LINK_WEIGHT_DESC = "Variables to indicate link weights";

  String ROUTER_IN_PATH = "RouterInPath";
  String ROUTER_IN_PATH_DESC = "Variable to indicate if router is in path";
}
