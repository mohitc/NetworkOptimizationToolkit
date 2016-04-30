package com.network.topology.serviceaware.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServiceAwareRoutePathDelayConstGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRouterDelayConstGroupInitializer.class);

  public ServiceAwareRoutePathDelayConstGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int t=1;t<=serviceClasses; t++) {
        for (String i : vertices) {
          for (String j: vertices) {
            if (i.equals(j))
              continue;
            //Todo base values based on service classes
            double delay = 100 - t*20;
            delay = (delay>40) ? delay : 40;
            model().createLpConstant(generator().getName(t, i, j), delay, group);
          }
        }
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }

  }
}
