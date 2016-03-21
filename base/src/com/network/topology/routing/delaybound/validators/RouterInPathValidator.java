package com.network.topology.routing.delaybound.validators;

import com.lpapi.entities.LPModel;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPNameException;
import com.lpapi.exception.LPVarException;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import com.network.topology.routing.validators.RoutingPathValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RouterInPathValidator extends ModelValidator{

  private static final Logger log = LoggerFactory.getLogger(RouterInPathValidator.class);

  private Set<String> vertexLabels;

  private RoutingPathValidator routingPathValidator;

  private LPNameGenerator routerInPathVarNameGenerator;

  public RouterInPathValidator(LPModel model, Set<String> vertexLabels, RoutingPathValidator routingPathValidator,
                               LPNameGenerator routerInPathVarNameGenerator) {
    super(model);
    if (vertexLabels != null)
      this.vertexLabels = vertexLabels;
    else {
      log.error("Symmetric routing path validator initialized with null vertex label set");
      this.vertexLabels = Collections.EMPTY_SET;
    }
    this.routingPathValidator = routingPathValidator;
    this.routerInPathVarNameGenerator = routerInPathVarNameGenerator;
  }

  @Override
  public void validate() throws ModelValidationException {
    log.info("Validating if symmetric routing constraints are met");
    if (routingPathValidator==null)
      throw new ModelValidationException("initialized with empty routing path validator");

    if (routerInPathVarNameGenerator==null)
      throw new ModelValidationException("Initialized with null router in path name generator");

    for (String s: vertexLabels) {
      for (String d: vertexLabels) {
        if (s.equals(d))
          continue;
        List<String> route = routingPathValidator.getRoute(routingPathValidator.generatePathKey(s, d));

        for (String i: vertexLabels) {
          try {
            int res = getModel().getLPVar(routerInPathVarNameGenerator.getName(s, d, i)).getResult().intValue();
            if ((route.contains(i) && res == 0) || (!route.contains(i) && res==1))
              throw new ModelValidationException("node " + i + " not validated in path from " + s + " to " + d + ": route = " + (route.contains(i)?1:0) + ", var: " + res);
          } catch (LPVarException | LPNameException e) {
            throw new ModelValidationException("Error in extracting var", e);
          }
        }
      }
    }
  }
}
