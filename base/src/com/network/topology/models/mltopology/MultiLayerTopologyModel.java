package com.network.topology.models.mltopology;

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
import com.network.topology.capacity.constants.InitialCapacityConstNameGenerator;
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
import com.network.topology.dyncircuits.vars.DynCircuitVarNameGenerator;
import com.network.topology.linkexists.constants.LinkExistsConstantGroupInitializer;
import com.network.topology.linkexists.constants.LinkExistsConstantNameGenerator;
import com.network.topology.linkexists.constraints.LinkExistsConstrGroupInitializer;
import com.network.topology.linkexists.constraints.LinkExistsConstrNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsNameGenerator;
import com.network.topology.linkexists.vars.LinkExistsVarGroupInitializer;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import com.network.topology.objfn.MinDynCirCostObjFnGenerator;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(MultiLayerTopologyModel.class);

  protected LPModel model;

  protected TopologyManager _instance;

  protected DynCircuitClassParser dynCircuitParser;

  protected List<ModelValidator> validatorList;

  public MultiLayerTopologyModel(String circuitConfFile, TopologyManager manager) {
    initDynamicCircuitParser(circuitConfFile);
    this._instance = manager;
  }

  public void initDynamicCircuitParser(String circuitConfFile) {
    dynCircuitParser = new DynCircuitClassParser(circuitConfFile);
  }

  protected Set<String> getVertexLabels() {
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

    Set<String> vertexLabels = getVertexLabels();

    LPConstantGroup constantGroup = model.createLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS, ConstantGroups.VARIABLE_BOUNDS_DESC);
    //constant to indicate the max number of dynamic circuits between a pair of nodes
    model.createLpConstant(FixedConstants.DYN_CIRTUITS_MAX, 1, constantGroup);
    //constant to indicate the number of distinct dynamic circuit categories available
    model.createLpConstant(FixedConstants.CIRCUIT_CLASSES, dynCircuitParser.getResult().keySet().size(), constantGroup);
    //constant to indicate the max capacity C(inf) for a link between a pair of nodes
    model.createLpConstant(FixedConstants.CAP_MAX, 100000, constantGroup);

    LPNameGenerator linkExistsConstantNameGenerator = new LinkExistsConstantNameGenerator(vertexLabels);
    LinkExistsConstantGroupInitializer linkExistsConstantGroupInitializer = new LinkExistsConstantGroupInitializer(_instance);
    model.createLPConstantGroup(ConstantGroups.LINK_EXISTS, ConstantGroups.LINK_EXISTS_DESC, linkExistsConstantNameGenerator,
        linkExistsConstantGroupInitializer);

    LPNameGenerator initialCapacityConstNameGenerator = new InitialCapacityConstNameGenerator(vertexLabels);
    InitialCapacityConstGroupInitializer initialCapacityConstGroupInitializer = new InitialCapacityConstGroupInitializer(vertexLabels,_instance);
    model.createLPConstantGroup(ConstantGroups.INITIAL_CAP, ConstantGroups.INITIAL_CAP_DESC,
        initialCapacityConstNameGenerator, initialCapacityConstGroupInitializer);

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
    LPNameGenerator linkExistsNameGenerator =   new LinkExistsNameGenerator(vertexLabels);
    LinkExistsVarGroupInitializer linkExistsVarGroupInitializer = new LinkExistsVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.LINK_EXISTS, VarGroups.LINK_EXISTS_DESC, linkExistsNameGenerator, linkExistsVarGroupInitializer);

    LPNameGenerator capacityVarNameGenerator = new CapacityVarNameGenerator(vertexLabels);
    CapacityVarGroupInitializer capacityVarGroupInitializer = new CapacityVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(VarGroups.CAPACITY, VarGroups.CAPACITY_DESC, capacityVarNameGenerator, capacityVarGroupInitializer);

    LPNameGenerator dynCircuitVarNameGenerator;
    try {
      dynCircuitVarNameGenerator = new DynCircuitVarNameGenerator((int)model.getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue(), vertexLabels);
      DynCircuitVarGroupInitializer dynCircuitVarGroupInitializer = new DynCircuitVarGroupInitializer(vertexLabels);
      model.createLPVarGroup(VarGroups.DYN_CIRCUITS, VarGroups.DYN_CIRCUITS_DESC, dynCircuitVarNameGenerator, dynCircuitVarGroupInitializer);
    } catch (LPConstantException e) {
      throw new LPVarGroupException("Exception while generating dynamic circuit variables: " + e.getMessage());
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
    Set<String> vertexLabels = getVertexLabels();

    //Link Exists constraints
    try {
      int circuitClasses = (int) model.getLPConstant(FixedConstants.CIRCUIT_CLASSES).getValue();

      LinkExistsConstrNameGenerator lLinkExistsConstrNameGenerator = new LinkExistsConstrNameGenerator(vertexLabels);
      LinkExistsConstrGroupInitializer linkExistsVarGroupInitializer = new LinkExistsConstrGroupInitializer(vertexLabels);
      model.createLPConstraintGroup(ConstraintGroups.LINK_EXISTS, ConstraintGroups.LINK_EXISTS_DESC, lLinkExistsConstrNameGenerator, linkExistsVarGroupInitializer);

      //Dynamic circuit bound constrants
      DynCircuitBoundConstrNameGenerator dynCircuitBoundConstrNameGenerator = new DynCircuitBoundConstrNameGenerator(vertexLabels);
      DynCircuitBoundConstrGroupInitializer dynCircuitBoundConstrGroupInitializer = new DynCircuitBoundConstrGroupInitializer(vertexLabels);
      model.createLPConstraintGroup(ConstraintGroups.DYN_CIRCUIT_BOUND, ConstraintGroups.DYN_CIRCUIT_BOUND_DESC, dynCircuitBoundConstrNameGenerator, dynCircuitBoundConstrGroupInitializer);

      //Symmetric dynamic circuit constraint
      SymDynCirConstrNameGenerator symDynCirConstrNameGenerator = new SymDynCirConstrNameGenerator(circuitClasses, vertexLabels);
      SymDynCirConstrGroupInitializer symDynCirConstrGroupInitializer = new SymDynCirConstrGroupInitializer(vertexLabels);
      model.createLPConstraintGroup(ConstraintGroups.SYMM_DYN_CIRCUITS, ConstraintGroups.SYMM_DYN_CIRCUITS_DESC, symDynCirConstrNameGenerator, symDynCirConstrGroupInitializer);

      //Capacity constraints
      ActualCapacityNameGenerator actualCapacityNameGenerator = new ActualCapacityNameGenerator(vertexLabels);
      ActualCapacityGroupInitializer actualCapacityGroupInitializer = new ActualCapacityGroupInitializer(vertexLabels, dynCircuitParser.getResult());
      model.createLPConstraintGroup(ConstraintGroups.ACTUAL_CAPACITY, ConstraintGroups.ACTUAL_CAPACITY_DESC, actualCapacityNameGenerator, actualCapacityGroupInitializer);

    } catch (LPConstantException e) {
      log.error("Constant to indicate the number of dynamic circuit classes not defined");
    }

  }

  public void initModel() throws LPModelException {
//  model = new CplexLPModel("Test");
//  model = new GurobiLPModel("Test");
//    model = new SkeletonLPModel("Test");
    model = new GlpkLPModel("Test");
  }

  public void init() throws LPModelException {
    initModel();
    initConstants();
    initVarGroups();
    initConstraintGroups();
    //Initialize Objective function generator
    model.attachObjectiveFunctionGenerator(new MinDynCirCostObjFnGenerator(getVertexLabels(),
        dynCircuitParser.getResult()));
    //Initialize LP Model
    model.init();
    model.initObjectiveFunction();
  }

  public void compute() throws LPModelException {
    model.computeModel();
  }

  public abstract TopologyManager getExtractedModel() throws ModelExtractionException;

  public void postCompute() {
    try {
      if (model.getSolutionStatus() == LPSolutionStatus.OPTIMAL) {
        log.info("Objective:" + model.getObjectiveValue());

        initModelValidators();
        runModelValidators();

        log.info("Exporting model to JSON files");
        LPModelExporter exporter = LPModelExporterFactory.instance(model, LPModelExporterType.JSON_FILE);
        ((JSONFileLPModelExporter) exporter).setFolderPath("./export/");
        exporter.export();

        //Import model from JSON to test
        log.info("Import model from JSON Files");
        LPModel importModel = new SkeletonLPModel(model.getIdentifier());
        LPModelImporter modelImporter = LPModelImporterFactory.instance(importModel, LPModelImporterType.JSON_FILE);
        ((JSONFileLPModelImporter) modelImporter).setFolderPath("./export/");
        modelImporter.importModel();

        model = importModel;
        runModelValidators();

        log.info("Import Successful");
      }
    } catch (ModelValidationException e) {
      log.error("Error while validating model", e);
    } catch (LPExportException e) {
      log.error("Error in exporting LP model", e);
    } catch (LPImportException e) {
      log.error("Error in importing LP model", e);
    } catch (LPModelException e) {
      log.error("LP model exception", e);
    }

  }

  public abstract void initModelValidators();

  public void runModelValidators() throws ModelValidationException {
    for (ModelValidator validator: validatorList) {
      validator.validate();
    }
  }

  public abstract ModelExtractor<TopologyManager> initModelExtractor();

}
