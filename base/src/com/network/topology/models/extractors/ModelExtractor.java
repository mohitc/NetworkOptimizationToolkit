package com.network.topology.models.extractors;

import com.lpapi.entities.LPModel;

public interface ModelExtractor<T> {

  T extractModel(LPModel model) throws ModelExtractionException;
}
