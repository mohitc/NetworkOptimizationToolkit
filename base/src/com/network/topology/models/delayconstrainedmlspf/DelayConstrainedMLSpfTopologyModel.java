package com.network.topology.models.delayconstrainedmlspf;

import com.lpapi.exception.*;
import com.network.topology.models.multilayerspf.MultiLayerSpfTopologyModel;
import com.network.topology.routing.delaybound.constants.LinkDelayConstGroupInitializer;
import com.network.topology.routing.delaybound.constants.RoutePathDelayConstGroupInitializer;
import com.network.topology.routing.delaybound.constants.RouterDelayConstGroupInitializer;
import com.network.topology.routing.delaybound.constraints.RouteDelayConstrGroupInitializer;
import com.network.topology.routing.delaybound.constraints.RouteDelayConstrNameGenerator;
import com.network.topology.routing.delaybound.constraints.RouterInPathConstrGroupInitializer;
import com.network.topology.routing.delaybound.constraints.RouterInPathConstrNameGenerator;
import com.network.topology.routing.delaybound.vars.RouterInPathVarGroupInitializer;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DelayConstrainedMLSpfTopologyModel extends MultiLayerSpfTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(DelayConstrainedMLSpfTopologyModel.class);

  public DelayConstrainedMLSpfTopologyModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {

    super.initConstants();

    LinkDelayConstGroupInitializer linkDelayConstGroupInitializer = new LinkDelayConstGroupInitializer(getVertexLabels(), _instance);
    model.createLPConstantGroup("D(L)", "Constants to store the link delays", factory.getLinkDelayConstantNameGenerator(), linkDelayConstGroupInitializer);

    RouterDelayConstGroupInitializer routerDelayConstGroupInitializer = new RouterDelayConstGroupInitializer(getVertexLabels(), _instance);
    model.createLPConstantGroup("D(R)", "Constants to store delay of routers", factory.getRouterDelayConstantNameGenerator(), routerDelayConstGroupInitializer);

    RoutePathDelayConstGroupInitializer routePathDelayConstGroupInitializer = new RoutePathDelayConstGroupInitializer(getVertexLabels());
    model.createLPConstantGroup("D(Path)", "Constants to store max delay on path", factory.getRoutePathDelayConstantNameGenerator(), routePathDelayConstGroupInitializer);
  }

  public void initVarGroups() throws LPVarGroupException {

    super.initVarGroups();

    RouterInPathVarGroupInitializer routerInPathVarGroupInitializer = new RouterInPathVarGroupInitializer(getVertexLabels());
    model.createLPVarGroup("RouterInPath", "Variable to indicate if router is in path", factory.getRouterInPathVarNameGenerator(), routerInPathVarGroupInitializer);

  }

  public void initConstraintGroups() throws LPConstraintGroupException {

    super.initConstraintGroups();

    //Delay Constraints
    RouterInPathConstrNameGenerator routerInPathConstrNameGenerator = new RouterInPathConstrNameGenerator(getVertexLabels());
    RouterInPathConstrGroupInitializer routerInPathConstrGroupInitializer = new RouterInPathConstrGroupInitializer(getVertexLabels(), factory.getRoutingNameGenerator(),
      factory.getRouterInPathVarNameGenerator());
    model.createLPConstraintGroup("RouterInPathConstr", "Constraint to identify is router is in path", routerInPathConstrNameGenerator, routerInPathConstrGroupInitializer);

    RouteDelayConstrNameGenerator routeDelayConstrNameGenerator = new RouteDelayConstrNameGenerator(getVertexLabels());
    RouteDelayConstrGroupInitializer routeDelayConstrGroupInitializer = new RouteDelayConstrGroupInitializer(getVertexLabels(), factory.getRoutingNameGenerator(),
      factory.getRouterInPathVarNameGenerator(), factory.getLinkDelayConstantNameGenerator(), factory.getRouterDelayConstantNameGenerator(),
      factory.getRoutePathDelayConstantNameGenerator());
    model.createLPConstraintGroup("RouteDelay", "Constraint to satisfy routing delay constraints", routeDelayConstrNameGenerator, routeDelayConstrGroupInitializer);
  }

  public static void main (String[] args) {
    try {

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      MultiLayerSpfTopologyModel lpModel = new MultiLayerSpfTopologyModel("conf/nobel-us.xml", manager);
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
