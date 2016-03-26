package com.network.topology.serviceaware;

public interface SAConstraintGroups {
  String SA_ROUTING_LINK_EXISTS = "SARoutingLEConstr";
  String SA_ROUTING_LINK_EXISTS_DESC = "Constraint to ensure that service aware routing variables only use links that exist";

  String SA_SYMM_ROUTING = "SASymmetricRouting";
  String SA_SYMM_ROUTING_DESC = "Constraint to ensure that each service routing configuration is symmetric";

  String SA_ROUTING_CONTINUITY = "SARoutingContinuity";
  String SA_ROUTING_CONTINUITY_DESC = "Constraint to ensure that each service routing configuration obeys routing continuity";

  String SA_SOURCE_LOOP_AVOIDANCE = "SASourceLoopAvoidance";
  String SA_SOURCE_LOOP_AVOIDANCE_DESC = "Constraint to ensure loop avoidance at source for each service routing";

  String SA_DEST_LOOP_AVOIDANCE = "SADestLoopAvoidance";
  String SA_DEST_LOOP_AVOIDANCE_DESC = "Constraint to ensure loop avoidance at destination for each service routing";

}
