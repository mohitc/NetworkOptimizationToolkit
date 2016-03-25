package com.network.topology;


import com.lpapi.entities.group.LPGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public abstract class LPMLGroupInitializer extends LPGroupInitializer {

  protected Set<String> vertices;

  private static final Logger log = LoggerFactory.getLogger(LPMLGroupInitializer.class);

  public LPMLGroupInitializer(Set<String> vertices) {
    if (vertices==null) {
      log.error("Set of vertices is null, reverting to empty set");
      this.vertices = Collections.EMPTY_SET;
    } else {
      this.vertices = vertices;
    }
  }

}
