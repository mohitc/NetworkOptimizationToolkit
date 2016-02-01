package com.network.topology.serviceaware.models;

import com.lpapi.entities.group.LPNameGenerator;
import com.network.topology.serviceaware.routing.vars.ServiceAwareRoutingVarNameGenerator;
import com.network.topology.serviceaware.traffic.knowntm.constants.KnownServiceTrafficMatConstNameGenerator;

import java.util.Set;

public class ServiceAwareRoutingNameFactory {

  private int serviceClasses;

  private Set<String> vertexLabels;

  public ServiceAwareRoutingNameFactory(int serviceClasses, Set<String> vertexLabels) {
    this.vertexLabels = vertexLabels;
    this.serviceClasses = serviceClasses;
  }


  // name generators for variables
  private LPNameGenerator _serviceAwareRoutingNameGenerator;

  public LPNameGenerator getServiceRoutingNameGenerator() {
    if (_serviceAwareRoutingNameGenerator==null) {
      _serviceAwareRoutingNameGenerator = new ServiceAwareRoutingVarNameGenerator(vertexLabels, serviceClasses);
    }
    return _serviceAwareRoutingNameGenerator;
  }

  //Name generators for constants
  private LPNameGenerator _knownServiceTrafficMatConstNameGenerator;

  public LPNameGenerator getKnownServiceTrafficMatConstNameGenerator() {
    if (_knownServiceTrafficMatConstNameGenerator == null) {
      _knownServiceTrafficMatConstNameGenerator = new KnownServiceTrafficMatConstNameGenerator(vertexLabels, serviceClasses);
    }
    return _knownServiceTrafficMatConstNameGenerator;
  }

}
