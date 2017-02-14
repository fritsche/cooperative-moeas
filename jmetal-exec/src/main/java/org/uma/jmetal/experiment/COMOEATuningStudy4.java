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
package org.uma.jmetal.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.comoea.COMOEA;
import org.uma.jmetal.algorithm.multiobjective.comoea.COMOEABuilder;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
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
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

/**
 * Tuning of COMOEA parameter N. Problems: WFG7 and DTLZ2; N = { 10 20 50 }; M =
 * { 3 5 8 10 15}
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class COMOEATuningStudy4 {

    private static final int INDEPENDENT_RUNS = 20;

    private static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(List<ExperimentProblem<DoubleSolution>> problemList, List<Integer> generationsList) {

        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

        int N[] = {1, 5, 10, 20, 50};
                
        // SPLIT_POPULATION
        for (int n : N) {
            for (int i = 0; i < problemList.size(); i++) {

                Problem<DoubleSolution> problem = problemList.get(i).getProblem();

                int popSize = 300; // default
                int m = problem.getNumberOfObjectives();

                switch (m) {
                    case 3:
                        popSize = 91;
                        break;
                    case 5:
                        popSize = 210;
                        break;
                    case 8:
                        popSize = 156;
                        break;
                    case 10:
                        popSize = 275;
                        break;
                    case 15:
                        popSize = 135;
                        break;
                }

                COMOEABuilder builder = new COMOEABuilder<>(problem)
                        .setMaxEvaluations(generationsList.get(i) * popSize)
                        .setN(n);
                popSize = (int) Math.ceil(popSize / 2.0);
                builder.addAlgorithm(COMOEAConfiguration.configureCONSGAIII(problem, popSize));
                builder.addAlgorithm(COMOEAConfiguration.configureCOMOEADSTM(problem, popSize));
                COMOEA algorithm = builder.build();
                algorithm.setName("POPN" + n);

                algorithms.add(
                        new ExperimentAlgorithm<>(
                                algorithm,
                                problemList.get(i).getTag())
                );
            }
        }

        return algorithms;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            throw new JMetalException("Needed arguments: experimentBaseDirectory experimentName m");
        }
        String experimentBaseDirectory = args[0];
        String experimentName = args[1];
        int m = Integer.parseInt(args[2]);

        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
        List<Integer> generationsList = new ArrayList<>();

        /**
         * The number of variables are (M+k−1), where M is number of objectives
         * and k = 5 for DTLZ1, while k = 10 for DTLZ2, DTLZ3 and DTLZ4
         */
        int k;

        switch (m) {
            case 3:
                generationsList.add(250); // DTLZ2
                generationsList.add(400); // WFG7
                break;
            case 5:
                generationsList.add(350);
                generationsList.add(750);
                break;
            case 8:
                generationsList.add(500);
                generationsList.add(1500);
                break;
            case 10:
                generationsList.add(750);
                generationsList.add(2000);
                break;
            case 15:
                generationsList.add(1000);
                generationsList.add(3000);
                break;
        }

        k = 10; //  k = 10 for DTLZ2, DTLZ3 and DTLZ4
        problemList.add(new ExperimentProblem<>(new DTLZ2(m + k - 1, m)));

        /**
         * from the WFG readme file l=20 (distance related) k=4 (position
         * related) if M=2 otherwise k=2*(M-1)
         */
        k = 2 * (m - 1);
        problemList.add(new ExperimentProblem<>(new WFG7(k, 20, m)));
        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList
                = configureAlgorithmList(problemList, generationsList);

        ExperimentBuilder<DoubleSolution, List<DoubleSolution>> study = new ExperimentBuilder<>(experimentName + File.separator + m);
        study.setAlgorithmList(algorithmList);
        study.setProblemList(problemList);
        study.setExperimentBaseDirectory(experimentBaseDirectory);
        study.setOutputParetoFrontFileName("FUN");
        study.setOutputParetoSetFileName("VAR");
        study.setReferenceFrontDirectory(experimentBaseDirectory + File.separator + experimentName + File.separator + m + "/referenceFronts");
        if (m < 8) {
            study.setIndicatorList(Arrays.asList(
                    new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(), new PISAHypervolume<DoubleSolution>(), new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()));
        } else {
            study.setIndicatorList(Arrays.asList(
                    new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()));
        }
        study.setIndependentRuns(INDEPENDENT_RUNS);
        study.setNumberOfCores(Runtime.getRuntime().availableProcessors());
        Experiment<DoubleSolution, List<DoubleSolution>> experiment = study.build();

        new ExecuteAlgorithms<>(experiment).run();
        new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
    }

}
