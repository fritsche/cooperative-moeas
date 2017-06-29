
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
package org.uma.jmetal.algorithm.multiobjective.twoarch2;

import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class TwoArch2Builder<S extends Solution<?>> implements AlgorithmBuilder<TwoArch2<S, List<S>>> {

    private final Problem<S> problem;
    private int maxEvaluations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SolutionListEvaluator<S> evaluator;

    public TwoArch2Builder(Problem<S> problem, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
        int gmax = 300; // Maximum Iterations
        int n = 100; // Scale for CA and DA
        this.problem = problem;
        maxEvaluations = n * 3 * gmax;
        populationSize = n;
        this.crossoverOperator = crossover;
        this.mutationOperator = mutation;
        evaluator = new SequentialSolutionListEvaluator<S>();
    }

    @Override
    public TwoArch2<S, List<S>> build() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TwoArch2Builder<S> setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public TwoArch2Builder<S> setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

}
