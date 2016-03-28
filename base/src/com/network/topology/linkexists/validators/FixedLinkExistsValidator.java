package com.network.topology.linkexists.validators;

import com.lpapi.entities.LPModel;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.exception.LPNameException;
import com.lpapi.exception.LPVarException;
import com.network.topology.models.validators.ModelValidationException;
import com.network.topology.models.validators.ModelValidator;
import com.topology.primitives.ConnectionPoint;
import com.topology.primitives.Link;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.TopologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class FixedLinkExistsValidator extends ModelValidator {

  private static final Logger log = LoggerFactory.getLogger(FixedLinkExistsValidator.class);

  private TopologyManager initTopology;

  private LPNameGenerator linkExistsNameGenerator;

  private Set<String> vertexLabels;

  public FixedLinkExistsValidator(LPModel model, TopologyManager initTopology, LPNameGenerator linkExistsNameGenerator, Set<String> vertexLabels) {
    super(model);
    this.vertexLabels = vertexLabels;
    this.linkExistsNameGenerator = linkExistsNameGenerator;
    this.initTopology = initTopology;
  }


  @Override
  public void validate() throws ModelValidationException {
    if (this.vertexLabels == null) {
      throw new ModelValidationException("Initialized with null vertex label set");
    }
    if (this.linkExistsNameGenerator == null) {
      throw new ModelValidationException("Initialized with null name generator");
    }
    if (this.initTopology== null) {
      throw new ModelValidationException("Initialized with null topology");
    }
    log.info("Validating links computed by the model against the links in the initial topology");
    try {

      for (String i: vertexLabels) {
        for (String j: vertexLabels) {
          if (i.equals(j))
            continue;
          ConnectionPoint iCp = initTopology.getSingleElementByLabel(i, ConnectionPoint.class);
          ConnectionPoint jCp = initTopology.getSingleElementByLabel(j, ConnectionPoint.class);
          Set<Link> links = iCp.getConnections(Link.class).stream().filter(u -> u.getaEnd().getID() == jCp.getID() || u.getzEnd().getID() == jCp.getID()).collect(Collectors.toSet());
          boolean linkExists = (links!=null) && (links.size()>0);
          boolean modelLinkExists = getModel().getLPVar(linkExistsNameGenerator.getName(i, j)).getResult().intValue()==1;
          if (linkExists!=modelLinkExists) {
            throw new ModelValidationException("Validation error: LinkExists validation between " + i + " amd " + j + ", topology result: " + linkExists + ", model result: " + modelLinkExists);
          }
        }
      }
    } catch (TopologyException | LPNameException | LPVarException e) {
      throw new ModelValidationException("Exception while validating if computed links match the initial link configuration", e);
    }

  }
}
