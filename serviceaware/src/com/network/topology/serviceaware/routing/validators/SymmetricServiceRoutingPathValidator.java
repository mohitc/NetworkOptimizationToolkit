package com.network.topology.serviceaware.routing.validators;

import com.lpapi.entities.LPModel;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SymmetricServiceRoutingPathValidator extends ModelValidator {
  private static final Logger log = LoggerFactory.getLogger(SymmetricServiceRoutingPathValidator.class);

  private Set<String> vertexLabels;

  private ServiceRoutingPathValidator serviceRoutingPathValidator;

  private int serviceClasses;

  public SymmetricServiceRoutingPathValidator(LPModel model, Set<String> vertexLabels, int serviceClasses,
                                              ServiceRoutingPathValidator serviceRoutingPathValidator) {
    super(model);
    if (vertexLabels != null)
      this.vertexLabels = vertexLabels;
    else {
      log.error("Symmetric routing path validator initialized with null vertex label set");
      this.vertexLabels = Collections.EMPTY_SET;
    }
    if (serviceClasses<=0) {
      log.error("Service classes should be >=1. Defaulting to 1");
      this.serviceClasses = 1;
    } else {
      this.serviceClasses = serviceClasses;
    }

    this.serviceRoutingPathValidator = serviceRoutingPathValidator;
  }

  @Override
  public void validate() throws ModelValidationException {
    log.info("Validating if symmetric routing constraints are met");
    if (serviceRoutingPathValidator==null)
      throw new ModelValidationException("initialized with empty routing path validator");

    for (int n=1;n<=serviceClasses;n++)
    for (String s: vertexLabels) {
      for (String d: vertexLabels) {
        if (s.equals(d))
          continue;
        List<String> forwardRoute = serviceRoutingPathValidator.getRoute(serviceRoutingPathValidator.generatePathKey(n, s, d));
        List<String> reverseRoute = serviceRoutingPathValidator.getRoute(serviceRoutingPathValidator.generatePathKey(n, d, s));
        if (!(forwardRoute.size()>0 && reverseRoute.size()>0 && forwardRoute.size()==reverseRoute.size())) {
          throw new ModelValidationException("Symmetric route mismatch from " + s + " to " + d + " for service class "+ n + ": " +
              "Forward Path = " + forwardRoute + " reverse path : " + reverseRoute);
        }
        Iterator<String> forwardIterator = forwardRoute.iterator();
        ListIterator<String> reverseIterator = reverseRoute.listIterator(reverseRoute.size());
        while (forwardIterator.hasNext()) {
          if (!(forwardIterator.next().equals(reverseIterator.previous()))) {
            throw new ModelValidationException("Symmetric route mismatch from " + s + " to " + d + " for service class "+ n +
                ": Forward Path = " + forwardRoute + " reverse path : " + reverseRoute);
          }
        }
      }
    }
  }

}
