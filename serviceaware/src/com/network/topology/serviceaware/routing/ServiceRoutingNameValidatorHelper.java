package com.network.topology.serviceaware.routing;

import com.lpapi.entities.group.LPNamePrefixValidator;
import com.lpapi.entities.group.validators.LPDistinctPrefixValidator;
import com.lpapi.entities.group.validators.LPPrefixClassValidator;
import com.lpapi.entities.group.validators.LPSetContainmentValidator;
import org.slf4j.Logger;

import java.util.*;

public class ServiceRoutingNameValidatorHelper {

  public static List<LPNamePrefixValidator> getServiceAwareNameValidators (int serviceClasses, Set<String> vertexVars, Logger log) {

    List<LPNamePrefixValidator> validatorList = new ArrayList<>();
    if (vertexVars==null) {
      log.error("Name generator initialized with empty set of vertices");
      vertexVars = Collections.EMPTY_SET;
    }
    if (serviceClasses <=0) {
      log.error("Service classes should be a positive integer (>0). Defaulting to 1");
      serviceClasses = 1;
    }

    //add validators
    Set<Integer> serviceClassSet = new HashSet<>();
    for (int i=1;i<=serviceClasses;i++)
      serviceClassSet.add(i);
    //service class is an integer in the set serviceClassSet
    validatorList.add(new LPPrefixClassValidator(0, Integer.class, "Service class should be an integer"));
    validatorList.add(new LPSetContainmentValidator(0, serviceClassSet, "Not a valid circuit class"));

    //a check for parameter class types
    validatorList.add(new LPPrefixClassValidator(1, String.class, "source should be a string"));
    validatorList.add(new LPPrefixClassValidator(2, String.class, "destination should be a string"));
    validatorList.add(new LPPrefixClassValidator(3, String.class, "start of link should be a string"));
    validatorList.add(new LPPrefixClassValidator(4, String.class, "end of link should be a string"));

    //b) all prefixes should be in the set of vertexes
    validatorList.add(new LPSetContainmentValidator(1, vertexVars, "Source should be in the set of vertices"));
    validatorList.add(new LPSetContainmentValidator(2, vertexVars, "Destination should be in the set of vertices"));
    validatorList.add(new LPSetContainmentValidator(3, vertexVars, "prefix i should be in the set of vertices"));
    validatorList.add(new LPSetContainmentValidator(4, vertexVars, "prefix j should be in the set of vertices"));
    //a) unique because LinkExists x-x is an invalid variable, and
    validatorList.add(new LPDistinctPrefixValidator(1, 2, "Source and destination cannot be the same"));
    validatorList.add(new LPDistinctPrefixValidator(2, 3, "d != i"));
    validatorList.add(new LPDistinctPrefixValidator(3, 4, "i != j"));
    validatorList.add(new LPDistinctPrefixValidator(4, 5, "s != j"));
    return validatorList;
  }
}
