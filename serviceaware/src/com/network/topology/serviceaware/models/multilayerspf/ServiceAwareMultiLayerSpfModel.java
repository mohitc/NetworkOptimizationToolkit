package com.network.topology.serviceaware.models.multilayerspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.FixedConstants;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.mltopology.MultiLayerTopologyModel;
import com.network.topology.routing.delaybound.constants.LinkDelayConstGroupInitializer;
import com.network.topology.routing.delaybound.constants.LinkDelayConstNameGenerator;
import com.network.topology.serviceaware.SAConstantGroups;
import com.network.topology.serviceaware.SAConstraintGroups;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import com.network.topology.serviceaware.routing.constraints.*;
import com.network.topology.serviceaware.routing.delaybound.constants.ServiceAwareRoutePathDelayConstGroupInitializer;
import com.network.topology.serviceaware.routing.delaybound.constants.ServiceAwareRoutePathDelayConstNameGenerator;
import com.network.topology.serviceaware.routing.delaybound.constants.ServiceAwareRouterDelayConstGroupInitializer;
import com.network.topology.serviceaware.routing.delaybound.constants.ServiceAwareRouterDelayConstNameGenerator;
import com.network.topology.serviceaware.routing.delaybound.constraints.RouterInServicePathConstrGroupInitializer;
import com.network.topology.serviceaware.routing.delaybound.constraints.RouterInServicePathConstrNameGenerator;
import com.network.topology.serviceaware.routing.delaybound.constraints.ServiceRouteDelayConstrGroupInitializer;
import com.network.topology.serviceaware.routing.delaybound.constraints.ServiceRouteDelayConstrNameGenerator;
import com.network.topology.serviceaware.routing.delaybound.vars.RouterInServicePathVarGroupInitializer;
import com.network.topology.serviceaware.routing.delaybound.vars.RouterInServicePathVarNameGenerator;
import com.network.topology.serviceaware.routing.vars.ServiceAwareRoutingVarGroupInitializer;
import com.network.topology.serviceaware.routing.vars.ServiceAwareRoutingVarNameGenerator;
import com.network.topology.serviceaware.traffic.knowntm.constants.KnownServiceTrafficMatConstGroupInitializer;
import com.network.topology.serviceaware.traffic.knowntm.constants.KnownServiceTrafficMatConstNameGenerator;
import com.network.topology.serviceaware.traffic.knowntm.constraints.KnownServiceTmTrafficConstrGroupInitializer;
import com.network.topology.serviceaware.traffic.knowntm.constraints.KnownServiceTmTrafficConstrNameGenerator;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServiceAwareMultiLayerSpfModel extends MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareMultiLayerSpfModel.class);

  public ServiceAwareMultiLayerSpfModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    //add constant for service classes
    int serviceClasses = 3;
    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    model.createLpConstant(ServiceAwareFixedConstants.SERVICE_CLASSES, serviceClasses, constantGroup);
    //constant to indicate the max utilization of a link (Alpha)
    model.createLpConstant(FixedConstants.ALPHA, 0.7, constantGroup);
    //Max Route Delay
    model.createLpConstant(FixedConstants.ROUTE_DELAY_INF, 100000, constantGroup);

    Set<String> vertexLabels = getVertexLabels();

    //delay bounds on routers based on service classes
    LPNameGenerator saRouterDelayConstNameGenerator = new ServiceAwareRouterDelayConstNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saRouterDelayConstGroupInitializer = new ServiceAwareRouterDelayConstGroupInitializer(vertexLabels);
    model.createLPConstantGroup(SAConstantGroups.SERVICE_ROUTER_DELAY, SAConstantGroups.SERVICE_ROUTER_DELAY_DESC,
        saRouterDelayConstNameGenerator, saRouterDelayConstGroupInitializer);

    //Link Delay constants computed based on shortest delay path in the physical topology
    LPNameGenerator linkDelayConstantNameGenerator = new LinkDelayConstNameGenerator(vertexLabels);
    LinkDelayConstGroupInitializer linkDelayConstGroupInitializer = new LinkDelayConstGroupInitializer(vertexLabels, _instance);
    model.createLPConstantGroup(ConstantGroups.LINK_DELAY, ConstantGroups.LINK_DELAY_DESC, linkDelayConstantNameGenerator, linkDelayConstGroupInitializer);

    //Delay bound on routes based on service classes
    LPNameGenerator saRoutePathDelayConstNameGenerator = new ServiceAwareRoutePathDelayConstNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saRoutePathDelayConstGroupInitializer = new ServiceAwareRoutePathDelayConstGroupInitializer(vertexLabels);
    model.createLPConstantGroup(SAConstantGroups.SERVICE_PATH_DELAY, SAConstantGroups.SERVICE_PATH_DELAY_DESC,
        saRoutePathDelayConstNameGenerator, saRoutePathDelayConstGroupInitializer);

    //Traffic matrix (known)
    LPNameGenerator knownServiceTrafficMatConstNameGenerator = new KnownServiceTrafficMatConstNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer knownServiceTrafficMatConstGroupInitializer = new KnownServiceTrafficMatConstGroupInitializer(vertexLabels, _instance);
    model.createLPConstantGroup(SAConstantGroups.SA_TRAFFIC_MAT, SAConstantGroups.SA_TRAFFIC_MAT_DESC,
        knownServiceTrafficMatConstNameGenerator, knownServiceTrafficMatConstGroupInitializer);

  }

  public void initVarGroups() throws LPVarGroupException {
    super.initVarGroups();
    int serviceClasses;
    try {
      serviceClasses = (int)model.getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
    } catch (LPConstantException e) {
      log.error("Could not find constant : ", e);
      throw  new LPVarGroupException("Could not find constant for service classes");
    }

    Set<String> vertexLabels = getVertexLabels();
    LPNameGenerator serviceAwareRoutingNameGenerator = new ServiceAwareRoutingVarNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer serviceAwareRoutingVarGroupInitializer = new ServiceAwareRoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(SAVarGroups.SA_ROUTING, SAVarGroups.SA_ROUTING_DESC, serviceAwareRoutingNameGenerator, serviceAwareRoutingVarGroupInitializer);

    //Router in service path
    LPNameGenerator routerInServicePathNameGenerator = new RouterInServicePathVarNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer routerInServicePathVarGroupInitializer = new RouterInServicePathVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(SAVarGroups.SA_ROUTER_IN_PATH, SAVarGroups.SA_ROUTER_IN_PATH_DESC, routerInServicePathNameGenerator, routerInServicePathVarGroupInitializer);
  }


  public void initConstraintGroups() throws LPConstraintGroupException {
    super.initConstraintGroups();
    Set<String> vertexLabels = getVertexLabels();

    int serviceClasses;
    try {
      serviceClasses = (int)model.getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
    } catch (LPConstantException e) {
      log.error("Could not find constant : ", e);
      throw  new LPConstraintGroupException("Could not find constant for service classes");
    }

    //Routing if link exists
    LPNameGenerator serviceAwareRoutingConstrNameGenerator = new ServiceAwareRoutingConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer serviceAwareRoutingConstrGroupInitializer = new ServiceAwareRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTING_LINK_EXISTS, SAConstraintGroups.SA_ROUTING_LINK_EXISTS_DESC,
        serviceAwareRoutingConstrNameGenerator, serviceAwareRoutingConstrGroupInitializer);

    //Symmetric routing
    LPNameGenerator saSymmetricRoutingConstrNameGenerator = new ServiceAwareSymmetricRoutingConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saSymmetricRoutingConstrGroupInitializer = new ServiceAwareSymmetricRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_SYMM_ROUTING, SAConstraintGroups.SA_SYMM_ROUTING_DESC,
        saSymmetricRoutingConstrNameGenerator, saSymmetricRoutingConstrGroupInitializer);

    //Routing continuity
    LPNameGenerator saRoutingContinuityConstrNameGenerator = new ServiceAwareRoutingContinuityConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saRoutingContinuityConstrGroupInitializer = new ServiceAwareRoutingContinuityConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTING_CONTINUITY, SAConstraintGroups.SA_ROUTING_CONTINUITY_DESC,
        saRoutingContinuityConstrNameGenerator, saRoutingContinuityConstrGroupInitializer);

    //Source Loop Avoidance
    LPNameGenerator saSourceLoopAvoidanceConstrNameGenerator = new ServiceAwareSourceLoopAvoidanceConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saSourceLoopAvoidanceConstrGroupInitializer = new ServiceAwareSourceLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_SOURCE_LOOP_AVOIDANCE, SAConstraintGroups.SA_SOURCE_LOOP_AVOIDANCE_DESC,
        saSourceLoopAvoidanceConstrNameGenerator, saSourceLoopAvoidanceConstrGroupInitializer);

    //Destination loop avoidance
    LPNameGenerator saDestLoopAvoidanceConstrNameGenerator = new ServiceAwareDestLoopAvoidanceConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saDestLoopAvoidanceConstrGroupInitializer = new ServiceAwareDestLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_DEST_LOOP_AVOIDANCE, SAConstraintGroups.SA_DEST_LOOP_AVOIDANCE_DESC,
        saDestLoopAvoidanceConstrNameGenerator, saDestLoopAvoidanceConstrGroupInitializer);

    //Router in service path
    LPNameGenerator routerInServicePathConstrNameGenerator = new RouterInServicePathConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer routerInServicePathConstrGroupInitializer = new RouterInServicePathConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTER_IN_PATH, SAConstraintGroups.SA_ROUTER_IN_PATH_DESC,
        routerInServicePathConstrNameGenerator, routerInServicePathConstrGroupInitializer);

    //Path Delay constraints on service path
    LPNameGenerator serviceRouteDelayConstrNameGenerator = new ServiceRouteDelayConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer serviceRouteDelayConstrGroupInitializer = new ServiceRouteDelayConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTE_DELAY, SAConstraintGroups.SA_ROUTE_DELAY_DESC, serviceRouteDelayConstrNameGenerator,
        serviceRouteDelayConstrGroupInitializer);

    //Traffic constraints (known traffic matrix)
    LPNameGenerator knownServiceTmTrafficConstrNameGenerator = new KnownServiceTmTrafficConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer knownServiceTmTrafficConstrGroupInitializer = new KnownServiceTmTrafficConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_KNOWN_TRAFFIC_MAT, SAConstraintGroups.SA_KNOWN_TRAFFIC_MAT_DESC,
        knownServiceTmTrafficConstrNameGenerator, knownServiceTmTrafficConstrGroupInitializer);
  }

  @Override
  public TopologyManager getExtractedModel() throws ModelExtractionException {
    return null;
  }

  @Override
  public void initModelValidators() {

  }

  @Override
  public ModelExtractor<TopologyManager> initModelExtractor() {
    return null;
  }
}
