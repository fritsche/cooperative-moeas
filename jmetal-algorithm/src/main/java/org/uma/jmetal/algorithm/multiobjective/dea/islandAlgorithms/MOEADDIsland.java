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
package org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.solutionattribute.Ranking;

public class MOEADDIsland<S extends Solution<?>> extends MOEADD<DoubleSolution> implements IslandAlgorithm<DoubleSolution> {

    private Island island;
    private final int migrationFrequency;

    public MOEADDIsland(Problem<DoubleSolution> problem, int populationSize,
            int resultPopulationSize, int maxEvaluations,
            MutationOperator<DoubleSolution> mutation,
            CrossoverOperator<DoubleSolution> crossover,
            FunctionType functionType, String dataDirectory,
            double neighborhoodSelectionProbability,
            int maximumNumberOfReplacedSolutions, int neighborSize, int migrationFrequency) {

        super(problem, populationSize, resultPopulationSize, maxEvaluations,
                crossover, mutation, functionType, dataDirectory,
                neighborhoodSelectionProbability,
                maximumNumberOfReplacedSolutions, neighborSize);

        this.migrationFrequency = migrationFrequency;
    }

    @Override
    public void setIsland(Island island) {
        this.island = island;
    }

    @Override
    public List<DoubleSolution> selectionPolicy() {
        JMetalLogger.logger.log(Level.INFO, "sent migrants: {0}", offspringPopulation.size());
        List<DoubleSolution> migration = new ArrayList(offspringPopulation);
        offspringPopulation.clear();
        return migration;
    }

    @Override
    public void replacementPolicy() {
        List<DoubleSolution> migrants = island.getMigrantQueue();
        JMetalLogger.logger.log(Level.INFO, "received migrants: {0}", migrants.size());

        for (DoubleSolution migrant : migrants) {
            updateIdealPoint(migrant);
            updateNadirPoint(migrant);
            updateArchive(migrant);
        }

    }

    @Override
    public void run() {
        evaluations = 0;
        population = new ArrayList(populationSize);

        rankSolution = new HashMap<>();
        associateDistSolution = new HashMap();
        regionSolution = new HashMap();

        neighborhood = new int[populationSize][neighborSize];
        lambda = new double[populationSize][problem.getNumberOfObjectives()];

        idealPoint = new double[problem.getNumberOfObjectives()]; // ideal point for Pareto-based population
        nadirPoint = new double[problem.getNumberOfObjectives()]; // nadir point for Pareto-based population

        rankIdx = new int[populationSize][populationSize];
        subregionIdx = new int[populationSize][populationSize];
        subregionDist = new double[populationSize][populationSize];

        // STEP 1. Initialization
        initializeUniformWeight();
        initializeNeighborhood();
        initPopulation();
        // init offspring (MOEADDIsland only)
        offspringPopulation = new ArrayList<>(populationSize);
        initializeIdealPoint();
        initializeNadirPoint();

        // initialize the distance
        for (int i = 0; i < populationSize; i++) {
            double distance = calculateDistance2(population.get(i), lambda[i], idealPoint, nadirPoint);
            subregionDist[i][i] = distance;
        }

        Ranking ranking = computeRanking(population);
        for (int curRank = 0; curRank < ranking.getNumberOfSubfronts(); curRank++) {
            List<Solution> front = ranking.getSubfront(curRank);
            for (Solution s : front) {
                int position = this.population.indexOf(s);
                rankIdx[curRank][position] = 1;
            }
        }

        // main procedure
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int cid = permutation[i];
                int type;
                double rnd = randomGenerator.nextDouble();

                // mating selection style
                if (rnd < neighborhoodSelectionProbability) {
                    type = 1; // neighborhood
                } else {
                    type = 2; // whole population
                }
                DoubleSolution[] parentvect;
                parentvect = matingSelection(cid, type);
                ArrayList<DoubleSolution> parents = new ArrayList<>(Arrays.asList(parentvect));

                List<DoubleSolution> children = crossoverOperator.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint(child);
                updateArchive(child);

                // keep the generated offspring to send
                offspringPopulation.add(child);

                // convert migrationFrequency from iterations to FEs before compare
                if (evaluations % (migrationFrequency * populationSize) == 0) {

                    JMetalLogger.logger.log(Level.INFO, "iteration: {0}", evaluations / populationSize );
                    // send solutions
                    island.sendSolutions(selectionPolicy());
                    
                    island.await();
                    
                    // receive solutions
                    replacementPolicy();
                }

                //System.out.println(evaluations);
            } // for
        } while (evaluations < maxEvaluations);
        
         island.setAcceptingMigrants(false);
    }

}
