package com.network.topology.capacity.constants;

import com.lpapi.entities.LPConstantGroup;
import com.lpapi.entities.LPConstraintGroup;
import com.lpapi.entities.group.LPGroupInitializer;
import com.lpapi.entities.group.LPNameGenerator;
import com.lpapi.entities.group.generators.LPEmptyNameGenratorImpl;
import com.lpapi.exception.LPModelException;
import com.lpapi.exception.LPNameException;
import com.network.topology.models.fixedtopology.FixedTopologyModel;
import com.topology.primitives.TopologyManager;
import com.topology.primitives.exception.properties.PropertyException;
import com.topology.primitives.properties.TEPropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by fpederzolli on 16/01/16.
 */
public class CapacityConstGroupInitializer extends LPGroupInitializer {
    private static final Logger log = LoggerFactory.getLogger(CapacityConstGroupInitializer.class);

    private Set<String> vertices;

    private LPNameGenerator capacityConstNameGenerator;

    public CapacityConstGroupInitializer(Set<String> vertices, LPNameGenerator capacityConstNameGenerator) {
        if (vertices==null) {
            log.error("Set of vertices is null, reverting to empty set");
            this.vertices = Collections.EMPTY_SET;
        } else {
            this.vertices = vertices;
        }

        if (capacityConstNameGenerator==null) {
            log.error("Initialized with empty variable name generator");
            this.capacityConstNameGenerator = new LPEmptyNameGenratorImpl<>();
        } else {
            this.capacityConstNameGenerator = capacityConstNameGenerator;
        }
    }

    @Override
    public void run() throws LPModelException {
        try {
            LPConstantGroup group = model().getLPConstantGroup(this.getGroup().getIdentifier());
            TopologyManager topo;
            try {
                Field this$0 = model().getClass().getDeclaredField("this$0");
                FixedTopologyModel outer = (FixedTopologyModel) this$0.get(model().getClass()); //TODO: this is an ugly hack
                topo = outer._instance;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            TEPropertyKey demandStoreKey;
            Map<String, Map<String, String>> demandStore;
            try {
                demandStoreKey = topo.getKey("Demands");
                demandStore = (Map<String, Map<String, String>>) topo.getProperty(demandStoreKey);
            } catch (PropertyException e) {
                e.printStackTrace();
                throw new LPModelException("Demand store not found: " + e.getMessage());
            }
            for (String i : vertices) {
                for (String j : vertices) {
                    String label = i.concat(j);
                    if (demandStore.containsKey(label)){
                        Map<String, String> demand = demandStore.get(label);

                        model().createLpConstant(capacityConstNameGenerator.getName(i,j), Double.valueOf(demand.get("capacity")), group);
                    }
                }
            }
        }catch (LPNameException e) {
        log.error("Variable name not found: " + e.getMessage());
        throw new LPModelException("Variable name not found: " + e.getMessage());
    }
//        try {
//            LPVarGroup group = this.getGroup().getModel().getLPVarGroup(this.getGroup().getIdentifier());
//            for (String i : vertices) {
//                for (String j : vertices) {
//                    if (i.equals(j)) {
//                        continue;
//                    }
//                    this.getGroup().getModel().createLPVar(group.getNameGenerator().getName(i, j), LPVarType.INTEGER, 0, Integer.MAX_VALUE, group);
//                }
//            }
//
//        } catch (LPNameException e) {
//            log.error("Error while initializing Capacity variable group variables", e);
//            throw new LPModelException(e.getMessage());
//        }
    }
}
