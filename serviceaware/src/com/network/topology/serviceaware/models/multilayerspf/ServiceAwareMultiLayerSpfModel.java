package com.network.topology.serviceaware.models.multilayerspf;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.*;
import com.network.topology.ConstantGroups;
import com.network.topology.models.extractors.ModelExtractionException;
import com.network.topology.models.extractors.ModelExtractor;
import com.network.topology.models.mltopology.MultiLayerTopologyModel;
import com.network.topology.serviceaware.SAConstraintGroups;
import com.network.topology.serviceaware.SAVarGroups;
import com.network.topology.serviceaware.ServiceAwareFixedConstants;
import com.network.topology.serviceaware.routing.constraints.*;
import com.network.topology.serviceaware.routing.vars.ServiceAwareRoutingVarGroupInitializer;
import com.network.topology.serviceaware.routing.vars.ServiceAwareRoutingVarNameGenerator;
import com.topology.primitives.TopologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServiceAwareMultiLayerSpfModel extends MultiLayerTopologyModel {

  private static final Logger log = LoggerFactory.getLogger(ServiceAwareMultiLayerSpfModel.class);

  public ServiceAwareMultiLayerSpfModel(String circuitConfFile, TopologyManager manager) {
    super(circuitConfFile, manager);
  }

  public void initConstants() throws LPConstantGroupException, LPConstantException {
    super.initConstants();

    //add constant for service classes
    LPConstantGroup constantGroup = model.getLPConstantGroup(ConstantGroups.VARIABLE_BOUNDS);
    model.createLpConstant(ServiceAwareFixedConstants.SERVICE_CLASSES, 3, constantGroup);
  }

  public void initVarGroups() throws LPVarGroupException {
    super.initVarGroups();
    int serviceClasses;
    try {
      serviceClasses = (int)model.getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
    } catch (LPConstantException e) {
      log.error("Could not find constant : ", e);
      throw  new LPVarGroupException("Could not find constant for service classes");
    }

    Set<String> vertexLabels = getVertexLabels();
    LPNameGenerator serviceAwareRoutingNameGenerator = new ServiceAwareRoutingVarNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer serviceAwareRoutingVarGroupInitializer = new ServiceAwareRoutingVarGroupInitializer(vertexLabels);
    model.createLPVarGroup(SAVarGroups.SA_ROUTING, SAVarGroups.SA_ROUTING_DESC, serviceAwareRoutingNameGenerator, serviceAwareRoutingVarGroupInitializer);
  }


  public void initConstraintGroups() throws LPConstraintGroupException {
    super.initConstraintGroups();
    Set<String> vertexLabels = getVertexLabels();

    int serviceClasses;
    try {
      serviceClasses = (int)model.getLPConstant(ServiceAwareFixedConstants.SERVICE_CLASSES).getValue();
    } catch (LPConstantException e) {
      log.error("Could not find constant : ", e);
      throw  new LPConstraintGroupException("Could not find constant for service classes");
    }

    //Routing if link exists
    LPNameGenerator serviceAwareRoutingConstrNameGenerator = new ServiceAwareRoutingConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer serviceAwareRoutingConstrGroupInitializer = new ServiceAwareRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTING_LINK_EXISTS, SAConstraintGroups.SA_ROUTING_LINK_EXISTS_DESC,
        serviceAwareRoutingConstrNameGenerator, serviceAwareRoutingConstrGroupInitializer);

    //Symmetric routing
    LPNameGenerator saSymmetricRoutingConstrNameGenerator = new ServiceAwareSymmetricRoutingConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saSymmetricRoutingConstrGroupInitializer = new ServiceAwareSymmetricRoutingConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_SYMM_ROUTING, SAConstraintGroups.SA_SYMM_ROUTING_DESC,
        saSymmetricRoutingConstrNameGenerator, saSymmetricRoutingConstrGroupInitializer);

    //Routing continuity
    LPNameGenerator saRoutingContinuityConstrNameGenerator = new ServiceAwareRoutingContinuityConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saRoutingContinuityConstrGroupInitializer = new ServiceAwareRoutingContinuityConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_ROUTING_CONTINUITY, SAConstraintGroups.SA_ROUTING_CONTINUITY_DESC,
        saRoutingContinuityConstrNameGenerator, saRoutingContinuityConstrGroupInitializer);

    //Source Loop Avoidance
    LPNameGenerator saSourceLoopAvoidanceConstrNameGenerator = new ServiceAwareSourceLoopAvoidanceConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saSourceLoopAvoidanceConstrGroupInitializer = new ServiceAwareSourceLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_SOURCE_LOOP_AVOIDANCE, SAConstraintGroups.SA_SOURCE_LOOP_AVOIDANCE_DESC,
        saSourceLoopAvoidanceConstrNameGenerator, saSourceLoopAvoidanceConstrGroupInitializer);

    LPNameGenerator saDestLoopAvoidanceConstrNameGenerator = new ServiceAwareDestLoopAvoidanceConstrNameGenerator(vertexLabels, serviceClasses);
    LPGroupInitializer saDestLoopAvoidanceConstrGroupInitializer = new ServiceAwareDestLoopAvoidanceConstrGroupInitializer(vertexLabels);
    model.createLPConstraintGroup(SAConstraintGroups.SA_DEST_LOOP_AVOIDANCE, SAConstraintGroups.SA_DEST_LOOP_AVOIDANCE_DESC,
        saDestLoopAvoidanceConstrNameGenerator, saDestLoopAvoidanceConstrGroupInitializer);
  }

  @Override
  public TopologyManager getExtractedModel() throws ModelExtractionException {
    return null;
  }

  @Override
  public void initModelValidators() {

  }

  @Override
  public ModelExtractor<TopologyManager> initModelExtractor() {
    return null;
  }
}
