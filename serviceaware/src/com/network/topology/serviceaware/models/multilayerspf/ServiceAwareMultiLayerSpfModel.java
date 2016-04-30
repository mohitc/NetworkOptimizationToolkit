package com.network.topology.serviceaware.models.multilayerspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.FixedConstants;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.fixedtopology.FixedTopologyModel;
import com.network.topology.models.mltopology.MultiLayerTopologyModel;
import com.network.topology.routing.delaybound.constants.LinkDelayConstGroupInitializer;
import com.network.topology.routing.delaybound.constants.LinkDelayConstNameGenerator;
import com.network.topology.serviceaware.SAConstantGroups;
import com.network.topology.serviceaware.SAConstraintGroups;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import com.network.topology.serviceaware.models.traffic.generator.ServiceTrafficMatrixGenerator;
import com.network.topology.serviceaware.models.traffic.parsing.ServiceTrafficMatrixSplit;
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
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import com.topology.primitives.properties.TEPropertyKey;
import com.topology.primitives.properties.converters.impl.MapConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class ServiceAwareMultiLayerSpfModel extends MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareMultiLayerSpfModel.class);

  public ServiceAwareMultiLayerSpfModel(String circuitConfFile, TopologyManager manager, String instanceName) {
    super(circuitConfFile, manager, instanceName);
  }

  public ServiceAwareMultiLayerSpfModel(String circuitConfFile, TopologyManager manager, String instanceName, String exportPath) {
    super(circuitConfFile, manager, instanceName, exportPath);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    //add constant for service classes
    int serviceClasses = 2;
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
    LPNameGenerator knownServiceTmTrafficConstrNameGenerator = new KnownServiceTmTrafficConstrNameGenerator(vertexLabels);
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

  public static void main (String[] args) {
    try {
      //arg 1 = file name for topology
      //arg 2 = file name for circuit capacity
      //arg 3 = path to import folder
      //arg 4 = identifier for the import model
      //arg 5 = path to export folder
      //arg 6 = identifier for the export model
      //arg 7 = path to xml defining the service traffic matrix split
      if (args==null || args.length!=7) {
        log.error("Invalid arguments provided to program. Expected {path to topology} {path to circuit capacity configuration} {path to export folder} {model identifier} {boolean to indicate if model shold be solved}");
      }

      TopologyManager manager = new TopologyManagerImpl(args[3]);
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile(args[0], manager);

      FixedTopologyModel lpModel = new FixedTopologyModel(args[1], manager, args[3], args[2]);
      log.info("Attempting to load model from export information");
/*
      lpModel.init();
      lpModel.compute();
      lpModel.postCompute();
      lpModel.exportModel();
*/
      lpModel.importModel();

      TopologyManager newTopology = lpModel.getExtractedModel();

      TEPropertyKey demands = newTopology.registerKey("Demands", "Demands for the topology", Map.class, MapConverter.class);
      ServiceTrafficMatrixGenerator generator = new ServiceTrafficMatrixGenerator();
      log.info("parsing service traffic matrix split");
      ServiceTrafficMatrixSplit split = generator.parseXml(args[6]);
      if (split==null) {
        log.error("Could not parse split successfully. Exiting");
        return;
      }

      newTopology.addProperty(demands, generator.generateServiceTrafficMatrix(manager.getProperty(demands, Map.class), split));

      ServiceAwareMultiLayerSpfModel newModel = new ServiceAwareMultiLayerSpfModel(args[1], newTopology, args[5], args[4]);
      newModel.init();
      newModel.compute();
      newModel.postCompute();

    } catch (LPModelException e) {
      log.error("Error initializing model", e);
    } catch (TopologyException e) {
      log.error("Error initializing topology", e);
    } catch (FileFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      log.error("Error initializing model file", e);
    } catch (ModelExtractionException e) {
      log.error("Error extracting topology from initial solution", e);
    }
  }

}
