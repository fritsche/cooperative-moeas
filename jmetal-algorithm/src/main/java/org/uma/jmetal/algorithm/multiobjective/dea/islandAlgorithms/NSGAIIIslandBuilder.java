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

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class NSGAIIIslandBuilder<S extends Solution<?>> extends NSGAIIBuilder {

    private int migrationFrequency = 1;

    public NSGAIIIslandBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator){
        super(problem, crossoverOperator, mutationOperator);
    }

    @Override
    public NSGAIIIsland build() {
        return new NSGAIIIsland<>(super.getProblem(), super.getMaxIterations(), super.getPopulationSize(), super.getCrossoverOperator(),
          super.getMutationOperator(), super.getSelectionOperator(), new SequentialSolutionListEvaluator<>(), migrationFrequency);
    }

    public int getMigrationFrequency() {
        return this.migrationFrequency;
    }

    public NSGAIIBuilder setMigrationFrequency(int migrationFrequency) {
        this.migrationFrequency = migrationFrequency;
        return this;
    }

}
