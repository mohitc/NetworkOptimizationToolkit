package com.network.topology.models.multilayerspf;

import com.lpapi.entities.group.LPNameGenerator;
import com.network.topology.capacity.constants.InitialCapacityConstNameGenerator;
import com.network.topology.capacity.vars.CapacityVarNameGenerator;
import com.network.topology.dyncircuits.vars.DynCircuitVarNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarNameGenerator;
import com.network.topology.linkexists.constants.LinkExistsConstantNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.linkweight.constants.LinkWeightConstantNameGenerator;
import com.network.topology.linkweight.vars.LinkWeightVarNameGenerator;
import com.network.topology.routing.delaybound.constants.LinkDelayConstNameGenerator;
import com.network.topology.routing.delaybound.constants.RoutePathDelayConstNameGenerator;
import com.network.topology.routing.delaybound.constants.RouterDelayConstNameGenerator;
import com.network.topology.routing.delaybound.vars.RouterInPathVarNameGenerator;
import com.network.topology.routing.routingcost.vars.RoutingCostVarNameGenerator;
import com.network.topology.routing.vars.RoutingNameGenerator;
import com.network.topology.traffic.knowntm.constants.KnownTrafficMatConstNameGenerator;

import java.util.Collections;
import java.util.Set;

public class MultiLayerSpfModelNameFactory {

  protected Set<String> vertexLabels;

  protected int circuitClasses;

  public MultiLayerSpfModelNameFactory(Set<String> vertexLabels, int circuitClasses) {
    this.vertexLabels = Collections.unmodifiableSet(vertexLabels);
    this.circuitClasses = circuitClasses;
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

  public LPNameGenerator getDynamicCircuitNameGenerator() {
    if (_dynCircuitVarNameGenerator == null) {
      _dynCircuitVarNameGenerator = new DynCircuitVarNameGenerator(circuitClasses, vertexLabels);
    }
    return _dynCircuitVarNameGenerator;
  }

  private LPNameGenerator _capacityVarNameGenerator;

  public LPNameGenerator getCapacityVarNameGenerator() {
    if (_capacityVarNameGenerator == null) {
      _capacityVarNameGenerator = new CapacityVarNameGenerator(vertexLabels);
    }
    return _capacityVarNameGenerator;
  }

  private LPNameGenerator _knownTrafficMatConstNameGenerator;

  public LPNameGenerator getKnownTrafficMatConstNameGenerator() {
    if (_knownTrafficMatConstNameGenerator == null) {
      _knownTrafficMatConstNameGenerator = new KnownTrafficMatConstNameGenerator(vertexLabels);
    }
    return _knownTrafficMatConstNameGenerator;
  }

  private LPNameGenerator _initialCapacityConstNameGenerator;

  public LPNameGenerator getInitialCapacityConstNameGenerator() {
    if (_initialCapacityConstNameGenerator == null) {
      _initialCapacityConstNameGenerator = new InitialCapacityConstNameGenerator(vertexLabels);
    }
    return _initialCapacityConstNameGenerator;
  }

  private LPNameGenerator _linkWeightVarNameGenerator;

  public LPNameGenerator getLinkWeightVarNameGenerator() {
    if (_linkWeightVarNameGenerator == null) {
      _linkWeightVarNameGenerator = new LinkWeightVarNameGenerator(vertexLabels);
    }
    return _linkWeightVarNameGenerator;
  }

  private LPNameGenerator _linkWeightConstantNameGenerator;

  public LPNameGenerator getLinkWeightConstantNameGenerator() {
    if (_linkWeightConstantNameGenerator == null) {
      _linkWeightConstantNameGenerator = new LinkWeightConstantNameGenerator(vertexLabels);
    }
    return _linkWeightConstantNameGenerator;
  }

  private LPNameGenerator _linkDelayConstantNameGenerator;

  public LPNameGenerator getLinkDelayConstantNameGenerator() {
    if (_linkDelayConstantNameGenerator==null) {
      _linkDelayConstantNameGenerator = new LinkDelayConstNameGenerator(vertexLabels);
    }
    return _linkDelayConstantNameGenerator;
  }

  private LPNameGenerator _routerDelayConstantNameGenerator;

  public LPNameGenerator getRouterDelayConstantNameGenerator() {
    if (_routerDelayConstantNameGenerator==null) {
      _routerDelayConstantNameGenerator = new RouterDelayConstNameGenerator(vertexLabels);
    }
    return _routerDelayConstantNameGenerator;
  }

  private LPNameGenerator _routerInPathVarNameGenerator;

  public LPNameGenerator getRouterInPathVarNameGenerator() {
    if (_routerInPathVarNameGenerator==null) {
      _routerInPathVarNameGenerator = new RouterInPathVarNameGenerator(vertexLabels);
    }
    return _routerInPathVarNameGenerator;
  }

  private LPNameGenerator _routePathDelayConstantNameGenerator;

  public LPNameGenerator getRoutePathDelayConstantNameGenerator() {
    if (_routePathDelayConstantNameGenerator==null) {
      _routePathDelayConstantNameGenerator = new RoutePathDelayConstNameGenerator(vertexLabels);
    }
    return _routePathDelayConstantNameGenerator;
  }
}