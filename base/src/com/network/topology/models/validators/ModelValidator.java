package com.network.topology.models.validators;

import com.lpapi.entities.LPModel;

public abstract class ModelValidator {

  private LPModel model;

  public LPModel getModel() {
    return model;
  }

  public ModelValidator(LPModel model) {
    this.model = model;
  }

  public abstract void validate() throws ModelValidationException;
}
