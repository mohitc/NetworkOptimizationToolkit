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

  public FixedTopologyModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
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

      TopologyManager manager = new TopologyManagerImpl("test");
      SNDLibImportTopology importer = new SNDLibImportTopology();
      importer.importFromFile("conf/nobel-us.xml", manager);

      FixedTopologyModel lpModel = new FixedTopologyModel("conf/circuit-cap.xml", manager);
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
