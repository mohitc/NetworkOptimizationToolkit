package com.network.topology.serviceaware.routing.constraints;

import com.lpapi.entities.group.LPNamePrefixValidator;
import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareConstraintPrefixes;
import com.network.topology.serviceaware.routing.ServiceRoutingNameValidatorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServiceAwareRoutingConstrNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = ServiceAwareConstraintPrefixes.SERVICE_ROUTING_IFF_LINK_EXISTS;

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRoutingConstrNameGenerator.class);

  public ServiceAwareRoutingConstrNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 5);
    List<LPNamePrefixValidator> validators = ServiceRoutingNameValidatorHelper.getServiceAwareNameValidators(serviceClasses, vertexVars, log);
    validators.forEach(v->addValidator(v));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
