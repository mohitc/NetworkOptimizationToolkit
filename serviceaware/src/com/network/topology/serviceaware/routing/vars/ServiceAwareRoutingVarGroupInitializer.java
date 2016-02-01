package com.network.topology.serviceaware.routing.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class ServiceAwareRoutingVarGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRoutingVarGroupInitializer.class);

  private Set<String> vertices;

  public ServiceAwareRoutingVarGroupInitializer(Set<String> vertices) {
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
      int serviceClasses = (int) model().getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
      for (int t = 1; t<=serviceClasses; t++) {
        for (String s : vertices) {
          for (String d : vertices) {
            //for all distinct pair of vertices, a linkexists can possibly have a value 0 or 1
            if (s.equals(d))
              continue;
            for (String i : vertices) {
              for (String j : vertices) {
                if (i.equals(j) || d.equals(i))
                  continue;
                //all incoming links to the source cannot carry traffic going out from the source
                if (j.equals(s))
                  this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(t, s, d, i, j), LPVarType.BOOLEAN, 0, 0, group);
                else
                  this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(t, s, d, i, j), LPVarType.BOOLEAN, 0, 1, group);
              }
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
