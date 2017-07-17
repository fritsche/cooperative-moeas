/*
 * Copyright (C) 2017 Gian Fritsche <gmfritsche@inf.ufpr.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.uma.jmetal.experiment.methodology;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.dea.DEA;
import org.uma.jmetal.algorithm.multiobjective.dea.DEA.VERSION;
import org.uma.jmetal.algorithm.multiobjective.dea.DEABuilder;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.MOEADDIsland;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.MOEADDIslandBuilder;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.NSGAIIIIsland;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.NSGAIIIIslandBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class HeDiConfiguration implements AlgorithmConfiguration {

    private final DEA.VERSION version;

    HeDiConfiguration(DEA.VERSION version) {
        this.version = version;
    }

    public NSGAIIIIsland configureNSGAIIIIsland(Problem problem, int popSize, int iterations) {

        double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 30.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection<>();

        NSGAIIIIslandBuilder builder = new NSGAIIIIslandBuilder(problem);

        builder.setCrossoverOperator(crossover)
                .setMaxIterations(iterations)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setUniformWeightFileName("NSGAIII_Weights/W" + problem.getNumberOfObjectives() + "D_" + popSize + ".dat");

        builder.setMigrationFrequency(1); // 1 means migrate every iteration
        return builder.build();

    }

    public MOEADDIsland configureMOEADDIsland(Problem problem, int popSize, int generations) {
        MutationOperator<DoubleSolution> mutation;
        double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 30.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        MOEADDIslandBuilder builder = new MOEADDIslandBuilder(problem);

        //problem, populationSize, resultPopulationSize,
//                maxEvaluations, mutation, crossover, functionType,
//                dataDirectory, neighborhoodSelectionProbability,
//                maximumNumberOfReplacedSolutions, neighborSize, migrationFrequency
        builder.setCrossover(crossover)
                .setMutation(mutation)
                .setMaxEvaluations(generations * popSize)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(1)
                .setNeighborSize(20)
                .setPopulationSize(popSize)
                .setFunctionType(AbstractMOEAD.FunctionType.PBI)
                .setDataDirectory("MOEAD_Weights")
                .build();

        builder.setMigrationFrequency(1); // 1 times population size
        return builder.build();

    }

    @Override
    public Algorithm cofigure(Problem problem, int popSize, int generations) {

        DEABuilder builder = new DEABuilder();

        int numberofislands = 2; // nsgaiii + moeadd

        // we do not split population anymore
//        popSize = (int) Math.ceil((double) popSize / numberofislands);
        // instead we split the generations
        int islandGenerations = generations / numberofislands;

        // * 1. Build each island; 
        Island moeadd = new Island(configureMOEADDIsland(problem, popSize, islandGenerations), popSize);
        moeadd.getAlgorithm().setIsland(moeadd);

        Island nsgaiii = new Island(configureNSGAIIIIsland(problem, popSize, islandGenerations), popSize);
        nsgaiii.getAlgorithm().setIsland(nsgaiii);

        // * 2. Set the neighborhood; 
        moeadd.addNeighbor(nsgaiii);
        nsgaiii.addNeighbor(moeadd);

        // * 3. Add the islands to the dEA algorithm;
        builder.addIsland(moeadd);
        builder.addIsland(nsgaiii);

        builder.setVersion(version);

        DEA dea = builder.build();

        dea.setName((version == VERSION.ASYNC) ? ("AsyncHeDi") : ("SyncHeDi"));

        return dea;

    }

}
