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
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class NSGAIIIsland<S extends Solution<?>> extends NSGAII<S> implements IslandAlgorithm<S> {

    private Island island;
    private final int migrationFrequency;
    private List<S> offspringPopulation;
    private List<S> matingPopulation;

    public NSGAIIIsland(Problem<S> problem, int maxEvaluations, int populationSize,
            CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
            SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, int migrationFrequency) {
        super(problem, maxEvaluations, populationSize, crossoverOperator,
                mutationOperator, selectionOperator, evaluator);
        this.migrationFrequency = migrationFrequency;
    }

    @Override
    public void setIsland(Island island) {
        this.island = island;
    }

    @Override
    public List<S> selectionPolicy() {
        JMetalLogger.logger.log(Level.INFO, "sent migrants: {0}", offspringPopulation.size());
        return offspringPopulation;
    }

    @Override
    public void replacementPolicy() {
        List<S> migrants = island.getMigrantQueue();
        JMetalLogger.logger.log(Level.INFO, "received migrants: {0}", migrants.size());
        offspringPopulation.addAll(migrants);
    }

    @Override
    public void run() {

        setPopulation(createInitialPopulation());
        setPopulation(evaluatePopulation(getPopulation()));
        initProgress();
        while (!isStoppingConditionReached()) {
            matingPopulation = selection(getPopulation());
            offspringPopulation = reproduction(matingPopulation);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            if (evaluations % (migrationFrequency * maxPopulationSize) == 0) {
                island.sendSolutions(selectionPolicy());
                island.await();
                replacementPolicy();
            }
            setPopulation(replacement(getPopulation(), offspringPopulation));
            updateProgress();
        }

        island.setAcceptingMigrants(false);
    }
}
