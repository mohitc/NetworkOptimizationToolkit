package com.network.topology.models.fixedtopology;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPObjType;
//import com.lpapi.entities.gurobi.impl.CplexLPModel;
import com.lpapi.entities.gurobi.impl.GurobiLPModel;
import com.lpapi.exception.*;
import com.network.topology.VariableBoundConstants;
import com.network.topology.dyncircuits.constraints.DynCircuitBoundConstrNameGenerator;
import com.network.topology.dyncircuits.constraints.DynCircuitBountConstrGroupInitializer;
import com.network.topology.dyncircuits.vars.DynCircuitVarGroupInitializer;
import com.network.topology.dyncircuits.vars.DynCircuitVarNameGenerator;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarGroupInitializer;
import com.network.topology.forwarding.vars.ForwardingVarNameGenerator;
import com.network.topology.linkexists.constants.LinkExistsConstantGroupInitializer;
import com.network.topology.linkexists.constants.LinkExistsConstantNameGenerator;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrNameGenerator;
import com.network.topology.linkexists.constraints.LinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.LinkExistsConstrNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsVarGroupInitializer;
import com.network.topology.routing.constraints.*;
import com.network.topology.routing.routingcost.vars.RoutingCostVarGroupInitializer;
import com.network.topology.routing.routingcost.vars.RoutingCostVarNameGenerator;
import com.network.topology.routing.vars.RoutingNameGenerator;
import com.network.topology.routing.vars.RoutingVarGroupInitializer;
import com.topology.impl.primitives.TopologyManagerFactoryImpl;
import com.topology.primitives.*;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FixedTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(FixedTopologyModel.class);

  private LPModel model;

  private TopologyManager _instance;

  public TopologyManager initTopology() throws TopologyException {
    TopologyManagerFactory factory = new TopologyManagerFactoryImpl();
    TopologyManager manager = factory.createTopologyManager("Test");
    NetworkElement ne1 = manager.createNetworkElement();
    ne1.setLabel("1");
    ConnectionPoint cp1 = manager.createConnectionPoint(ne1);
    cp1.setLabel("1");
    NetworkElement ne2 = manager.createNetworkElement();
    ne2.setLabel("2");
    ConnectionPoint cp2 = manager.createConnectionPoint(ne2);
    cp2.setLabel("2");
    NetworkElement ne3 = manager.createNetworkElement();
    ne3.setLabel("3");
    ConnectionPoint cp3 = manager.createConnectionPoint(ne3);
    cp3.setLabel("3");
    NetworkElement ne4 = manager.createNetworkElement();
    ne4.setLabel("4");
    ConnectionPoint cp4 = manager.createConnectionPoint(ne4);
    cp4.setLabel("4");

    Link link12 = manager.createLink(cp1.getID(), cp2.getID());
    Link link23 = manager.createLink(cp2.getID(), cp3.getID());
    Link link34 = manager.createLink(cp3.getID(), cp4.getID());
    Link link41 = manager.createLink(cp4.getID(), cp1.getID());

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
    LPConstantGroup constantGroup = model.createLPConstantGroup(VariableBoundConstants.GROUP_NAME, VariableBoundConstants.GROUP_DESC);
    model.createLpConstant(VariableBoundConstants.ROUTING_COST_MAX, 1000, constantGroup);
    //constant to indicate the max number of dynamic circuits between a pair of nodes
    model.createLpConstant(VariableBoundConstants.DYN_CIRTUITS_MAX, 1, constantGroup);
    //constant to indicate the number of distinct dynamic circuit categories available
    model.createLpConstant(VariableBoundConstants.CIRCUIT_CLASSES, 2, constantGroup);

    Set<String> vertexLabels = getVertexLabels();

    LinkExistsConstantNameGenerator linkExistsConstantNameGenerator = new LinkExistsConstantNameGenerator(vertexLabels);
    LinkExistsConstantGroupInitializer linkExistsConstantGroupInitializer = new LinkExistsConstantGroupInitializer(_instance, linkExistsConstantNameGenerator, true);
    model.createLPConstantGroup("Hat(LinkExists)", "Constants to indicate if link existed in original topology", linkExistsConstantNameGenerator, linkExistsConstantGroupInitializer);
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
    LinkExistsNameGenerator linkExistsNameGenerator = new LinkExistsNameGenerator(vertexLabels);
    LinkExistsVarGroupInitializer linkExistsVarGroupInitializer = new LinkExistsVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("LinkExists", "Variables to indicate if link exists", linkExistsNameGenerator, linkExistsVarGroupInitializer);

    RoutingNameGenerator routingNameGenerator = new RoutingNameGenerator(vertexLabels);
    RoutingVarGroupInitializer routingVarGroupInitializer = new RoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("Routing", "Variables to indicate route", routingNameGenerator, routingVarGroupInitializer);

    ForwardingVarNameGenerator forwardingNameGenerator = new ForwardingVarNameGenerator(vertexLabels);
    ForwardingVarGroupInitializer forwardingGroupInitializer = new ForwardingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("Forwarding", "Variables to constrain forwarding", forwardingNameGenerator, forwardingGroupInitializer);

    RoutingCostVarNameGenerator rcVarNameGenerator = new RoutingCostVarNameGenerator(vertexLabels);
    RoutingCostVarGroupInitializer rcVarGroupInitializer = new RoutingCostVarGroupInitializer(vertexLabels);
    model.createLPVarGroup("RoutingCost", "Routing Cost variables", rcVarNameGenerator, rcVarGroupInitializer);

    try {
      int circuitClasses = (int) model.getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();
      DynCircuitVarNameGenerator dynCircuitVarNameGenerator = new DynCircuitVarNameGenerator(circuitClasses, vertexLabels);
      DynCircuitVarGroupInitializer dynCircuitVarGroupInitializer = new DynCircuitVarGroupInitializer(vertexLabels);
      model.createLPVarGroup("DynCircuits", "Dynamic circuits variables", dynCircuitVarNameGenerator, dynCircuitVarGroupInitializer);
    } catch (LPConstantException e) {
      log.error("Constant to indicate the number of dynamic circuit classes not defined");
    }
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
    LinkExistsNameGenerator linkExistsNameGenerator = new LinkExistsNameGenerator(vertexLabels);
    FixedLinkExistsConstrNameGenerator fixedLinkExistsConstrNameGenerator = new FixedLinkExistsConstrNameGenerator(vertexLabels);
    FixedLinkExistsConstrGroupInitializer fixedLinkExistsVarGroupInitializer = new FixedLinkExistsConstrGroupInitializer(_instance, linkExistsNameGenerator);
    model.createLPConstraintGroup("FixedLinkExistsConstr", "Constarint to restrict link exists to already existing links", fixedLinkExistsConstrNameGenerator, fixedLinkExistsVarGroupInitializer);


    //Link Exists constraints
    try {
      LinkExistsConstrNameGenerator lLinkExistsConstrNameGenerator = new LinkExistsConstrNameGenerator(vertexLabels);
      LinkExistsConstantNameGenerator linkExistsConstantNameGenerator = new LinkExistsConstantNameGenerator(vertexLabels);
      int circuitClasses = (int) model.getLPConstant(VariableBoundConstants.CIRCUIT_CLASSES).getValue();
      DynCircuitVarNameGenerator dynCircuitVarNameGenerator = new DynCircuitVarNameGenerator(circuitClasses, vertexLabels);
      LinkExistsConstrGroupInitializer linkExistsVarGroupInitializer = new LinkExistsConstrGroupInitializer(vertexLabels, linkExistsNameGenerator, linkExistsConstantNameGenerator, dynCircuitVarNameGenerator);
      model.createLPConstraintGroup("LinkExistsConstr", "Constarint to restrict link exists to already existing links or dynamic circuits", lLinkExistsConstrNameGenerator, linkExistsVarGroupInitializer);

      //Dynamic circuit bound constrants
      DynCircuitBoundConstrNameGenerator dynCircuitBoundConstrNameGenerator = new DynCircuitBoundConstrNameGenerator(vertexLabels);
      DynCircuitBountConstrGroupInitializer dynCircuitBountConstrGroupInitializer = new DynCircuitBountConstrGroupInitializer(vertexLabels, dynCircuitVarNameGenerator);
      model.createLPConstraintGroup("DynCircuitBound", "Constraints to bound the number of dynamic circuits", dynCircuitBoundConstrNameGenerator, dynCircuitBountConstrGroupInitializer);
    } catch (LPConstantException e) {
      log.error("Constant to indicate the number of dynamic circuit classes not defined");
    }


    //Routing Constraints
    RoutingNameGenerator routingNameGenerator = new RoutingNameGenerator(vertexLabels);
    RoutingConstrNameGenerator routingConstrNameGenerator = new RoutingConstrNameGenerator(vertexLabels);
    RoutingConstrGroupInitializer routingConstrGroupInitializer = new RoutingConstrGroupInitializer(vertexLabels, linkExistsNameGenerator, routingNameGenerator);
    model.createLPConstraintGroup("RoutingConstr", "Constraints on routing using existing links", routingConstrNameGenerator, routingConstrGroupInitializer);

    RoutingContinuityConstrNameGenerator routingContinuityConstrNameGenerator = new RoutingContinuityConstrNameGenerator(vertexLabels);
    RoutingContinuityConstrGroupInitializer routingContinuityConstrGroupInitializer = new RoutingContinuityConstrGroupInitializer(vertexLabels, routingNameGenerator);
    model.createLPConstraintGroup("RoutingContinuityConstr", "Constraints on single path routing continuity", routingContinuityConstrNameGenerator, routingContinuityConstrGroupInitializer);

    SymmetricRoutingConstrNameGenerator symmetricRoutingConstrNameGenerator = new SymmetricRoutingConstrNameGenerator(vertexLabels);
    SymmetricRoutingConstrGroupInitializer symmetricRoutingConstrGroupInitializer = new SymmetricRoutingConstrGroupInitializer(vertexLabels, routingNameGenerator);
    model.createLPConstraintGroup("SymmetricRouting", "Constraint to ensure symmetric routing between each s-d pair", symmetricRoutingConstrNameGenerator, symmetricRoutingConstrGroupInitializer);

    //Forwarding Constraints

    ForwardingVarNameGenerator forwardingNameGenerator = new ForwardingVarNameGenerator(vertexLabels);
    UniqueForwardingConstrNameGenerator uniqueForwardingConstrNameGenerator = new UniqueForwardingConstrNameGenerator(vertexLabels);
    UniqueForwardingConstrGroupInitializer uniqueForwardingConstrGroupInitializer = new UniqueForwardingConstrGroupInitializer(vertexLabels, forwardingNameGenerator);
    model.createLPConstraintGroup("UniqueForwarding", "Constraints to ensure a single forwarding entry for each destination", uniqueForwardingConstrNameGenerator, uniqueForwardingConstrGroupInitializer);

    ForwardingBasedRoutingConstrNameGenerator forwardingBasedRoutingConstrNameGenerator = new ForwardingBasedRoutingConstrNameGenerator(vertexLabels);
    ForwardingBasedRoutingConstrGroupInitializer forwardingBasedRoutingConstrGroupInitializer = new ForwardingBasedRoutingConstrGroupInitializer(vertexLabels, forwardingNameGenerator, routingNameGenerator);
    model.createLPConstraintGroup("ForwardingRouting", "Routing follows forwarding", forwardingBasedRoutingConstrNameGenerator, forwardingBasedRoutingConstrGroupInitializer);
  }

  public void initModel() throws LPModelException {
//    model = new CplexLPModel("Test");
    model = new GurobiLPModel("Test");
  }


  public static void main (String[] args) {
    try {

      FixedTopologyModel lpModel = new FixedTopologyModel();

      lpModel._instance = lpModel.initTopology();

      lpModel.initModel();
      lpModel.initConstants();
      lpModel.initVarGroups();
      lpModel.initConstraintGroups();
      LPExpression obj = new LPExpression(lpModel.model);
      obj.addTerm(1);
      lpModel.model.setObjFn(obj, LPObjType.MAXIMIZE);
      lpModel.model.init();
      lpModel.model.computeModel();
    } catch (LPModelException e) {
      log.error("Error initializing model", e);
    } catch (TopologyException e) {
      log.error("Error initializing topology", e);
    }
  }

}
