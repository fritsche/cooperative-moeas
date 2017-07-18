package org.uma.jmetal.experiment.methodology;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.dea.DEA;
import org.uma.jmetal.algorithm.multiobjective.dea.DEABuilder;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.problem.Problem;

public class BroadcastHeDiConfiguration extends DEAConfiguration {

    public BroadcastHeDiConfiguration() {

    }

    @Override
    public Algorithm cofigure(Problem problem, int popSize, int generations) {
        DEABuilder builder = new DEABuilder();

        int numberofislands = 3; // nsgaiii + moeadd + nsgaii

        // we do not split population anymore
//        popSize = (int) Math.ceil((double) popSize / numberofislands);
        // instead we split the generations
        int islandGenerations = generations / numberofislands;

        int bufferSizeLimit = (numberofislands -1) * popSize;
        
        // * 1. Build each island; 
        Island moeadd = new Island(configureMOEADDIsland(problem, popSize, islandGenerations), bufferSizeLimit);
        moeadd.getAlgorithm().setIsland(moeadd);

        Island nsgaiii = new Island(configureNSGAIIIIsland(problem, popSize, islandGenerations), bufferSizeLimit);
        nsgaiii.getAlgorithm().setIsland(nsgaiii);
        
        Island nsgaii = new Island(configureNSGAIIIsland(problem, popSize, generations), bufferSizeLimit);
        nsgaii.getAlgorithm().setIsland(nsgaii);

        // * 2. Set the Broadcast neighborhood; 
        moeadd.addNeighbor(nsgaiii);
        moeadd.addNeighbor(nsgaii);
        
        nsgaii.addNeighbor(moeadd);
        nsgaii.addNeighbor(nsgaiii);
      
        nsgaiii.addNeighbor(moeadd);
        nsgaiii.addNeighbor(nsgaii);

        // * 3. Add the islands to the dEA algorithm;
        builder.addIsland(moeadd);
        builder.addIsland(nsgaiii);
        builder.addIsland(nsgaii);

        // the MOEADDIsland do not support sync communication as it is SteadyState
        builder.setVersion(DEA.VERSION.ASYNC);

        DEA dea = builder.build();

        dea.setName("BroadcastHeDi");

        return dea;

    }
}
