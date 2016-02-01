package com.network.topology.routing.routingcost.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.FixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RoutingCostVarGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingCostVarGroupInitializer.class);

  private Set<String> vertices;

  public RoutingCostVarGroupInitializer(Set<String> vertices) {
    if (vertices==null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }

  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          //for all distinct pair of vertices, routing cost can have a positive value between 0 and RC(MAX)
          if (s.equals(d))
            continue;
          //all incoming links to the source cannot carry traffic going out from the source
          this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(s, d), LPVarType.DOUBLE, 0, model().getLPConstant(FixedConstants.ROUTING_COST_MAX).getValue(), group);
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Routing variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
