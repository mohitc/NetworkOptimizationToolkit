package com.network.topology.linkexists.vars;

import com.lpapi.entities.LPVarGroup;
import com.lpapi.entities.LPVarType;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.LPMLGroupInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class LinkExistsVarGroupInitializer extends LPMLGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(LinkExistsVarGroupInitializer.class);

  public LinkExistsVarGroupInitializer(Set<String> vertices) {
    super(vertices);
  }

  @Override
  public void run() throws LPModelException {
    try {
      LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
      for (String vertex1 : vertices) {
        for (String vertex2 : vertices) {
          //for all distinct pair of vertices, a linkexists can possibly have a value 0 or 1
          if (vertex1.equals(vertex2))
            continue;
          this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(vertex1, vertex2), LPVarType.BOOLEAN, 0, 1, group);
        }
      }
    }catch (LPNameException e) {
      log.error("Error while initializing Link Exists variable group variables", e);
      throw new LPModelException(e.getMessage());
    }
  }
}
