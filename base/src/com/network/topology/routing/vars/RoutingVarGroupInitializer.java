package com.network.topology.routing.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RoutingVarGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingVarGroupInitializer.class);

  private Set<String> vertices;

  public RoutingVarGroupInitializer(Set<String> vertices) {
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
          //for all distinct pair of vertices, a linkexists can possibly have a value 0 or 1
          if (s.equals(d))
            continue;
          for (String i: vertices) {
            for (String j: vertices) {
              if (i.equals(j))
                continue;
              //all incoming links to the source cannot carry traffic going out from the source
              if (j.equals(s))
                this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(s, d, i, j), LPVarType.BOOLEAN, 0, 0, group);
              else
                this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(s, d, i, j), LPVarType.BOOLEAN, 0, 1, group);
            }
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Routing variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
