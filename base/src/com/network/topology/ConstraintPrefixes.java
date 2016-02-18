package com.network.topology;

public interface ConstraintPrefixes {

  //Constraint for actual capacity
  String ACTUAL_CAPACITY = "Capacity";
  //Constraint to minimize the routing cost
  String MIN_ROUTING_COST = "MinRoutingCostConstr";
  //Constraint to calculate routing cost of path based on routing
  String ROUTING_COST = "RoutingCostConstr";
  //Constraint to bound the number of dynamic circuits that can exist between a node pair
  String DYN_CIRCUIT_BOUND = "DYN-CIR-BOUND";
  //Constraints to ensure symmetric dynamic circuit configurations
  String SYM_DYN_CIRCUIT = "SYM-DYN-CIR";
  //Constraint to ensure that routing variables follow forwarding rules on a node
  String FORWARDING_BASED_ROUTING = "FORWARDING-ROUTING";
  //constraint to ensure that there is a unique forwarding entry to a destination at a node
  String UNIQUE_FORWARDING = "UNIQUE-FW";
  //Constraint to fix the topology of the network by forcing link exists variables to be 1 or 0 in specific cases
  String FIXED_LINK_EXISTS = "LE-Const";
  //Constraint to indicate if link exists based on the existence of an old link or the creation of a dynamic circuit
  String LINK_EXISTS = "LINK-EXISTS";
  //Constraint to calculate the link weight based on the condition that the link exists
  String LINK_WEIGHT = "LINK-WEIGHT";
  //Constraint to ensure routing can only use a link if it exists
  String ROUTING_IFF_LINK_EXISTS = "ROUTING-LINK-EXISTS";
  //Routing continuity constraints
  String ROUTING_CONTINUITY = "ROUTING-CONTINUITY";
  //Symmetric routing constraints
  String SYMMETRIC_ROUTING = "SYMMETRIC-ROUTING";
  //Source loop avoidance
  String LOOP_AVOIDANCE_SOURCE = "LOOP-AVOIDANCE-SOURCE";
  //Destination loop avoidance
  String LOOP_AVOIDANCE_DESTINATION = "LOOP-AVOIDANCE-DEST";

  //Traffic constriants (in case of known TM) to ensure that total traffic on a link is less than the capacity (taking into account
  //the max utilization of the link
  String KNOWN_TM_TRAFFIC = "KNOWN_TM_TRAFFIC";
}
