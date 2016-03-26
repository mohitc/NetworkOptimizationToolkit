package com.network.topology.models.multilayerrouting;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.ConstraintGroups;
import com.network.topology.FixedConstants;
import com.network.topology.VarGroups;
import com.network.topology.capacity.vars.CapacityVarNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.fixedtopology.ModelTopologyExtractor;
import com.network.topology.models.mltopology.MultiLayerTopologyModel;
import com.network.topology.routing.constraints.*;
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
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MultiLayerRoutingTopologyModel extends MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(MultiLayerRoutingTopologyModel.class);

  public MultiLayerRoutingTopologyModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    //constant to indicate the max utilization of a link (Alpha)
    model.createLpConstant(FixedConstants.ALPHA, 0.7, constantGroup);

    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator knownTrafficMatConstNameGenerator = new KnownTrafficMatConstNameGenerator(vertexLabels);
    KnownTrafficMatConstGroupInitializer knownTrafficMatConstGroupInitializer = new KnownTrafficMatConstGroupInitializer(getVertexLabels(), _instance);
    model.createLPConstantGroup(ConstantGroups.TRAFFIC_MAT, ConstantGroups.TRAFFIC_MAT_DESC, knownTrafficMatConstNameGenerator,
        knownTrafficMatConstGroupInitializer);

  }

  public void initVarGroups() throws LPVarGroupException {

    super.initVarGroups();

    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator routingNameGenerator = new RoutingNameGenerator(vertexLabels);
    RoutingVarGroupInitializer routingVarGroupInitializer = new RoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.ROUTING, VarGroups.ROUTING_DESC, routingNameGenerator, routingVarGroupInitializer);

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

    SourceLoopAvoidanceConstrNameGenerator sourceLoopAvoidanceConstrNameGenerator = new SourceLoopAvoidanceConstrNameGenerator(vertexLabels);
    SourceLoopAvoidanceConstrGroupInitializer sourceLoopAvoidanceConstrGroupInitializer = new SourceLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.SOURCE_LOOP_AVOIDANCE, ConstraintGroups.SOURCE_LOOP_AVOIDANCE_DESC, sourceLoopAvoidanceConstrNameGenerator, sourceLoopAvoidanceConstrGroupInitializer);

    DestLoopAvoidanceConstrNameGenerator destLoopAvoidanceConstrNameGenerator = new DestLoopAvoidanceConstrNameGenerator(vertexLabels);
    DestLoopAvoidanceConstrGroupInitializer destLoopAvoidanceConstrGroupInitializer = new DestLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.DEST_LOOP_AVOIDANCE, ConstraintGroups.DEST_LOOP_AVOIDANCE_DESC, destLoopAvoidanceConstrNameGenerator, destLoopAvoidanceConstrGroupInitializer);

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

  public static void main(String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      MultiLayerRoutingTopologyModel lpModel = new MultiLayerRoutingTopologyModel("conf/circuit-cap.xml", manager);
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