package org.uma.jmetal.runner.multiobjective;


import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.twoarch2.TwoArch2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;

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
/**
 *
 * H. Wang, L. Jiao, and X. Yao. Two arch2: An improved two-archive al- gorithm
 * for many-objective optimization. IEEE Transactions on Evolu- tionary
 * Computation, 19(4):524–541, Aug 2015. ISSN 1089-778X. doi:
 * 10.1109/TEVC.2014.2350987.
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class TwoArch2Runner extends AbstractAlgorithmRunner {

    public static void main(String[] args) {
        Problem<DoubleSolution> problem;
        Algorithm<List<DoubleSolution>> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;

        int gmax = 300; // Maximum Iterations
        int c = 6; // No. of Decision Variables
        int n = 100; // Scale for CA and DA

        problem = new DTLZ1(c, 2);

        double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 15.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 0.1;
        double mutationDistributionIndex = 15.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        algorithm = new TwoArch2Builder<>(problem, crossover, mutation)
                .setMaxEvaluations(n * 3 * gmax)
                .setPopulationSize(n)
                .build();
    }
}
