package com.network.topology.models.multilayerspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.ConstraintGroups;
import com.network.topology.FixedConstants;
import com.network.topology.VarGroups;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.ForwardingBasedRoutingConstrNameGenerator;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrGroupInitializer;
import com.network.topology.forwarding.constraints.UniqueForwardingConstrNameGenerator;
import com.network.topology.forwarding.vars.ForwardingVarGroupInitializer;
import com.network.topology.forwarding.vars.ForwardingVarNameGenerator;
import com.network.topology.linkweight.constants.LinkWeightConstantGroupInitializer;
import com.network.topology.linkweight.constants.LinkWeightConstantNameGenerator;
import com.network.topology.linkweight.constraints.LinkWeightConstrGroupInitializer;
import com.network.topology.linkweight.constraints.LinkWeightConstrNameGenerator;
import com.network.topology.linkweight.vars.LinkWeightVarGroupInitializer;
import com.network.topology.linkweight.vars.LinkWeightVarNameGenerator;
import com.network.topology.models.multilayerrouting.MultiLayerRoutingTopologyModel;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.MinRoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrGroupInitializer;
import com.network.topology.routing.routingcost.constraints.RoutingCostConstrNameGenerator;
import com.network.topology.routing.routingcost.vars.RoutingCostVarGroupInitializer;
import com.network.topology.routing.routingcost.vars.RoutingCostVarNameGenerator;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class MultiLayerSpfTopologyModel extends MultiLayerRoutingTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(MultiLayerSpfTopologyModel.class);

  public MultiLayerSpfTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName) {
    super(circuitConfFile, manager, instanceName);
  }

  public MultiLayerSpfTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName, String exportPath) {
    super(circuitConfFile, manager, instanceName);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    model.createLpConstant(FixedConstants.ROUTING_COST_MAX, 1000, constantGroup);
    //constant to indicate the max capacity C(inf) for a link between a pair of nodes
    model.createLpConstant(FixedConstants.W_INF, 100000, constantGroup);

    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator linkWeightConstantNameGenerator = new LinkWeightConstantNameGenerator(vertexLabels);
    LinkWeightConstantGroupInitializer linkWeightConstantGroupInitializer = new LinkWeightConstantGroupInitializer(vertexLabels);
    model.createLPConstantGroup(ConstantGroups.LINK_WEIGHT, ConstantGroups.LINK_WEIGHT_DESC, linkWeightConstantNameGenerator,
        linkWeightConstantGroupInitializer);

  }

  public void initVarGroups() throws LPVarGroupException {

    super.initVarGroups();

    Set<String> vertexLabels = getVertexLabels();

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

    //Routing Constraints
    RoutingCostConstrNameGenerator routingCostConstrNameGenerator = new RoutingCostConstrNameGenerator(vertexLabels);
    RoutingCostConstrGroupInitializer routingCostConstrGroupInitializer = new RoutingCostConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.ROUTING_COST, ConstraintGroups.ROUTING_COST_DESC, routingCostConstrNameGenerator, routingCostConstrGroupInitializer);

    MinRoutingCostConstrNameGenerator minRoutingCostConstrNameGenerator = new MinRoutingCostConstrNameGenerator(vertexLabels);
    MinRoutingCostConstrGroupInitializer minRoutingCostConstrGroupInitializer = new MinRoutingCostConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.MIN_ROUTING_COST, ConstraintGroups.MIN_ROUTING_COST_DESC, minRoutingCostConstrNameGenerator, minRoutingCostConstrGroupInitializer);

    //Forwarding constraints
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

  public static void main (String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      MultiLayerSpfTopologyModel lpModel = new MultiLayerSpfTopologyModel("conf/circuit-cap.xml", manager, "testABC");
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