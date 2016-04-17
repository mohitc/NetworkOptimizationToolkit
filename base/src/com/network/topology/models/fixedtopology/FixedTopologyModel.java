package com.network.topology.models.fixedtopology;

import com.lpapi.exception.LPConstraintGroupException;
import com.lpapi.exception.LPModelException;
import com.network.topology.ConstraintGroups;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.FixedLinkExistsConstrNameGenerator;
import com.network.topology.linkexists.validators.FixedLinkExistsValidator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.models.multilayerspf.MultiLayerSpfTopologyModel;
import com.topology.impl.importers.sndlib.SNDLibImportTopology;
import com.topology.impl.primitives.TopologyManagerImpl;
import com.topology.primitives.*;
import com.topology.primitives.exception.FileFormatException;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FixedTopologyModel extends MultiLayerSpfTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(FixedTopologyModel.class);

  public FixedTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName) {
    super(circuitConfFile, manager, instanceName);
  }

  public FixedTopologyModel(String circuitConfFile, TopologyManager manager, String instanceName, String exportPath) {
    super(circuitConfFile, manager, instanceName, exportPath);
  }

  public void initConstraintGroups() throws LPConstraintGroupException {

    super.initConstraintGroups();

    //Fixed Link Exists constraints
    FixedLinkExistsConstrNameGenerator fixedLinkExistsConstrNameGenerator = new FixedLinkExistsConstrNameGenerator(getVertexLabels());
    FixedLinkExistsConstrGroupInitializer fixedLinkExistsVarGroupInitializer = new FixedLinkExistsConstrGroupInitializer(_instance);
    model.createLPConstraintGroup(ConstraintGroups.FIXED_LINK_EXISTS, ConstraintGroups.FIXED_LINK_EXISTS_DESC, fixedLinkExistsConstrNameGenerator, fixedLinkExistsVarGroupInitializer);

  }

  public void initModelValidators() {
    super.initModelValidators();
    validatorList.add(new FixedLinkExistsValidator(model, _instance, new LinkExistsNameGenerator(getVertexLabels()), getVertexLabels()));
  }

  public static void main (String[] args) {
    try {

      //arg 1 = file name for topology
      //arg 2 = file name for circuit capacity
      //arg 3 = path to export folder
      //arg 4 = identifier for the model
      //arg 5 = identify if we should run the model or read from file
      if (args==null || args.length!=5) {
        log.error("Invalid arguments provided to program. Expected {path to topology} {path to circuit capacity configuration} {path to export folder} {model identifier} {boolean to indicate if model shold be solved}");
      }

      TopologyManager manager = new TopologyManagerImpl(args[3]);
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile(args[0], manager);

      FixedTopologyModel lpModel = new FixedTopologyModel(args[1], manager, args[3], args[2]);
      boolean compute = Boolean.parseBoolean(args[4]);

      if (compute) {
        log.info("Computing model");
        lpModel.init();
        lpModel.compute();
        lpModel.postCompute();
      } else {
        log.info("Attempting to load model from export information");
        lpModel.importModel();
      }

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
