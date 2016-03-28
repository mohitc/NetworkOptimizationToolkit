package com.network.topology.objfn;

import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPObjFnGenerator;
import com.lpapi.entities.LPObjType;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPExpressionException;
import com.lpapi.exception.LPNameException;
import com.lpapi.exception.LPVarException;
import com.lpapi.exception.LPVarGroupException;
import com.network.topology.VarGroups;
import com.network.topology.dyncircuits.parser.DynCircuitClass;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MinDynCirCostObjFnGenerator extends LPObjFnGenerator {

  private Set<String> vertexSet;

  private Map<Integer, DynCircuitClass> circuitClassMap;

  public MinDynCirCostObjFnGenerator(Set<String> vertexSet, Map<Integer, DynCircuitClass> circuitClassMap){
    super(LPObjType.MINIMIZE);
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

  public LPExpression generate() throws LPExpressionException {
    LPExpression expr = new LPExpression(getModel());
    try {
      LPNameGenerator dynCircuitVarNameGenerator = getModel().getLPVarGroup(VarGroups.DYN_CIRCUITS).getNameGenerator();
      for (Map.Entry<Integer, DynCircuitClass> entry: circuitClassMap.entrySet()) {
        double cost = entry.getValue().getCost();
        for (String x: vertexSet) {
          for (String y: vertexSet) {
            if (x.equals(y))
              continue;
            expr.addTerm(cost, getModel().getLPVar(dynCircuitVarNameGenerator.getName(entry.getKey(), x, y)));
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
    } catch (LPVarGroupException e) {
      log.error("Dynamic circuit var group not found", e);
      throw new LPExpressionException("Dynamic circuit var group not found: " + e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected exception: ", e);
      throw new LPExpressionException("Unexpected exception: " + e.getMessage());
    }
  }
}
