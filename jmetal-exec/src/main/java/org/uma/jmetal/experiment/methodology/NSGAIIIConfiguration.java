/*
 * Copyright (C) 2017 Gian M. Fritsche <gmfritsche@inf.ufpr.br>
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
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
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
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class NSGAIIIConfiguration implements AlgorithmConfiguration<DoubleSolution>{

    @Override
    public Algorithm cofigure(Problem<DoubleSolution> problem, int popSize, int generations) {
        double crossoverProbability = 1.0;
            double crossoverDistributionIndex = 30.0;
            CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
            MutationOperator<DoubleSolution> mutation;
            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

            double mutationProbability = 1.0 / problem.getNumberOfVariables();
            double mutationDistributionIndex = 20.0;
            mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
            selection = new BinaryTournamentSelection<>();
            
            return (NSGAIII) new NSGAIIIBuilder<>(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setMaxIterations(generations)
                    // the NSGA-III population size is set based on the number of weights
                .setUniformWeightFileName("NSGAIII_Weights/W" + problem.getNumberOfObjectives() + "D_" + popSize + ".dat")
                .build();
    }
    
}
