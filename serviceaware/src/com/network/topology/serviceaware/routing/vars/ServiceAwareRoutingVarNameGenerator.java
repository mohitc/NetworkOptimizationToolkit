package com.network.topology.serviceaware.routing.vars;

import com.lpapi.entities.group.LPNamePrefixValidator;
import com.lpapi.entities.group.generators.LPNameGeneratorImpl;
import com.lpapi.exception.LPNameException;
import com.network.topology.serviceaware.ServiceAwareVariablePrefixes;
import com.network.topology.serviceaware.routing.ServiceRoutingNameValidatorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServiceAwareRoutingVarNameGenerator extends LPNameGeneratorImpl {

  private static final String PREFIX = ServiceAwareVariablePrefixes.SERVICE_AWARE_ROUTING;

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareRoutingVarNameGenerator.class);

  public ServiceAwareRoutingVarNameGenerator(Set<String> vertexVars, int serviceClasses) {
    super(PREFIX, 5);
    List<LPNamePrefixValidator> validators = ServiceRoutingNameValidatorHelper.getServiceAwareNameValidators(serviceClasses, vertexVars, log);
    validators.forEach(v->addValidator(v));
  }

  @Override
  protected void validatePrefixConstraint(List strings) throws LPNameException {
  }
}
