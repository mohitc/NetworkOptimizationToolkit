/*
 *  Copyright 2013 ADVA Optical Networking SE. All rights reserved.
 *
 *  Owner: mchamania
 *
 *  $Id: $
 */
package com.network.topology.routing.validators;

import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPVar;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPNameException;
import com.lpapi.exception.LPVarException;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoutingPathValidator extends ModelValidator {

  private static final Logger log = LoggerFactory.getLogger(RoutingPathValidator.class);

  private String source, destination;

  private Set<String> vertexLabels;

  private LPNameGenerator routingNameGenerator;

  public RoutingPathValidator(LPModel model, String source, String destination, Set<String> vertexLabels, LPNameGenerator routingNameGenerator) {
    super(model);
    this.source = source;
    this.destination = destination;
    this.vertexLabels = vertexLabels;
    if (routingNameGenerator == null) {
      this.routingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingNameGenerator = routingNameGenerator;
    }
  }

  @Override
  public void validate() throws ModelValidationException {
    log.info("Validating routing path from " + source + " to " + destination);
    String currentNode = source;
    List<String> visitedVertices = new ArrayList<>();
    visitedVertices.add(currentNode);

    while(currentNode.equals(destination) == false) {
      int count = 0;
      String nextNode = null;
      for (String node : vertexLabels) {
        if (node.equals(currentNode))
          continue;
        try {
          String varName = routingNameGenerator.getName(source, destination, currentNode, node);
          LPVar var = getModel().getLPVar(varName);
          if (var.getResult().intValue()==1) {
            count++;
            nextNode = node;
          }
        } catch (LPNameException e) {
          throw new ModelValidationException("Routing variable names not generated correctly", e);
        } catch (LPVarException e) {
          throw new ModelValidationException("Routing variable not found in the model", e);
        }

      }
      if (count>1) {
        throw new ModelValidationException("More than 1 outgoing route found from " + source + " to " + destination + " at node " + currentNode);
      } else if (count == 0) {
        throw new ModelValidationException("No outgoing route found from " + source + " to " + destination + " at node " + currentNode);
      } else if (nextNode!=null) {
        if (visitedVertices.contains(nextNode)) {
          throw new ModelValidationException("Loop condition found for route from " + source + " to " + destination + " at node " + currentNode + " next hop " + nextNode);
        } else {
          visitedVertices.add(nextNode);
          currentNode = nextNode;
        }
      } else {
        throw new ModelValidationException("Unexpected case. Terminating");
      }
    }
    log.info("Routing path: " + visitedVertices);
  }
}
