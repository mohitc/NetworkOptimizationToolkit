package com.network.topology.routing.delaybound.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RoutePathDelayConstGroupInitializer extends LPMLGroupInitializer {
  private static final Logger log = LoggerFactory.getLogger(RoutePathDelayConstGroupInitializer.class);

  public RoutePathDelayConstGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
      double delay = 4000000;
      for (String s : vertices) {
        for (String d : vertices) {
          if (s.equals(d))
            continue;
          model().createLpConstant(generator().getName(s, d), delay, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
