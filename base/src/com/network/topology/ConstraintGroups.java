package com.network.topology;

public interface ConstraintGroups {
  String LINK_EXISTS = "LinkExistsConstr";
  String LINK_EXISTS_DESC = "Constarint to restrict link exists to already existing links or dynamic circuits";

  String DYN_CIRCUIT_BOUND = "DynCircuitBound";
  String DYN_CIRCUIT_BOUND_DESC = "Constraints to bound the number of dynamic circuits";

  String SYMM_DYN_CIRCUITS = "SymDynCirConstr";
  String SYMM_DYN_CIRCUITS_DESC = "Constraints to symmetric dynamic circuits";

  String ACTUAL_CAPACITY = "ActualCapacityConstr";
  String ACTUAL_CAPACITY_DESC = "Constraints to instantiated capacity equal to initial plus dynaimc circuits";

  String KNOWN_TRAFFIC_MAT = "KnownTrafficMatrixCapacityConstr";
  String KNOWN_TRAFFIC_MAT_DESC = "Constrains instantiated capacity to be at least as big as requested";

  String ROUTING_CONTINUITY = "RoutingContinuityConstr";
  String ROUTING_CONTINUITY_DESC = "Constraints on single path routing continuity";

  String SYMMETRIC_ROUTING = "SymmetricRouting";
  String SYMMETRIC_ROUTING_DESC = "Constraint to ensure symmetric routing between each s-d pair";

  String ROUTING_COST = "RoutingCost";
  String ROUTING_COST_DESC = "Constraint to calculate routing cost based on route";

  String MIN_ROUTING_COST = "MinRoutingCost";
  String MIN_ROUTING_COST_DESC = "Constraint to ensure that routing cost is minimized";

  String SOURCE_LOOP_AVOIDANCE = "SourceLoopAvoidance";
  String SOURCE_LOOP_AVOIDANCE_DESC = "Constraint to ensure loop avoidance at source";

  String DEST_LOOP_AVOIDANCE = "DestLoopAvoidance";
  String DEST_LOOP_AVOIDANCE_DESC = "Constraint to ensyre loop avoidance at destination";

  String UNIQUE_FORWARDING = "UniqueForwarding";
  String UNIQUE_FORWARDING_DESC = "Constraints to ensure a single forwarding entry for each destination";

  String FORWARDING_ROUTING = "ForwardingRouting";
  String FORWARDING_ROUTING_DESC = "Routing follows forwarding";

  String LINK_WEIGHT = "LinkWeightConstr";
  String LINK_WEIGHT_DESC = "Constraints to define link weight";

  String FIXED_LINK_EXISTS = "FixedLinkExistsConstr";
  String FIXED_LINK_EXISTS_DESC = "Constarint to restrict link exists to already existing links";

  String ROUTER_IN_PATH = "RouterInPathConstr";
  String ROUTER_IN_PATH_DESC = "Constraint to identify is router is in path";

  String ROUTE_DELAY = "RouteDelay";
  String ROUTE_DELAY_DESC = "Constraint to satisfy routing delay constraints";
}
