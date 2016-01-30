package com.network.topology.objfn;

import com.lpapi.entities.LPExpression;
import com.lpapi.entities.LPModel;
import com.lpapi.entities.LPObjType;
import com.lpapi.exception.LPExpressionException;

public interface ObjFnGenerator {

  LPExpression generate(LPModel model) throws LPExpressionException;

  LPObjType getObjType();
}
