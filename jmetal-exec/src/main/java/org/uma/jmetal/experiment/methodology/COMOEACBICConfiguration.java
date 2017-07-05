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

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.comoea.COMOEA;
import org.uma.jmetal.algorithm.multiobjective.comoea.COMOEABuilder;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CONSGAIII;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.COMOEADD;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CONSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.COMOEADDBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.APPROACH;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.SUB_ALGORITHM;

/**
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class COMOEACBICConfiguration implements AlgorithmConfiguration<Solution<?>> {

    private final List<SUB_ALGORITHM> subAlgorithms;
    private final APPROACH approach;

    public COMOEACBICConfiguration(APPROACH approach, SUB_ALGORITHM... subAlgorithms) {
        this.subAlgorithms = Arrays.asList(subAlgorithms);
        this.approach = approach;
    }

    public static CONSGAIII configureCONSGAIII(Problem problem, int popSize) {

        double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 30.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection<>();

        return (CONSGAIII) new CONSGAIIIBuilder(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setUniformWeightFileName("NSGAIII_Weights/W" + problem.getNumberOfObjectives() + "D_" + popSize + ".dat")
                .build();
    }

    public static COMOEADD configureCOMOEADD(Problem problem, int popSize) {
        MutationOperator<DoubleSolution> mutation;
         double crossoverProbability = 1.0;
        double crossoverDistributionIndex = 30.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        return (COMOEADD) new COMOEADDBuilder(problem)
                .setCrossover(crossover)
                .setMutation(mutation)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(2)
                .setNeighborSize(20)
                .setPopulationSize(popSize)
                .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                .setDataDirectory("MOEAD_Weights")
                .build();
    }

    @Override
    public Algorithm cofigure(Problem<Solution<?>> problem, int popSize, int generations) {

        COMOEABuilder builder = new COMOEABuilder<>(problem)
                .setMaxEvaluations(generations * popSize)
                // changed default to 1 after parameter tuning: 
                // COMOEATuningStudy and COMOEATuningStudy2
                .setN(1);

        String app = "SPLIT_ITERATIONS";
        if (approach == APPROACH.SPLIT_POPULATION) {
            popSize = (int) Math.ceil(popSize / 2.0);
            app = "SPLIT_POPULATION";
        }

        String algs = "";
        for (SUB_ALGORITHM alg : subAlgorithms) {
            if (!"".equals(algs)) {
                algs += ",";
            }
            switch (alg) {
                case CONSGAIII:
                    builder.addAlgorithm(configureCONSGAIII(problem, popSize));
                    algs += "NSGAIII";
                    break;
                case COMOEADD:
                    builder.addAlgorithm(configureCOMOEADD(problem, popSize));
                    algs += "MOEADD";
                    break;
            }
        }

        COMOEA algorithm = builder.build();
        algorithm.setName("COMOEACBIC");
        return algorithm;
    }

}
