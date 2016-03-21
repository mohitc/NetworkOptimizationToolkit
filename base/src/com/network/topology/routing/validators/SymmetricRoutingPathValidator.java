package com.network.topology.routing.validators;

import com.lpapi.entities.LPModel;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SymmetricRoutingPathValidator extends ModelValidator {

  private static final Logger log = LoggerFactory.getLogger(SymmetricRoutingPathValidator.class);

  private Set<String> vertexLabels;

  private RoutingPathValidator routingPathValidator;

  public SymmetricRoutingPathValidator(LPModel model, Set<String> vertexLabels, RoutingPathValidator routingPathValidator) {
    super(model);
    if (vertexLabels != null)
      this.vertexLabels = vertexLabels;
    else {
      log.error("Symmetric routing path validator initialized with null vertex label set");
      this.vertexLabels = Collections.EMPTY_SET;
    }
    this.routingPathValidator = routingPathValidator;
  }

  @Override
  public void validate() throws ModelValidationException {
    log.info("Validating if symmetric routing constraints are met");
    if (routingPathValidator==null)
      throw new ModelValidationException("initialized with empty routing path validator");

    for (String s: vertexLabels) {
      for (String d: vertexLabels) {
        if (s.equals(d))
          continue;
        List<String> forwardRoute = routingPathValidator.getRoute(routingPathValidator.generatePathKey(s, d));
        List<String> reverseRoute = routingPathValidator.getRoute(routingPathValidator.generatePathKey(d,s));
        if (!(forwardRoute.size()>0 && reverseRoute.size()>0 && forwardRoute.size()==reverseRoute.size())) {
          throw new ModelValidationException("Symmetric route mismatch from " + s + " to " + d + "Forward Path = " + forwardRoute + " reverse path : " + reverseRoute);
        }
        Iterator<String> forwardIterator = forwardRoute.iterator();
        ListIterator<String> reverseIterator = reverseRoute.listIterator(reverseRoute.size());
        while (forwardIterator.hasNext()) {
          if (!(forwardIterator.next().equals(reverseIterator.previous()))) {
            throw new ModelValidationException("Symmetric route mismatch from " + s + " to " + d + "Forward Path = " + forwardRoute + " reverse path : " + reverseRoute);
          }
        }
      }
    }
  }

}
