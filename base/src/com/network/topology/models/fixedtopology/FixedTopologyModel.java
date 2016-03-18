package com.network.topology.models.fixedtopology;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPObjType;
//import com.lpapi.entities.gurobi.impl.GurobiLPModel;
import com.lpapi.entities.glpk.impl.GlpkLPModel;
import com.lpapi.entities.skeleton.impl.SkeletonLPModel;
import com.lpapi.exception.*;
import com.network.topology.FixedConstants;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import com.network.topology.routing.validators.RoutingPathValidator;
import com.network.topology.routing.validators.SymmetricRoutingPathValidator;
import com.network.topology.traffic.knowntm.constants.KnownTrafficMatConstGroupInitializer;
import com.network.topology.capacity.constants.InitialCapacityConstGroupInitializer;
import com.network.topology.capacity.constraints.ActualCapacityGroupInitializer;
import com.network.topology.capacity.constraints.ActualCapacityNameGenerator;
import com.network.topology.traffic.knowntm.constraints.KnownTmTrafficConstrGroupInitializer;
import com.network.topology.traffic.knowntm.constraints.KnownTmTrafficConstrNameGenerator;
import com.network.topology.capacity.vars.CapacityVarGroupInitializer;
import com.network.topology.dyncircuits.constraints.DynCircuitBoundConstrNameGenerator;
import com.network.topology.dyncircuits.constraints.DynCircuitBoundConstrGroupInitializer;
import com.network.topology.dyncircuits.constraints.SymDynCirConstrGroupInitializer;
import com.network.topology.dyncircuits.constraints.SymDynCirConstrNameGenerator;
import com.network.topology.dyncircuits.parser.DynCircuitClassParser;
import com.network.topology.dyncircuits.vars.DynCircuitVarGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarGroupInitializer;
import com.network.topology.linkexists.constants.LinkExistsConstantGroupInitializer;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrNameGenerator;
import com.network.topology.linkexists.constraints.LinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.LinkExistsConstrNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsVarGroupInitializer;
import com.network.topology.linkweight.constants.LinkWeightConstantGroupInitializer;
import com.network.topology.linkweight.constraints.LinkWeightConstrGroupInitializer;
import com.network.topology.linkweight.constraints.LinkWeightConstrNameGenerator;
import com.network.topology.linkweight.vars.LinkWeightVarGroupInitializer;
import com.network.topology.objfn.MinDynCirCostObjFnGenerator;
import com.network.topology.routing.constraints.*;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.vars.RoutingCostVarGroupInitializer;
import com.network.topology.routing.vars.RoutingVarGroupInitializer;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerFactoryImpl;
import com.topology.primitives.*;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class FixedTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(FixedTopologyModel.class);

  private LPModel model;

  public TopologyManager _instance;

  private FixedTopologyModelNameFactory factory;

  private DynCircuitClassParser dynCircuitParser;

  private List<ModelValidator> validatorList;

  public TopologyManager initTopology() throws TopologyException {
    TopologyManagerFactory factory = new TopologyManagerFactoryImpl();
    TopologyManager manager = factory.createTopologyManager("Test");
//    NetworkElement ne1 = manager.createNetworkElement();
//    ne1.setLabel("1");
//    ConnectionPoint cp1 = manager.createConnectionPoint(ne1);
//    cp1.setLabel("1");
//    NetworkElement ne2 = manager.createNetworkElement();
//    ne2.setLabel("2");
//    ConnectionPoint cp2 = manager.createConnectionPoint(ne2);
//    cp2.setLabel("2");
//    NetworkElement ne3 = manager.createNetworkElement();
//    ne3.setLabel("3");
//    ConnectionPoint cp3 = manager.createConnectionPoint(ne3);
//    cp3.setLabel("3");
//    NetworkElement ne4 = manager.createNetworkElement();
//    ne4.setLabel("4");
//    ConnectionPoint cp4 = manager.createConnectionPoint(ne4);
//    cp4.setLabel("4");
//
//    Link link12 = manager.createLink(cp1.getID(), cp2.getID());
//    Link link23 = manager.createLink(cp2.getID(), cp3.getID());
//    Link link34 = manager.createLink(cp3.getID(), cp4.getID());
//    Link link41 = manager.createLink(cp4.getID(), cp1.getID());

    return manager;
  }

  private Set<String> getVertexLabels() {
    if (_instance==null)
      return Collections.EMPTY_SET;
    Set<String> vertexLabels = new HashSet<>();
    Set<ConnectionPoint> cps = _instance.getAllElements(ConnectionPoint.class);
    for (ConnectionPoint cp: cps) {
      vertexLabels.add(cp.getLabel());
    }
    return Collections.unmodifiableSet(vertexLabels);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    if (model==null) {
      log.error("LPModel provided is not initialized, skipping var group generation");
      return;
    }

    LPConstantGroup constantGroup = model.createLPConstantGroup(FixedConstants.GROUP_NAME, FixedConstants.GROUP_DESC);
    model.createLpConstant(FixedConstants.ROUTING_COST_MAX, 1000, constantGroup);
    //constant to indicate the max number of dynamic circuits between a pair of nodes
    model.createLpConstant(FixedConstants.DYN_CIRTUITS_MAX, 1, constantGroup);
    //constant to indicate the number of distinct dynamic circuit categories available
    model.createLpConstant(FixedConstants.CIRCUIT_CLASSES, dynCircuitParser.getResult().keySet().size(), constantGroup);
    //constant to indicate the max capacity C(inf) for a link between a pair of nodes
    model.createLpConstant(FixedConstants.CAP_MAX, 100000, constantGroup);
    //constant to indicate the max capacity C(inf) for a link between a pair of nodes
    model.createLpConstant(FixedConstants.W_INF, 100000, constantGroup);
    //constant to indicate the max utilization of a link (Alpha)
    model.createLpConstant(FixedConstants.ALPHA, 0.7, constantGroup);

    LinkExistsConstantGroupInitializer linkExistsConstantGroupInitializer = new LinkExistsConstantGroupInitializer(_instance, factory.getLinkExistsConstantNameGenerator(), true);
    model.createLPConstantGroup("Hat(LinkExists)", "Constants to indicate if link existed in original topology", factory.getLinkExistsConstantNameGenerator(),
      linkExistsConstantGroupInitializer);

    LinkWeightConstantGroupInitializer linkWeightConstantGroupInitializer = new LinkWeightConstantGroupInitializer(getVertexLabels());
    model.createLPConstantGroup("Hat(W)", "Constants to indicate weight of link if exists", factory.getLinkWeightConstantNameGenerator(),
        linkWeightConstantGroupInitializer);

    KnownTrafficMatConstGroupInitializer knownTrafficMatConstGroupInitializer = new KnownTrafficMatConstGroupInitializer(getVertexLabels(),_instance);
    model.createLPConstantGroup("lambda", "Constants to indicate requested capacity between two nodes", factory.getKnownTrafficMatConstNameGenerator(),
      knownTrafficMatConstGroupInitializer);

    InitialCapacityConstGroupInitializer initialCapacityConstGroupInitializer = new InitialCapacityConstGroupInitializer(getVertexLabels(),_instance);
    model.createLPConstantGroup("HAT(C)", "Constants to store the initial capacity between each node pair",
            factory.getInitialCapacityConstNameGenerator(), initialCapacityConstGroupInitializer);

  }

  public void initVarGroups() throws LPVarGroupException {
    if (model==null) {
      log.error("LPModel provided is not initialized, skipping var group generation");
      return;
    }
    if (_instance==null) {
      log.error("TopologyManager not initialized, skipping var group generation");
    }

    Set<String> vertexLabels = getVertexLabels();

    //Initialize link exists variable group
    LinkExistsVarGroupInitializer linkExistsVarGroupInitializer = new LinkExistsVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("LinkExists", "Variables to indicate if link exists", factory.getLinkExistsNameGenerator(), linkExistsVarGroupInitializer);

    RoutingVarGroupInitializer routingVarGroupInitializer = new RoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("Routing", "Variables to indicate route", factory.getRoutingNameGenerator(), routingVarGroupInitializer);

    ForwardingVarGroupInitializer forwardingGroupInitializer = new ForwardingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("Forwarding", "Variables to constrain forwarding", factory.getForwardingNameGenerator(), forwardingGroupInitializer);

    RoutingCostVarGroupInitializer rcVarGroupInitializer = new RoutingCostVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("RoutingCost", "Routing Cost variables", factory.getRoutingCostNameGenerator(), rcVarGroupInitializer);

    CapacityVarGroupInitializer capacityVarGroupInitializer = new CapacityVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("LinkCapacity", "Variables to indicate link capacity", factory.getCapacityVarNameGenerator(), capacityVarGroupInitializer);

    LinkWeightVarGroupInitializer linkWeightVarGroupInitializer = new LinkWeightVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("LinkWeight", "Variables to indicate link weights", factory.getLinkWeightVarNameGenerator(), linkWeightVarGroupInitializer);

    DynCircuitVarGroupInitializer dynCircuitVarGroupInitializer = new DynCircuitVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("DynCircuits", "Dynamic circuits variables", factory.getDynamicCircuitNameGenerator(), dynCircuitVarGroupInitializer);
  }

  public void initConstraintGroups() throws LPConstraintGroupException {
    if (model==null) {
      log.error("LPModel provided is not initialized, skipping constraint group generation");
      return;
    }
    if (_instance==null) {
      log.error("TopologyManager not initialized, skipping constraint group generation");
    }


    //Fixed Link Exists constraints
    Set<String> vertexLabels = getVertexLabels();
    FixedLinkExistsConstrNameGenerator fixedLinkExistsConstrNameGenerator = new FixedLinkExistsConstrNameGenerator(vertexLabels);
    FixedLinkExistsConstrGroupInitializer fixedLinkExistsVarGroupInitializer = new FixedLinkExistsConstrGroupInitializer(_instance, factory.getLinkExistsNameGenerator());
    model.createLPConstraintGroup("FixedLinkExistsConstr", "Constarint to restrict link exists to already existing links", fixedLinkExistsConstrNameGenerator, fixedLinkExistsVarGroupInitializer);


    //Link Exists constraints
    try {
      LinkExistsConstrNameGenerator lLinkExistsConstrNameGenerator = new LinkExistsConstrNameGenerator(vertexLabels);
      int circuitClasses = (int) model.getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();
      LinkExistsConstrGroupInitializer linkExistsVarGroupInitializer = new LinkExistsConstrGroupInitializer(vertexLabels, factory.getLinkExistsNameGenerator(), factory.getLinkExistsConstantNameGenerator(), factory.getDynamicCircuitNameGenerator());
      model.createLPConstraintGroup("LinkExistsConstr", "Constarint to restrict link exists to already existing links or dynamic circuits", lLinkExistsConstrNameGenerator, linkExistsVarGroupInitializer);

      //Dynamic circuit bound constrants
      DynCircuitBoundConstrNameGenerator dynCircuitBoundConstrNameGenerator = new DynCircuitBoundConstrNameGenerator(vertexLabels);
      DynCircuitBoundConstrGroupInitializer dynCircuitBoundConstrGroupInitializer = new DynCircuitBoundConstrGroupInitializer(vertexLabels, factory.getDynamicCircuitNameGenerator());
      model.createLPConstraintGroup("DynCircuitBound", "Constraints to bound the number of dynamic circuits", dynCircuitBoundConstrNameGenerator, dynCircuitBoundConstrGroupInitializer);

      //Symmetric dynamic circuit constraint
      SymDynCirConstrNameGenerator symDynCirConstrNameGenerator = new SymDynCirConstrNameGenerator(circuitClasses, vertexLabels);
      SymDynCirConstrGroupInitializer symDynCirConstrGroupInitializer = new SymDynCirConstrGroupInitializer(vertexLabels, factory.getDynamicCircuitNameGenerator());
      model.createLPConstraintGroup("SymDynCirConstr", "Constraints to symmetric dynamic circuits", symDynCirConstrNameGenerator, symDynCirConstrGroupInitializer);

      //Capacity constraints
      ActualCapacityNameGenerator actualCapacityNameGenerator =
              new ActualCapacityNameGenerator(vertexLabels);
      ActualCapacityGroupInitializer actualCapacityGroupInitializer =
              new ActualCapacityGroupInitializer(
                      vertexLabels,
                      factory.getCapacityVarNameGenerator(),
                      factory.getInitialCapacityConstNameGenerator(),
                      factory.getDynamicCircuitNameGenerator(),
                dynCircuitParser.getResult()
              );
      model.createLPConstraintGroup("ActualCapacityConstr",
              "Constraints to instantiated capacity equal to initial plus dynaimc circuits",
              actualCapacityNameGenerator, actualCapacityGroupInitializer);
      KnownTmTrafficConstrNameGenerator knownTmTrafficConstrNameGenerator =
              new KnownTmTrafficConstrNameGenerator(vertexLabels);
      KnownTmTrafficConstrGroupInitializer knownTmTrafficConstrGroupInitializer =
              new KnownTmTrafficConstrGroupInitializer(
                      vertexLabels,
                      factory.getCapacityVarNameGenerator(),
                      factory.getKnownTrafficMatConstNameGenerator(),
                      factory.getRoutingNameGenerator()
              );
      model.createLPConstraintGroup("ActualDemandedCapacityConstr",
              "Constrains instantiated capacity to be at least as big as requested",
        knownTmTrafficConstrNameGenerator, knownTmTrafficConstrGroupInitializer);


    } catch (LPConstantException e) {
      log.error("Constant to indicate the number of dynamic circuit classes not defined");
    }


    //Routing Constraints
    RoutingConstrNameGenerator routingConstrNameGenerator = new RoutingConstrNameGenerator(vertexLabels);
    RoutingConstrGroupInitializer routingConstrGroupInitializer = new RoutingConstrGroupInitializer(vertexLabels, factory.getLinkExistsNameGenerator(), factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("RoutingConstr", "Constraints on routing using existing links", routingConstrNameGenerator, routingConstrGroupInitializer);

    RoutingContinuityConstrNameGenerator routingContinuityConstrNameGenerator = new RoutingContinuityConstrNameGenerator(vertexLabels);
    RoutingContinuityConstrGroupInitializer routingContinuityConstrGroupInitializer = new RoutingContinuityConstrGroupInitializer(vertexLabels, factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("RoutingContinuityConstr", "Constraints on single path routing continuity", routingContinuityConstrNameGenerator, routingContinuityConstrGroupInitializer);

    SymmetricRoutingConstrNameGenerator symmetricRoutingConstrNameGenerator = new SymmetricRoutingConstrNameGenerator(vertexLabels);
    SymmetricRoutingConstrGroupInitializer symmetricRoutingConstrGroupInitializer = new SymmetricRoutingConstrGroupInitializer(vertexLabels, factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("SymmetricRouting", "Constraint to ensure symmetric routing between each s-d pair", symmetricRoutingConstrNameGenerator, symmetricRoutingConstrGroupInitializer);

    RoutingCostConstrNameGenerator routingCostConstrNameGenerator = new RoutingCostConstrNameGenerator(vertexLabels);
    RoutingCostConstrGroupInitializer routingCostConstrGroupInitializer = new RoutingCostConstrGroupInitializer(vertexLabels, factory.getRoutingCostNameGenerator(),
      factory.getRoutingNameGenerator(), factory.getLinkWeightConstantNameGenerator());
    model.createLPConstraintGroup("RoutingCost", "Constraint to calculate routing cost based on route", routingCostConstrNameGenerator, routingCostConstrGroupInitializer);

    MinRoutingCostConstrNameGenerator minRoutingCostConstrNameGenerator = new MinRoutingCostConstrNameGenerator(vertexLabels);
    MinRoutingCostConstrGroupInitializer minRoutingCostConstrGroupInitializer = new MinRoutingCostConstrGroupInitializer(vertexLabels, factory.getRoutingCostNameGenerator(), factory.getLinkWeightVarNameGenerator());
    model.createLPConstraintGroup("MinRoutingCost", "Constraint to ensure that routing cost is minimized", minRoutingCostConstrNameGenerator, minRoutingCostConstrGroupInitializer);

    SourceLoopAvoidanceConstrNameGenerator sourceLoopAvoidanceConstrNameGenerator = new SourceLoopAvoidanceConstrNameGenerator(vertexLabels);
    SourceLoopAvoidanceConstrGroupInitializer sourceLoopAvoidanceConstrGroupInitializer = new SourceLoopAvoidanceConstrGroupInitializer(vertexLabels, factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("SourceLoopAvoidance", "Constraint to ensure loop avoidance at source", sourceLoopAvoidanceConstrNameGenerator, sourceLoopAvoidanceConstrGroupInitializer);

    DestLoopAvoidanceConstrNameGenerator destLoopAvoidanceConstrNameGenerator = new DestLoopAvoidanceConstrNameGenerator(vertexLabels);
    DestLoopAvoidanceConstrGroupInitializer destLoopAvoidanceConstrGroupInitializer = new DestLoopAvoidanceConstrGroupInitializer(vertexLabels, factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("DestLoopAvoidance", "Constraint to ensyre loop avoidance at destination", destLoopAvoidanceConstrNameGenerator, destLoopAvoidanceConstrGroupInitializer);
    //Forwarding Constraints

    UniqueForwardingConstrNameGenerator uniqueForwardingConstrNameGenerator = new UniqueForwardingConstrNameGenerator(vertexLabels);
    UniqueForwardingConstrGroupInitializer uniqueForwardingConstrGroupInitializer = new UniqueForwardingConstrGroupInitializer(vertexLabels, factory.getForwardingNameGenerator());
    model.createLPConstraintGroup("UniqueForwarding", "Constraints to ensure a single forwarding entry for each destination", uniqueForwardingConstrNameGenerator, uniqueForwardingConstrGroupInitializer);

    ForwardingBasedRoutingConstrNameGenerator forwardingBasedRoutingConstrNameGenerator = new ForwardingBasedRoutingConstrNameGenerator(vertexLabels);
    ForwardingBasedRoutingConstrGroupInitializer forwardingBasedRoutingConstrGroupInitializer = new ForwardingBasedRoutingConstrGroupInitializer(vertexLabels, factory.getForwardingNameGenerator(), factory.getRoutingNameGenerator());
    model.createLPConstraintGroup("ForwardingRouting", "Routing follows forwarding", forwardingBasedRoutingConstrNameGenerator, forwardingBasedRoutingConstrGroupInitializer);

    //Link weight constraint
    LinkWeightConstrNameGenerator linkWeightConstrNameGenerator = new LinkWeightConstrNameGenerator(vertexLabels);
    LinkWeightConstrGroupInitializer linkWeightConstrGroupInitializer = new LinkWeightConstrGroupInitializer(vertexLabels, factory.getLinkWeightVarNameGenerator(),
      factory.getLinkExistsNameGenerator(), factory.getLinkWeightConstantNameGenerator());
    model.createLPConstraintGroup("LinkWeightConstr", "Constraints to define link weight", linkWeightConstrNameGenerator, linkWeightConstrGroupInitializer);
  }

  public void initModel() throws LPModelException {
//  model = new CplexLPModel("Test");
//  model = new GurobiLPModel("Test");
//    model = new SkeletonLPModel("Test");
    model = new GlpkLPModel("Test");
  }


  public void initModelValidators() {
    validatorList = new ArrayList<>();
    RoutingPathValidator routingPathValidator = new RoutingPathValidator(model, getVertexLabels(), factory.getRoutingNameGenerator());
    validatorList.add(routingPathValidator);
    validatorList.add(new SymmetricRoutingPathValidator(model, getVertexLabels(), routingPathValidator));
  }

  public void runModelValidators() throws ModelValidationException {
    for (ModelValidator validator: validatorList) {
      validator.validate();
    }
  }

  public static void main (String[] args) {
    try {

      FixedTopologyModel lpModel = new FixedTopologyModel();

      lpModel._instance = lpModel.initTopology();
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", lpModel._instance);



      lpModel.dynCircuitParser = new DynCircuitClassParser("conf/circuit-cap.xml");
      lpModel.factory = new FixedTopologyModelNameFactory(lpModel.getVertexLabels(), lpModel.dynCircuitParser.getResult().keySet().size());

      int circuitClasses = lpModel.dynCircuitParser.getResult().keySet().size();
      lpModel.factory = new FixedTopologyModelNameFactory(lpModel.getVertexLabels(), circuitClasses);

      lpModel.initModel();
      lpModel.initConstants();
      lpModel.initVarGroups();
      lpModel.initConstraintGroups();


      //Initialize Objective function
      lpModel.model.attachObjectiveFunctionGenerator(new MinDynCirCostObjFnGenerator(lpModel.getVertexLabels(),
        lpModel.dynCircuitParser.getResult(), lpModel.factory.getDynamicCircuitNameGenerator()));
//      obj.addTerm(1);
//      lpModel.model.setObjFn(obj, LPObjType.MAXIMIZE);
      lpModel.model.init();
      lpModel.model.initObjectiveFunction();
      lpModel.model.computeModel();
      log.info("Objective:" + lpModel.model.getObjectiveValue());

      lpModel.initModelValidators();
      lpModel.runModelValidators();
    } catch (LPModelException e) {
      log.error("Error initializing model", e);
    } catch (TopologyException e) {
      log.error("Error initializing topology", e);
    } catch (FileFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      log.error("Error initializing model file", e);
    } catch (ModelValidationException e) {
      log.error("Error while validating model", e);
    }
  }

}
