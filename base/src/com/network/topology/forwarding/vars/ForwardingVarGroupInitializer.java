package com.network.topology.forwarding.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ForwardingVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ForwardingVarGroupInitializer.class);

  public ForwardingVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (String d : vertices) {
        for (String i: vertices) {
          if (d.equals(i))
            continue;
          for (String j: vertices) {
            if (i.equals(j))
              continue;
            this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(d, i, j), LPVarType.BOOLEAN, 0, 1, group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Routing variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
