package com.secureai;

import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.rl.vi.ValueIteration;
import com.secureai.system.SystemEnvironment;
import com.secureai.system.SystemState;
import com.secureai.utils.*;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.Map;

public class VIMain {

    public static void main(String... args) throws IOException {
        BasicConfigurator.configure();
        TimeUtils.setupStartMillis();
        System.out.println(TimeUtils.getStartMillis());

        Map<String, String> argsMap = ArgsUtils.toMap(args);

        Topology topology = YAML.parse(String.format("data/topologies/topology-%s.yml", argsMap.getOrDefault("topology", "paper-4")), Topology.class);
        ActionSet actionSet = YAML.parse(String.format("data/action-sets/action-set-%s.yml", argsMap.getOrDefault("actionSet", "paper-7")), ActionSet.class);

        SystemEnvironment mdp = new SystemEnvironment(topology, actionSet);

        ValueIteration.VIConfiguration viConfiguration = new ValueIteration.VIConfiguration(
                Integer.parseInt(argsMap.getOrDefault("seed", "123")),      //Random seed
                Integer.parseInt(argsMap.getOrDefault("iterations", "5")),  //iterations
                Double.parseDouble(argsMap.getOrDefault("gamma", "0.75")),  //gamma
                Double.parseDouble(argsMap.getOrDefault("epsilon", "1e-8")) //epsilon
        );

        ValueIteration<SystemState> vi = new ValueIteration<>(mdp, viConfiguration);
        //vi.setValueIterationFilter(input -> ArrayUtils.toPrimitive(mdp.getActionSpace().actionsMask(input)));

        vi.solve();

        double result = vi.evaluate(5);
        ValueWriter.writeValue("output/value_iteration.txt", new Timestamped<>(result));
    }
}
