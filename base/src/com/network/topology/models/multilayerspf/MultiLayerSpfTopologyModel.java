package com.network.topology.models.multilayerspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPSolutionStatus;
import com.lpapi.entities.glpk.impl.GlpkLPModel;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.skeleton.impl.SkeletonLPModel;
import com.lpapi.exception.*;
import com.lpapi.export.LPModelExporter;
import com.lpapi.export.LPModelExporterType;
import com.lpapi.export.LPModelImporter;
import com.lpapi.export.LPModelImporterType;
import com.lpapi.export.factory.LPModelExporterFactory;
import com.lpapi.export.factory.LPModelImporterFactory;
import com.lpapi.export.jsonfile.JSONFileLPModelExporter;
import com.lpapi.export.jsonfile.JSONFileLPModelImporter;
import com.network.topology.ConstantGroups;
import com.network.topology.ConstraintGroups;
import com.network.topology.FixedConstants;
import com.network.topology.VarGroups;
import com.network.topology.capacity.constants.InitialCapacityConstGroupInitializer;
import com.network.topology.capacity.constraints.ActualCapacityGroupInitializer;
import com.network.topology.capacity.constraints.ActualCapacityNameGenerator;
import com.network.topology.capacity.vars.CapacityVarGroupInitializer;
import com.network.topology.capacity.vars.CapacityVarNameGenerator;
import com.network.topology.dyncircuits.constraints.DynCircuitBoundConstrGroupInitializer;
import com.network.topology.dyncircuits.constraints.DynCircuitBoundConstrNameGenerator;
import com.network.topology.dyncircuits.constraints.SymDynCirConstrGroupInitializer;
import com.network.topology.dyncircuits.constraints.SymDynCirConstrNameGenerator;
import com.network.topology.dyncircuits.parser.DynCircuitClassParser;
import com.network.topology.dyncircuits.vars.DynCircuitVarGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarGroupInitializer;
import com.network.topology.forwarding.vars.ForwardingVarNameGenerator;
import com.network.topology.linkexists.constants.LinkExistsConstantGroupInitializer;
import com.network.topology.linkexists.constraints.LinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.LinkExistsConstrNameGenerator;
import com.network.topology.linkexists.validators.FixedLinkExistsValidator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsVarGroupInitializer;
import com.network.topology.linkweight.constants.LinkWeightConstantGroupInitializer;
import com.network.topology.linkweight.constants.LinkWeightConstantNameGenerator;
import com.network.topology.linkweight.constraints.LinkWeightConstrGroupInitializer;
import com.network.topology.linkweight.constraints.LinkWeightConstrNameGenerator;
import com.network.topology.linkweight.vars.LinkWeightVarGroupInitializer;
import com.network.topology.linkweight.vars.LinkWeightVarNameGenerator;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.fixedtopology.ModelTopologyExtractor;
import com.network.topology.models.mltopology.MultiLayerTopologyModel;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import com.network.topology.objfn.MinDynCirCostObjFnGenerator;
import com.network.topology.routing.constraints.*;
import com.network.topology.routing.delaybound.validators.RouterInPathValidator;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.vars.RoutingCostVarGroupInitializer;
import com.network.topology.routing.routingcost.vars.RoutingCostVarNameGenerator;
import com.network.topology.routing.validators.RoutingPathValidator;
import com.network.topology.routing.validators.SymmetricRoutingPathValidator;
import com.network.topology.routing.vars.RoutingNameGenerator;
import com.network.topology.routing.vars.RoutingVarGroupInitializer;
import com.network.topology.traffic.knowntm.constants.KnownTrafficMatConstGroupInitializer;
import com.network.topology.traffic.knowntm.constants.KnownTrafficMatConstNameGenerator;
import com.network.topology.traffic.knowntm.constraints.KnownTmTrafficConstrGroupInitializer;
import com.network.topology.traffic.knowntm.constraints.KnownTmTrafficConstrNameGenerator;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MultiLayerSpfTopologyModel extends MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(MultiLayerSpfTopologyModel.class);

  public MultiLayerSpfTopologyModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    model.createLpConstant(FixedConstants.ROUTING_COST_MAX, 1000, constantGroup);
    //constant to indicate the max capacity C(inf) for a link between a pair of nodes
    model.createLpConstant(FixedConstants.W_INF, 100000, constantGroup);
    //constant to indicate the max utilization of a link (Alpha)
    model.createLpConstant(FixedConstants.ALPHA, 0.7, constantGroup);

    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator linkWeightConstantNameGenerator = new LinkWeightConstantNameGenerator(vertexLabels);
    LinkWeightConstantGroupInitializer linkWeightConstantGroupInitializer = new LinkWeightConstantGroupInitializer(vertexLabels);
    model.createLPConstantGroup(ConstantGroups.LINK_WEIGHT, ConstantGroups.LINK_WEIGHT_DESC, linkWeightConstantNameGenerator,
        linkWeightConstantGroupInitializer);

    LPNameGenerator knownTrafficMatConstNameGenerator = new KnownTrafficMatConstNameGenerator(vertexLabels);
    KnownTrafficMatConstGroupInitializer knownTrafficMatConstGroupInitializer = new KnownTrafficMatConstGroupInitializer(getVertexLabels(),_instance);
    model.createLPConstantGroup(ConstantGroups.TRAFFIC_MAT, ConstantGroups.TRAFFIC_MAT_DESC, knownTrafficMatConstNameGenerator,
        knownTrafficMatConstGroupInitializer);

  }

  public void initVarGroups() throws LPVarGroupException {

    super.initVarGroups();

    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator routingNameGenerator = new RoutingNameGenerator(vertexLabels);
    RoutingVarGroupInitializer routingVarGroupInitializer = new RoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.ROUTING, VarGroups.ROUTING_DESC, routingNameGenerator, routingVarGroupInitializer);

    LPNameGenerator forwardingNameGenerator = new ForwardingVarNameGenerator(vertexLabels);
    ForwardingVarGroupInitializer forwardingGroupInitializer = new ForwardingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.FORWARDING, VarGroups.FORWARDING_DESC, forwardingNameGenerator, forwardingGroupInitializer);

    LPNameGenerator rcVarNameGenerator = new RoutingCostVarNameGenerator(vertexLabels);
    RoutingCostVarGroupInitializer rcVarGroupInitializer = new RoutingCostVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.ROUTING_COST, VarGroups.ROUTING_COST_DESC, rcVarNameGenerator, rcVarGroupInitializer);

    LPNameGenerator linkWeightVarNameGenerator = new LinkWeightVarNameGenerator(vertexLabels);
    LinkWeightVarGroupInitializer linkWeightVarGroupInitializer = new LinkWeightVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.LINK_WEIGHT, VarGroups.LINK_WEIGHT_DESC, linkWeightVarNameGenerator, linkWeightVarGroupInitializer);

  }

  public void initConstraintGroups() throws LPConstraintGroupException {

    super.initConstraintGroups();
    Set<String> vertexLabels = getVertexLabels();

    KnownTmTrafficConstrNameGenerator knownTmTrafficConstrNameGenerator = new KnownTmTrafficConstrNameGenerator(vertexLabels);
    KnownTmTrafficConstrGroupInitializer knownTmTrafficConstrGroupInitializer = new KnownTmTrafficConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.KNOWN_TRAFFIC_MAT, ConstraintGroups.KNOWN_TRAFFIC_MAT_DESC,
      knownTmTrafficConstrNameGenerator, knownTmTrafficConstrGroupInitializer);

    //Routing Constraints
    RoutingConstrNameGenerator routingConstrNameGenerator = new RoutingConstrNameGenerator(vertexLabels);
    RoutingConstrGroupInitializer routingConstrGroupInitializer = new RoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstantGroups.ROUTING_LINK_EXISTS, ConstantGroups.ROUTING_LINK_EXISTS_DESC, routingConstrNameGenerator, routingConstrGroupInitializer);

    RoutingContinuityConstrNameGenerator routingContinuityConstrNameGenerator = new RoutingContinuityConstrNameGenerator(vertexLabels);
    RoutingContinuityConstrGroupInitializer routingContinuityConstrGroupInitializer = new RoutingContinuityConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.ROUTING_CONTINUITY, ConstraintGroups.ROUTING_CONTINUITY_DESC, routingContinuityConstrNameGenerator, routingContinuityConstrGroupInitializer);

    SymmetricRoutingConstrNameGenerator symmetricRoutingConstrNameGenerator = new SymmetricRoutingConstrNameGenerator(vertexLabels);
    SymmetricRoutingConstrGroupInitializer symmetricRoutingConstrGroupInitializer = new SymmetricRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.SYMMETRIC_ROUTING, ConstraintGroups.SYMMETRIC_ROUTING_DESC, symmetricRoutingConstrNameGenerator, symmetricRoutingConstrGroupInitializer);

    RoutingCostConstrNameGenerator routingCostConstrNameGenerator = new RoutingCostConstrNameGenerator(vertexLabels);
    RoutingCostConstrGroupInitializer routingCostConstrGroupInitializer = new RoutingCostConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.ROUTING_COST, ConstraintGroups.ROUTING_COST_DESC, routingCostConstrNameGenerator, routingCostConstrGroupInitializer);

    MinRoutingCostConstrNameGenerator minRoutingCostConstrNameGenerator = new MinRoutingCostConstrNameGenerator(vertexLabels);
    MinRoutingCostConstrGroupInitializer minRoutingCostConstrGroupInitializer = new MinRoutingCostConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.MIN_ROUTING_COST, ConstraintGroups.MIN_ROUTING_COST_DESC, minRoutingCostConstrNameGenerator, minRoutingCostConstrGroupInitializer);

    SourceLoopAvoidanceConstrNameGenerator sourceLoopAvoidanceConstrNameGenerator = new SourceLoopAvoidanceConstrNameGenerator(vertexLabels);
    SourceLoopAvoidanceConstrGroupInitializer sourceLoopAvoidanceConstrGroupInitializer = new SourceLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.SOURCE_LOOP_AVOIDANCE, ConstraintGroups.SOURCE_LOOP_AVOIDANCE_DESC, sourceLoopAvoidanceConstrNameGenerator, sourceLoopAvoidanceConstrGroupInitializer);

    DestLoopAvoidanceConstrNameGenerator destLoopAvoidanceConstrNameGenerator = new DestLoopAvoidanceConstrNameGenerator(vertexLabels);
    DestLoopAvoidanceConstrGroupInitializer destLoopAvoidanceConstrGroupInitializer = new DestLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.DEST_LOOP_AVOIDANCE, ConstraintGroups.DEST_LOOP_AVOIDANCE_DESC, destLoopAvoidanceConstrNameGenerator, destLoopAvoidanceConstrGroupInitializer);
    //Forwarding Constraints

    UniqueForwardingConstrNameGenerator uniqueForwardingConstrNameGenerator = new UniqueForwardingConstrNameGenerator(vertexLabels);
    UniqueForwardingConstrGroupInitializer uniqueForwardingConstrGroupInitializer = new UniqueForwardingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.UNIQUE_FORWARDING, ConstraintGroups.UNIQUE_FORWARDING_DESC, uniqueForwardingConstrNameGenerator, uniqueForwardingConstrGroupInitializer);

    ForwardingBasedRoutingConstrNameGenerator forwardingBasedRoutingConstrNameGenerator = new ForwardingBasedRoutingConstrNameGenerator(vertexLabels);
    ForwardingBasedRoutingConstrGroupInitializer forwardingBasedRoutingConstrGroupInitializer = new ForwardingBasedRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.FORWARDING_ROUTING, ConstraintGroups.FORWARDING_ROUTING_DESC, forwardingBasedRoutingConstrNameGenerator, forwardingBasedRoutingConstrGroupInitializer);

    //Link weight constraint
    LinkWeightConstrNameGenerator linkWeightConstrNameGenerator = new LinkWeightConstrNameGenerator(vertexLabels);
    LinkWeightConstrGroupInitializer linkWeightConstrGroupInitializer = new LinkWeightConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.LINK_WEIGHT, ConstraintGroups.LINK_WEIGHT_DESC, linkWeightConstrNameGenerator, linkWeightConstrGroupInitializer);

  }

  public TopologyManager getExtractedModel() throws ModelExtractionException {
    ModelExtractor<TopologyManager> extractor = initModelExtractor();
    return extractor.extractModel(model);
  }

  public void initModelValidators() {
    validatorList = new ArrayList<>();
    Set<String> vertexLabels = getVertexLabels();
    RoutingPathValidator routingPathValidator = new RoutingPathValidator(model, vertexLabels, new RoutingNameGenerator(vertexLabels));
    validatorList.add(routingPathValidator);
    validatorList.add(new SymmetricRoutingPathValidator(model, vertexLabels, routingPathValidator));
  }

  public ModelExtractor<TopologyManager> initModelExtractor() {
    Set<String> vertexLabels = getVertexLabels();
    return new ModelTopologyExtractor(getVertexLabels(), new LinkExistsNameGenerator(vertexLabels), new CapacityVarNameGenerator(vertexLabels), _instance);
  }

  public static void main (String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      MultiLayerSpfTopologyModel lpModel = new MultiLayerSpfTopologyModel("conf/circuit-cap.xml", manager);
      lpModel.init();
      lpModel.compute();
      lpModel.postCompute();

    } catch (LPModelException e) {
      log.error("Error initializing model", e);
    } catch (TopologyException e) {
      log.error("Error initializing topology", e);
    } catch (FileFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      log.error("Error initializing model file", e);
    }
  }
}