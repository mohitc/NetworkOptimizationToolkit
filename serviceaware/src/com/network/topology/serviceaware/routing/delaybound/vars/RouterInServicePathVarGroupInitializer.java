package com.network.topology.serviceaware.routing.delaybound.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RouterInServicePathVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(RouterInServicePathVarGroupInitializer.class);

  public RouterInServicePathVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int t=1;t<=serviceClasses; t++) {
        for (String s : vertices) {
          for (String d : vertices) {
            if (s.equals(d))
              continue;
            //for all distinct pair of source/destination pairs, a router being in the service routing path can possibly have a value 0 or 1
            for (String i : vertices) {
              this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(t, s, d, i), LPVarType.BOOLEAN, 0, 1, group);
            }
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Routing in service path  group variables", e);
      throw new LPModelException(e.getMessage());
    }

  }
}
