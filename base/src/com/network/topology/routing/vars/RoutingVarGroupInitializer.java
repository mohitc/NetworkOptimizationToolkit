package com.network.topology.routing.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RoutingVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RoutingVarGroupInitializer.class);

  public RoutingVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (String s : vertices) {
        for (String d : vertices) {
          if (s.equals(d))
            continue;
          for (String i: vertices) {
            for (String j: vertices) {
              if (i.equals(j))
                continue;
              //All routing variables can have the value between 0 and 1
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
