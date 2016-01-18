package com.network.topology.models.fixedtopology;

import com.lpapi.entities.group.LPNameGenerator;
import com.network.topology.capacity.vars.CapacityVarNameGenerator;
import com.network.topology.dyncircuits.vars.DynCircuitVarNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarNameGenerator;
import com.network.topology.linkexists.constants.LinkExistsConstantNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.routing.routingcost.vars.RoutingCostVarNameGenerator;
import com.network.topology.routing.vars.RoutingNameGenerator;

import java.util.Set;

public class FixedTopologyModelNameFactory {

  private Set<String> vertexLabels;

  public FixedTopologyModelNameFactory(Set<String> vertexLabels) {
    this.vertexLabels = vertexLabels;
  }

  private LPNameGenerator _linkExistsConstantNameGenerator;

  public LPNameGenerator getLinkExistsConstantNameGenerator() {
    if (_linkExistsConstantNameGenerator == null) {
      _linkExistsConstantNameGenerator = new LinkExistsConstantNameGenerator(vertexLabels);
    }
    return _linkExistsConstantNameGenerator;
  }


  private LPNameGenerator _linkExistsNameGenerator;

  public LPNameGenerator getLinkExistsNameGenerator() {
    if (_linkExistsNameGenerator==null) {
      _linkExistsNameGenerator =   new LinkExistsNameGenerator(vertexLabels);
    }
    return _linkExistsNameGenerator;
  }

  private LPNameGenerator _routingNameGenerator;

  public LPNameGenerator getRoutingNameGenerator() {
    if (_routingNameGenerator == null) {
      _routingNameGenerator = new RoutingNameGenerator(vertexLabels);
    }
    return _routingNameGenerator;
  }

  private LPNameGenerator _forwardingNameGenerator;

  public LPNameGenerator getForwardingNameGenerator() {
    if (_forwardingNameGenerator == null) {
      _forwardingNameGenerator = new ForwardingVarNameGenerator(vertexLabels);
    }
    return _forwardingNameGenerator;
  }

  private LPNameGenerator _rcVarNameGenerator;

  public LPNameGenerator getRoutingCostNameGenerator() {
    if (_rcVarNameGenerator == null) {
      _rcVarNameGenerator = new RoutingCostVarNameGenerator(vertexLabels);
    }
    return _rcVarNameGenerator;
  }

  private LPNameGenerator _dynCircuitVarNameGenerator;

  public LPNameGenerator getDynamicCircuitNameGenerator(int circuitClasses) {
    if (_dynCircuitVarNameGenerator == null) {
      _dynCircuitVarNameGenerator = new DynCircuitVarNameGenerator(circuitClasses, vertexLabels);
    }
    return _dynCircuitVarNameGenerator;
  }

  private LPNameGenerator _capacityVarNameGenerator;

  public LPNameGenerator getCapacityNameGenerator() {
    if (_capacityVarNameGenerator == null) {
      _capacityVarNameGenerator = new CapacityVarNameGenerator(vertexLabels);
    }
    return _capacityVarNameGenerator;
  }

}
