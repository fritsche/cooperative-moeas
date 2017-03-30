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

import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADSTM1;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class MOEADSTMIsland<S extends Solution<?>> extends MOEADSTM1 implements IslandAlgorithm<DoubleSolution> {

    private Island island;
    private int migrationFrequency;
    private final DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public MOEADSTMIsland(Problem<DoubleSolution> problem, int populationSize,
            int resultPopulationSize, int maxEvaluations,
            MutationOperator<DoubleSolution> mutation,
            CrossoverOperator<DoubleSolution> crossover,
            FunctionType functionType, String dataDirectory,
            double neighborhoodSelectionProbability,
            int maximumNumberOfReplacedSolutions, int neighborSize, int migrationFrequency) {

        super(problem, populationSize, resultPopulationSize, maxEvaluations,
                mutation, crossover, functionType, dataDirectory,
                neighborhoodSelectionProbability,
                maximumNumberOfReplacedSolutions, neighborSize);

        this.migrationFrequency = migrationFrequency;
        this.differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    }

    @Override
    public void setIsland(Island island) {
        this.island = island;
    }
    
    @Override
    public List<DoubleSolution> selectionPolicy() {
        JMetalLogger.logger.log(Level.INFO, "sent migrants: {0}", offspringPopulation.size());
        return offspringPopulation;
    }

    @Override
    public void replacementPolicy() {
        // Combine the parent and the current offspring populations
        List<DoubleSolution> migrants = island.getMigrantQueue();
        JMetalLogger.logger.log(Level.INFO, "received migrants: {0}", migrants.size());
        jointPopulation.clear();
        jointPopulation.addAll(population);
        jointPopulation.addAll(migrants);
        // selection process
        stmSelection();
    }

    @Override
    public void run() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
        initializeNadirPoint();

        int generation = 0;
        evaluations = populationSize;
        do {

            List<Integer> permutation = tourSelection(10);
            offspringPopulation.clear();

            for (int i = 0; i < permutation.size(); i++) {
                int subProblemId = permutation.get(i);
                frequency[subProblemId]++;

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

                differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
                List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint((S) child);
                updateNeighborhood(child, subProblemId, neighborType);

                offspringPopulation.add(child);
            }

            // Combine the parent and the current offspring populations
            jointPopulation.clear();
            jointPopulation.addAll(population);
            jointPopulation.addAll(offspringPopulation);

            // selection process
            stmSelection();

            if (generation % migrationFrequency == 0) {
                // send solutions
                island.sendSolutions(selectionPolicy());
                // receive solutions
                replacementPolicy();
            }

            generation++;
            if (generation % 30 == 0) {
                utilityFunction();
            }

        } while (evaluations < maxEvaluations);

    }

    protected void updateNadirPoint(S individual) {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            if (individual.getObjective(i) > nadirPoint[i]) {
                nadirPoint[i] = individual.getObjective(i);
            }
        }
    }

}
