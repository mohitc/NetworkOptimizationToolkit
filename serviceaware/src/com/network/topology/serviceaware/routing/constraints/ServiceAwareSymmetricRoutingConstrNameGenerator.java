package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.group.LPNamePrefixValidator;
import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstraintPrefixes;
import com.network.topology.serviceaware.routing.ServiceRoutingNameValidatorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class ServiceAwareSymmetricRoutingConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = ServiceAwareConstraintPrefixes.SYMMETRIC_SERVICE_ROUTING;

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareSymmetricRoutingConstrNameGenerator.class);

  public ServiceAwareSymmetricRoutingConstrNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 5);
    List<LPNamePrefixValidator> validators = ServiceRoutingNameValidatorHelper.getServiceAwareNameValidators(serviceClasses, vertexVars, log);
    for (LPNamePrefixValidator validator : validators) {
      addValidator(validator);
    }
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
