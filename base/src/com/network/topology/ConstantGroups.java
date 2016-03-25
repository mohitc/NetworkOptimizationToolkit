package com.network.topology;

public interface ConstantGroups {

  String VARIABLE_BOUNDS="VariableBounds";
  String VARIABLE_BOUNDS_DESC="Constants defining maximum bounds on variables";

  String LINK_EXISTS = "Hat(LinkExists)";
  String LINK_EXISTS_DESC = "Constants to indicate if link existed in original topology";

  String INITIAL_CAP = "HAT(C)";
  String INITIAL_CAP_DESC = "Constants to store the initial capacity between each node pair";

  String LINK_WEIGHT = "Hat(W)";
  String LINK_WEIGHT_DESC = "Constants to indicate weight of link if exists";

  String TRAFFIC_MAT = "lambda";
  String TRAFFIC_MAT_DESC = "Constants to indicate requested capacity between two nodes";

  String ROUTING_LINK_EXISTS = "RoutingIfLinkExistsConstr";
  String ROUTING_LINK_EXISTS_DESC = "Constraints on routing using existing links";

  String LINK_DELAY = "D(L)";
  String LINK_DELAY_DESC = "Constants to store the link delays base on shortest delay routing in optical";

  String ROUTER_DELAY = "D(R)";
  String ROUTER_DELAY_DESC = "Constants to store delay of routers";

  String PATH_DELAY = "D(Path)";
  String PATH_DELAY_DESC = "Constants to store max delay on path";
}
