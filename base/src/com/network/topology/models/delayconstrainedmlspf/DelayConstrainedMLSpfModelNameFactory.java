package com.network.topology.models.delayconstrainedmlspf;

import com.lpapi.entities.group.LPNameGenerator;
import com.network.topology.models.multilayerspf.MultiLayerSpfModelNameFactory;
import com.network.topology.routing.delaybound.constants.LinkDelayConstNameGenerator;
import com.network.topology.routing.delaybound.constants.RoutePathDelayConstNameGenerator;
import com.network.topology.routing.delaybound.constants.RouterDelayConstNameGenerator;
import com.network.topology.routing.delaybound.vars.RouterInPathVarNameGenerator;

import java.util.Set;

public class DelayConstrainedMLSpfModelNameFactory extends MultiLayerSpfModelNameFactory {

  public DelayConstrainedMLSpfModelNameFactory(Set<String> vertexLabels, int circuitClasses) {
    super(vertexLabels, circuitClasses);
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
