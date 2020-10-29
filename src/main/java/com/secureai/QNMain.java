package com.secureai;

import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.rl.qn.FilteredDynamicQTable;
import com.secureai.rl.qn.QLearning;
import com.secureai.system.SystemEnvironment;
import com.secureai.system.SystemState;
import com.secureai.utils.ArgsUtils;
import com.secureai.utils.TimeUtils;
import com.secureai.utils.YAML;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.Map;

public class QNMain {

    public static void main(String... args) throws IOException {
        BasicConfigurator.configure();
        TimeUtils.setupStartMillis();

        Map<String, String> argsMap = ArgsUtils.toMap(args);

        Topology topology = YAML.parse(String.format("data/topologies/topology-%s.yml", argsMap.getOrDefault("topology", "1-vms")), Topology.class);
        ActionSet actionSet = YAML.parse(String.format("data/action-sets/action-set-%s.yml", argsMap.getOrDefault("actionSet", "1-vms")), ActionSet.class);

        SystemEnvironment mdp = new SystemEnvironment(topology, actionSet);

        QLearning.QNConfiguration qnConfiguration = new QLearning.QNConfiguration(
                Integer.parseInt(argsMap.getOrDefault("seed", "123")),              //Random seed
                Integer.parseInt(argsMap.getOrDefault("maxStep", "15000")),        //episodes
                Integer.parseInt(argsMap.getOrDefault("maxEpisodeStep", "400")),    //max step
                Double.parseDouble(argsMap.getOrDefault("learningRate", "0.9")),    //alpha
                Double.parseDouble(argsMap.getOrDefault("discountFactor", "0.75")), //gamma
                Float.parseFloat(argsMap.getOrDefault("minEpsilon", "0.01")),       //min epsilon
                Integer.parseInt(argsMap.getOrDefault("epsilonNbStep", "20000"))    //num step for eps greedy anneal
        );

        FilteredDynamicQTable qTable = new FilteredDynamicQTable(mdp.getActionSpace().getSize());
        //qTable.setDynamicQTableGetFilter(input -> ArrayUtils.toPrimitive(mdp.getActionSpace().actionsMask(input)));

        QLearning<SystemState> ql = new QLearning<>(mdp, qnConfiguration, qTable);
        ql.train();

        ql.evaluate(5);
    }
}
