package com.network.topology.models.delayconstrainedmlspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.ConstraintGroups;
import com.network.topology.FixedConstants;
import com.network.topology.VarGroups;
import com.network.topology.models.multilayerspf.MultiLayerSpfTopologyModel;
import com.network.topology.routing.delaybound.constants.*;
import com.network.topology.routing.delaybound.constraints.RouteDelayConstrGroupInitializer;
import com.network.topology.routing.delaybound.constraints.RouteDelayConstrNameGenerator;
import com.network.topology.routing.delaybound.constraints.RouterInPathConstrGroupInitializer;
import com.network.topology.routing.delaybound.constraints.RouterInPathConstrNameGenerator;
import com.network.topology.routing.delaybound.validators.RouterInPathValidator;
import com.network.topology.routing.delaybound.vars.RouterInPathVarGroupInitializer;
import com.network.topology.routing.delaybound.vars.RouterInPathVarNameGenerator;
import com.network.topology.routing.validators.RoutingPathValidator;
import com.network.topology.routing.validators.SymmetricRoutingPathValidator;
import com.network.topology.routing.vars.RoutingNameGenerator;
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

public class DelayConstrainedMLSpfTopologyModel extends MultiLayerSpfTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(DelayConstrainedMLSpfTopologyModel.class);

  public DelayConstrainedMLSpfTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName) {
    super(circuitConfFile, manager, instanceName);
  }

  public DelayConstrainedMLSpfTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName, String exportPath) {
    super(circuitConfFile, manager, instanceName, exportPath);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {

    super.initConstants();

    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    model.createLpConstant(FixedConstants.ROUTE_DELAY_INF, 100000, constantGroup);
    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator linkDelayConstantNameGenerator = new LinkDelayConstNameGenerator(vertexLabels);
    LinkDelayConstGroupInitializer linkDelayConstGroupInitializer = new LinkDelayConstGroupInitializer(vertexLabels, _instance);
    model.createLPConstantGroup(ConstantGroups.LINK_DELAY, ConstantGroups.LINK_DELAY_DESC, linkDelayConstantNameGenerator, linkDelayConstGroupInitializer);

    LPNameGenerator routerDelayConstantNameGenerator = new RouterDelayConstNameGenerator(vertexLabels);
    RouterDelayConstGroupInitializer routerDelayConstGroupInitializer = new RouterDelayConstGroupInitializer(getVertexLabels(), _instance);
    model.createLPConstantGroup(ConstantGroups.ROUTER_DELAY, ConstantGroups.ROUTER_DELAY_DESC, routerDelayConstantNameGenerator, routerDelayConstGroupInitializer);

    LPNameGenerator routePathDelayConstantNameGenerator = new RoutePathDelayConstNameGenerator(vertexLabels);
    RoutePathDelayConstGroupInitializer routePathDelayConstGroupInitializer = new RoutePathDelayConstGroupInitializer(getVertexLabels());
    model.createLPConstantGroup(ConstantGroups.PATH_DELAY, ConstantGroups.PATH_DELAY_DESC, routePathDelayConstantNameGenerator, routePathDelayConstGroupInitializer);
  }

  public void initVarGroups() throws LPVarGroupException {

    super.initVarGroups();
    Set<String> vertexLabels = getVertexLabels();

    LPNameGenerator routerInPathVarNameGenerator = new RouterInPathVarNameGenerator(vertexLabels);
    RouterInPathVarGroupInitializer routerInPathVarGroupInitializer = new RouterInPathVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.ROUTER_IN_PATH, VarGroups.ROUTER_IN_PATH_DESC, routerInPathVarNameGenerator, routerInPathVarGroupInitializer);

  }

  public void initConstraintGroups() throws LPConstraintGroupException {

    super.initConstraintGroups();
    Set<String> vertexLabels = getVertexLabels();

    //Delay Constraints
    RouterInPathConstrNameGenerator routerInPathConstrNameGenerator = new RouterInPathConstrNameGenerator(vertexLabels);
    RouterInPathConstrGroupInitializer routerInPathConstrGroupInitializer = new RouterInPathConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.ROUTER_IN_PATH, ConstraintGroups.ROUTER_IN_PATH_DESC, routerInPathConstrNameGenerator, routerInPathConstrGroupInitializer);

    RouteDelayConstrNameGenerator routeDelayConstrNameGenerator = new RouteDelayConstrNameGenerator(vertexLabels);
    RouteDelayConstrGroupInitializer routeDelayConstrGroupInitializer = new RouteDelayConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(ConstraintGroups.ROUTE_DELAY, ConstraintGroups.ROUTE_DELAY_DESC, routeDelayConstrNameGenerator, routeDelayConstrGroupInitializer);
  }

  public void initModelValidators() {
    validatorList = new ArrayList<>();
    Set<String> vertexLabels = getVertexLabels();
    RoutingPathValidator routingPathValidator = new RoutingPathValidator(model, vertexLabels, new RoutingNameGenerator(vertexLabels));
    validatorList.add(routingPathValidator);
    validatorList.add(new SymmetricRoutingPathValidator(model, vertexLabels, routingPathValidator));
    validatorList.add(new RouterInPathValidator(model, getVertexLabels(), routingPathValidator, new RouterInPathVarNameGenerator(vertexLabels)));
  }

  public static void main (String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      MultiLayerSpfTopologyModel lpModel = new MultiLayerSpfTopologyModel("conf/circuit-cap.xml", manager, "TestABC");
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
