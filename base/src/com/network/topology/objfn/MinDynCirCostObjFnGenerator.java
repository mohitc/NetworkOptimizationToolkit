package com.network.topology.objfn;

import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPObjType;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPConstantException;
import com.lpapi.exception.LPExpressionException;
import com.lpapi.exception.LPNameException;
import com.lpapi.exception.LPVarException;
import com.network.topology.VariableBoundConstants;
import com.network.topology.dyncircuits.parser.DynCircuitClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MinDynCirCostObjFnGenerator implements ObjFnGenerator{

  private static final Logger log = LoggerFactory.getLogger(MinDynCirCostObjFnGenerator.class);

  private Set<String> vertexSet;

  private LPNameGenerator dynCircuitVarNameGenerator;

  private Map<Integer, DynCircuitClass> circuitClassMap;


  public MinDynCirCostObjFnGenerator(Set<String> vertexSet, Map<Integer, DynCircuitClass> circuitClassMap, LPNameGenerator dynCircuitVarNameGenerator){
    if (dynCircuitVarNameGenerator==null) {
      log.error("Initialized with empty dynamic circuit variable name generator");
      this.dynCircuitVarNameGenerator = new LPEmptyNameGenratorImpl<>();
    } else {
      this.dynCircuitVarNameGenerator = dynCircuitVarNameGenerator;
    }
    if (vertexSet!=null) {
      this.vertexSet = Collections.unmodifiableSet(vertexSet);
    } else {
      log.error("Constraint generator initialized with empty set of vertices");
      this.vertexSet = Collections.EMPTY_SET;
    }
    if (circuitClassMap==null) {
      log.error("Initialized with empty dynamic circuit definition map");
      this.circuitClassMap = Collections.EMPTY_MAP;
    } else {
      this.circuitClassMap = circuitClassMap;
    }
  }

  @Override
  public LPExpression generate(LPModel model) throws LPExpressionException {
    LPExpression expr = new LPExpression(model);
    try {
      for (int i: circuitClassMap.keySet()) {
        double cost = circuitClassMap.get(i).getCost();
        for (String x: vertexSet) {
          for (String y: vertexSet) {
            if (x.equals(y))
              continue;
            expr.addTerm(cost, model.getLPVar(dynCircuitVarNameGenerator.getName(i, x, y)));
          }
        }
      }
      return expr;
    } catch (LPVarException e) {
      log.error("Error extracting var from model", e);
      throw new LPExpressionException("Error extracting var from model: " + e.getMessage());
    } catch (LPNameException e) {
      log.error("Error generating var name", e);
      throw new LPExpressionException("Error generating var name: " + e.getMessage());
    }
  }

  //Objective is to minimize the circuit cost
  @Override
  public LPObjType getObjType() {
    return LPObjType.MINIMIZE;
  }
}
