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
import org.uma.jmetal.algorithm.multiobjective.dea.DEABuilder;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.MOEADSTMIsland;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.MOEADSTMIslandBuilder;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.NSGAIIIIsland;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.NSGAIIIIslandBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class DEAConfigurationV2 implements AlgorithmConfiguration {

    public DEAConfigurationV2() {

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

    public MOEADSTMIsland configureMOEADSTMIsland(Problem problem, int popSize, int generations) {
        MutationOperator<DoubleSolution> mutation;
        DifferentialEvolutionCrossover crossover;
        double cr = 1.0;
        double f = 0.5;
        crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin");

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        MOEADSTMIslandBuilder builder = new MOEADSTMIslandBuilder(problem);

        builder.setCrossover(crossover)
                .setMaxEvaluations(generations * popSize)
                .setMutation(mutation)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(2)
                .setNeighborSize(20)
                .setPopulationSize(popSize)
                .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                .setDataDirectory("MOEAD_Weights");

        builder.setMigrationFrequency(1); // every iteration the MOEADSTMIsland update 1/5 of the population
        return builder.build();

    }

    @Override
    public Algorithm cofigure(Problem problem, int popSize, int generations) {

        DEABuilder builder = new DEABuilder();

        int numberofislands = 2; // nsgaiii + moeadstm

        popSize = (int) Math.ceil((double) popSize / numberofislands);

        // * 1. Build each island; 
        Island moeadstm = new Island(configureMOEADSTMIsland(problem, popSize, generations));
        moeadstm.getAlgorithm().setIsland(moeadstm);
        
        Island nsgaiii = new Island(configureNSGAIIIIsland(problem, popSize, generations));
        nsgaiii.getAlgorithm().setIsland(nsgaiii);

        // * 2. Set the neighborhood; 
        moeadstm.addNeighbor(nsgaiii);
        nsgaiii.addNeighbor(moeadstm);

        // * 3. Add the islands to the dEA algorithm;
        builder.addIsland(moeadstm);
        builder.addIsland(nsgaiii);
        DEA dea = builder.build();
        dea.setName("dEAv2");
        return dea;

    }

}
