package com.network.topology.serviceaware.routing.validators;

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

public class ServiceRoutingPathValidator extends ModelValidator {
  
  private static final Logger log = LoggerFactory.getLogger(ServiceRoutingPathValidator.class);

  private Set<String> vertexLabels;

  private LPNameGenerator serviceRoutingNameGenerator;

  private Map<String, List<String>> routeList;

  private int serviceClasses;

  public ServiceRoutingPathValidator(LPModel model, Set<String> vertexLabels, int serviceClasses,
                                     LPNameGenerator serviceRoutingNameGenerator) {
    super(model);
    if (vertexLabels!=null) {
      this.vertexLabels = vertexLabels;
    } else {
      log.error("Routing Path Validator initialized with an empty set");
      this.vertexLabels = Collections.EMPTY_SET;
    }
    if (serviceRoutingNameGenerator == null) {
      log.error("Routing Path Validator initialized with an empty name generator");
      this.serviceRoutingNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.serviceRoutingNameGenerator = serviceRoutingNameGenerator;
    }
    routeList = new HashMap<>();
    if (serviceClasses<=0) {
      log.error("Service classes should be >=1. Defaulting to 1");
      this.serviceClasses = 1;
    } else {
      this.serviceClasses = serviceClasses;
    }
  }

  @Override
  public void validate() throws ModelValidationException {
    for (int serviceClass = 1;serviceClass<=serviceClasses;serviceClass ++)
    for (String s: vertexLabels) {
      for (String d: vertexLabels) {
        if (s.equals(d))
          continue;
        validateRoute(serviceClass, s, d);
      }
    }
  }

  protected String getRoutingVarNameGenerator(int n, String s, String d, String i, String j) throws LPNameException {
    return serviceRoutingNameGenerator.getName(n,s,d,i,j);
  }


  public void validateRoute (int serviceClass, String source, String destination) throws ModelValidationException {
    log.debug("Validating routing path from " + source + " to " + destination + " for service class:" + serviceClass);
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
          String varName = getRoutingVarNameGenerator(serviceClass, source, destination, currentNode, node);
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
        throw new ModelValidationException("More than 1 outgoing route found from " + source + " to " + destination + " at node " + currentNode + " for service class:" + serviceClass);
      } else if (count == 0) {
        throw new ModelValidationException("No outgoing route found from " + source + " to " + destination + " at node " + currentNode + " for service class:" + serviceClass);
      } else if (nextNode!=null) {
        if (visitedVertices.contains(nextNode)) {
          throw new ModelValidationException("Loop condition found for route from " + source + " to " + destination + " at node " + currentNode + " next hop " + nextNode + " for service class:" + serviceClass);
        } else {
          visitedVertices.add(nextNode);
          currentNode = nextNode;
        }
      } else {
        throw new ModelValidationException("Unexpected case. Terminating");
      }
    }
    log.debug("Routing path: " + visitedVertices);
    addRoute(serviceClass, source, destination, visitedVertices);
  }

  protected void addRoute(int serviceClass, String source, String destination, List<String> route) {
    routeList.put(generatePathKey(serviceClass, source, destination), route);
  }

  public String generatePathKey(int serviceClass, String s, String d) {
    return "{" + serviceClass + "}" + s + "-/-" + d;
  }

  public List<String> getRoute(String key) {
    return (key!=null && routeList.containsKey(key))?new ArrayList<>(routeList.get(key)):Collections.EMPTY_LIST;
  }
}
