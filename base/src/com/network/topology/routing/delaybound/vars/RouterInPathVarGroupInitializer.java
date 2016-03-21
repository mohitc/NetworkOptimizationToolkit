package com.network.topology.routing.delaybound.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RouterInPathVarGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouterInPathVarGroupInitializer.class);

  private Set<String> vertices;

  public RouterInPathVarGroupInitializer(Set<String> vertices) {
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
          if (s.equals(d))
            continue;
          //for all distinct pair of source/destination pairs, a router being in the routing path can possibly have a value 0 or 1
          for (String i : vertices) {
            this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(s, d, i), LPVarType.BOOLEAN, 0, 1, group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Routing in service path  group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
