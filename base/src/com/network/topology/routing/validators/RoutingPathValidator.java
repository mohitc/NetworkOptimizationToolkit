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

import java.util.*;

public class RoutingPathValidator extends ModelValidator {

  private static final Logger log = LoggerFactory.getLogger(RoutingPathValidator.class);

  private Set<String> vertexLabels;

  private LPNameGenerator routingNameGenerator;

  private Map<String, List<String>> routeList;

  public RoutingPathValidator(LPModel model, Set<String> vertexLabels, LPNameGenerator routingNameGenerator) {
    super(model);
    if (vertexLabels!=null) {
      this.vertexLabels = vertexLabels;
    } else {
      log.error("Routing Path Validator initialized with an empty set");
      this.vertexLabels = Collections.EMPTY_SET;
    }
    if (routingNameGenerator == null) {
      log.error("Routing Path Validator initialized with an empty name generator");
      this.routingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.routingNameGenerator = routingNameGenerator;
    }
    routeList = new HashMap<>();
  }

  @Override
  public void validate() throws ModelValidationException {
    for (String s: vertexLabels) {
      for (String d: vertexLabels) {
        if (s.equals(d))
          continue;
        validateRoute(s, d);
      }
    }
  }

  protected String getRoutingVarNameGenerator(String s, String d, String i, String j) throws LPNameException {
    return routingNameGenerator.getName(s,d,i,j);
  }


  public void validateRoute (String source, String destination) throws ModelValidationException {
    log.info("Validating routing path from " + source + " to " + destination);
    String currentNode = source;
    List<String> visitedVertices = new ArrayList<>();
    visitedVertices.add(currentNode);

    while(!currentNode.equals(destination)) {
      int count = 0;
      String nextNode = null;
      for (String node : vertexLabels) {
        if (node.equals(currentNode))
          continue;
        try {
          String varName = getRoutingVarNameGenerator(source, destination, currentNode, node);
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
    log.debug("Routing path: " + visitedVertices);
    addRoute(source, destination, visitedVertices);
  }

  protected void addRoute(String source, String destination, List<String> route) {
    routeList.put(generatePathKey(source, destination), route);
  }

  public String generatePathKey(String s, String d) {
    return s+ "-/-" + d;
  }

  public List<String> getRoute(String key) {
    return (key!=null && routeList.containsKey(key))?new ArrayList<>(routeList.get(key)):Collections.EMPTY_LIST;
  }
}
