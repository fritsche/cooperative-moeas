//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package org.uma.jmetal.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

/**
 * Comparison between NSGA-III and MOEA/D-STM
 *
 * * Using NSGA-III methodology - DTLZ1-4 - WFG6-7 - 3, 5, 8, 10 and 15
 * objectives
 *
 * Based on ZDTStudy2: org.uma.jmetal.experiment.ZDTStudy2
 *
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class MOEADSTMStudy {

    private static final int INDEPENDENT_RUNS = 20;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new JMetalException("Needed arguments: experimentBaseDirectory and an integer m");
        }
        String experimentBaseDirectory = args[0];
        int m = Integer.parseInt(args[1]);

        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
        List<Integer> generationsList = new ArrayList<>();

        /**
         * The number of variables are (M+k−1), where M is number of objectives
         * and k = 5 for DTLZ1, while k = 10 for DTLZ2, DTLZ3 and DTLZ4
         */
        int k;

        switch (m) {
            case 3:
                generationsList.add(400); // DTLZ1
                generationsList.add(250); // DTLZ2
                generationsList.add(1000);// DTLZ3
                generationsList.add(600); // DTLZ4
                generationsList.add(400); // WFG6
                generationsList.add(400); // WFG7
                break;
            case 5:
                generationsList.add(600);
                generationsList.add(350);
                generationsList.add(1000);
                generationsList.add(1000);
                generationsList.add(750);
                generationsList.add(750);
                break;
            case 8:
                generationsList.add(750);
                generationsList.add(500);
                generationsList.add(1000);
                generationsList.add(1250);
                generationsList.add(1500);
                generationsList.add(1500);
                break;
            case 10:
                generationsList.add(1000);
                generationsList.add(750);
                generationsList.add(1500);
                generationsList.add(2000);
                generationsList.add(2000);
                generationsList.add(2000);
                break;
            case 15:
                generationsList.add(1500);
                generationsList.add(1000);
                generationsList.add(2000);
                generationsList.add(3000);
                generationsList.add(3000);
                generationsList.add(3000);
                break;
        }

        k = 5; // k = 5 for DTLZ1
        problemList.add(new ExperimentProblem<>(new DTLZ1(m + k - 1, m)));

        k = 10; //  k = 10 for DTLZ2, DTLZ3 and DTLZ4
        problemList.add(new ExperimentProblem<>(new DTLZ2(m + k - 1, m)));
        problemList.add(new ExperimentProblem<>(new DTLZ3(m + k - 1, m)));
        problemList.add(new ExperimentProblem<>(new DTLZ4(m + k - 1, m)));

        /**
         * from the WFG readme file l=20 (distance related) k=4 (position
         * related) if M=2 otherwise k=2*(M-1)
         */
        k = 2 * (m - 1);
        problemList.add(new ExperimentProblem<>(new WFG6(k, 20, m)));
        problemList.add(new ExperimentProblem<>(new WFG7(k, 20, m)));

        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList
                = configureAlgorithmList(problemList, generationsList);

        ExperimentBuilder<DoubleSolution, List<DoubleSolution>> moeadstmStudy = new ExperimentBuilder<>("MOEADSTMStudy" + m + "M");
        moeadstmStudy.setAlgorithmList(algorithmList);
        moeadstmStudy.setProblemList(problemList);
        moeadstmStudy.setExperimentBaseDirectory(experimentBaseDirectory);
        moeadstmStudy.setOutputParetoFrontFileName("FUN");
        moeadstmStudy.setOutputParetoSetFileName("VAR");
        moeadstmStudy.setReferenceFrontDirectory(experimentBaseDirectory + "/referenceFronts");
        moeadstmStudy.setIndicatorList(Arrays.asList(
                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                new PISAHypervolume<DoubleSolution>(),
                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()));
        moeadstmStudy.setIndependentRuns(INDEPENDENT_RUNS);
        moeadstmStudy.setNumberOfCores(Runtime.getRuntime().availableProcessors());
        Experiment<DoubleSolution, List<DoubleSolution>> experiment = moeadstmStudy.build();

        new ExecuteAlgorithms<>(experiment).run();
        new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
        new GenerateLatexTablesWithStatistics(experiment).run();
        new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        new GenerateFriedmanTestTables<>(experiment).run();
        new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();

    }

    /**
     * The algorithm list is composed of pairs
     * {@link Algorithm} + {@link Problem} which form part of a
     * {@link ExperimentAlgorithm}, which is a decorator for class
     * {@link Algorithm}.
     *
     * @param problemList
     * @return
     */
    static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
            List<ExperimentProblem<DoubleSolution>> problemList, List<Integer> generationsList) {
        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

        /**
         * Configure NSGA-III
         */
        for (int i = 0; i < problemList.size(); i++) {

            Problem<DoubleSolution> problem = problemList.get(i).getProblem();

            int D = 300; // default
            int m = problem.getNumberOfObjectives();

            switch (m) {
                case 3:
                    D = 91;
                    break;
                case 5:
                    D = 210;
                    break;
                case 8:
                    D = 156;
                    break;
                case 10:
                    D = 275;
                    break;
                case 15:
                    D = 135;
                    break;
            }

            double crossoverProbability = 0.9;
            double crossoverDistributionIndex = 30.0;
            CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
            MutationOperator<DoubleSolution> mutation;
            SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

            double mutationProbability = 1.0 / problem.getNumberOfVariables();
            double mutationDistributionIndex = 20.0;
            mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

            selection = new BinaryTournamentSelection<>();

            Algorithm<List<DoubleSolution>> algorithm = new NSGAIIIBuilder<>(problem)
                    .setCrossoverOperator(crossover)
                    .setMutationOperator(mutation)
                    .setSelectionOperator(selection)
                    .setMaxIterations(generationsList.get(i))
                    .setUniformWeightFileName("MOEAD_Weights/W" + m + "D_" + D + ".dat")
                    .build();
            algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
        }

        /**
         * Configure MOEA/D-STM
         */
        for (int i = 0; i < problemList.size(); i++) {

            Problem<DoubleSolution> problem = problemList.get(i).getProblem();
            MutationOperator<DoubleSolution> mutation;
            DifferentialEvolutionCrossover crossover;
            Algorithm<List<DoubleSolution>> algorithm;

            int D = 300; // default
            int m = problem.getNumberOfObjectives();

            switch (m) {
                case 3:
                    D = 91;
                    break;
                case 5:
                    D = 210;
                    break;
                case 8:
                    D = 156;
                    break;
                case 10:
                    D = 275;
                    break;
                case 15:
                    D = 135;
                    break;
            }

            double cr = 1.0;
            double f = 0.5;
            crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin");

            double mutationProbability = 1.0 / problem.getNumberOfVariables();
            double mutationDistributionIndex = 20.0;
            mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

            algorithm = new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADSTM)
                    .setCrossover(crossover)
                    .setMutation(mutation)
                    .setMaxEvaluations(generationsList.get(i) * D)
                    .setPopulationSize(D)
                    .setResultPopulationSize(D)
                    .setNeighborhoodSelectionProbability(0.9)
                    .setMaximumNumberOfReplacedSolutions(2)
                    .setNeighborSize(20)
                    .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                    .setDataDirectory("MOEAD_Weights")
                    .build();

            algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));

        }

        return algorithms;
    }

}
