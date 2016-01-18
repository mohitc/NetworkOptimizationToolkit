/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.linkweight.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class LinkWeightConstantGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkWeightConstantGroupInitializer.class);

  private Set<String> vertices;

  public LinkWeightConstantGroupInitializer(Set<String> vertices) {
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
      LPConstantGroup group = this.getGroup().getModel().getLPConstantGroup(this.getGroup().getIdentifier());

      for (String vertex1 : vertices) {
        for (String vertex2 : vertices) {
          //for all distinct pair of vertices, a capacity can possibly have a value between 0 and C_MAX
          if (vertex1.equals(vertex2))
            continue;
          this.getGroup().getModel().createLpConstant(group.getNameGenerator().getName(vertex1, vertex2), 1, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Link Exists variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
