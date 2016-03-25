package com.network.topology.linkexists.constraints;

import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPOperator;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.VarGroups;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/** Variable group initializer to constrain Link exists variable to 1 if link exists in graph and 0 otherwise
 *
 */
public class FixedLinkExistsConstrGroupInitializer extends LPGroupInitializer {

  private static final Logger log = LoggerFactory.getLogger(FixedLinkExistsConstrGroupInitializer.class);

  private TopologyManager manager;

  public FixedLinkExistsConstrGroupInitializer(TopologyManager manager) {
    if (manager!=null) {
      this.manager= manager;
    } else {
      log.error("Null topology manager provided for initializing constraints");
    }
  }

  @Override
  public void run() throws LPModelException {
    if (manager==null) {
      throw new LPModelException("Provided Topology manager for initializing link exists constraints is null");
    }
    //Set<Link> links = manager.getAllElements(Link.class);
    try {
      Set<ConnectionPoint> cps = manager.getAllElements(ConnectionPoint.class);
      LPNameGenerator linkExistsNameGenerator = model().getLPVarGroup(VarGroups.LINK_EXISTS).getNameGenerator();
      LPConstraintGroup group = model().getLPConstraintGroup(this.getGroup().getIdentifier());

      for (ConnectionPoint cp1: cps) {
        Set<ConnectionPoint> remoteCPs = new HashSet<>();
        Set<Link> connections = cp1.getConnections(Link.class);
        for (Link link: connections) {
          if (link.isDirected()) {
            //Link is unidirectional
            if (link.getaEnd().equals(cp1)) {
              remoteCPs.add(link.getzEnd());
            }
          } else {
            //link is bidirectional
            if (link.getaEnd().equals(cp1)) {
              remoteCPs.add(link.getzEnd());
            } else {
              remoteCPs.add(link.getaEnd());
            }
          }
        }
        for (ConnectionPoint cp2: cps) {
          //link does not exist between the same cps
          if (cp1.equals(cp2))
            continue;
          LPExpression lhs = new LPExpression(model());
          lhs.addTerm(model().getLPVar(linkExistsNameGenerator.getName(cp1.getLabel(), cp2.getLabel())));
          LPExpression rhs = new LPExpression(model());
          if (remoteCPs.contains(cp2))
            rhs.addTerm(1);
          else
            rhs.addTerm(0);
          model().addConstraint(generator().getName(cp1.getLabel(), cp2.getLabel()), lhs, LPOperator.EQUAL, rhs, group);
        }
      }
    } catch (LPNameException e) {
      log.error("Variable name not found: " + e.getMessage());
      throw new LPModelException("Variable name not found: " + e.getMessage());
    }
  }
}
