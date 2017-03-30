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
package org.uma.jmetal.runner.multiobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.experiment.methodology.AlgorithmConfigurationFactory;
import org.uma.jmetal.problem.multiobjective.UF.UF1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * A parameterized runner: One Algorithm; One Problem; The objective number; The
 * execution index (used as seed);
 *
 * The algorithm and experiment configurations are based on the NSGA-III
 * methodology (Deb and Jain, 2013).
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class ParameterizedRunner<Result> extends ExecuteAlgorithms<Solution<?>, Result> {

    private final int id;

    public ParameterizedRunner(Experiment<Solution<?>, Result> configuration, int id) {
        super(configuration);
        this.id = id;
    }

    public static List<ExperimentProblem<DoubleSolution>> getProblemList(String problem, int m) {
        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
        int k;
        switch (problem) {
            case "DTLZ1":
                k = 5;
                problemList.add(new ExperimentProblem<>(new DTLZ1(m + k - 1, m)));
                break;
            case "DTLZ2":
                k = 10;
                problemList.add(new ExperimentProblem<>(new DTLZ2(m + k - 1, m)));
                break;
            case "DTLZ3":
                k = 10;
                problemList.add(new ExperimentProblem<>(new DTLZ3(m + k - 1, m)));
                break;
            case "DTLZ4":
                k = 10;
                problemList.add(new ExperimentProblem<>(new DTLZ4(m + k - 1, m)));
                break;
            case "WFG6":
                k = 2 * (m - 1);
                problemList.add(new ExperimentProblem<>(new WFG6(k, 20, m)));
                break;
            case "WFG7":
                k = 2 * (m - 1);
                problemList.add(new ExperimentProblem<>(new WFG7(k, 20, m)));
                break;
            case "UF1":
                problemList.add(new ExperimentProblem<>(new UF1()));
            default:
                throw new JMetalException("There is no configurations for " + problem + " problem");
        }
        return problemList;
    }

    public static int getGenerationsNumber(String problem, int m) {

        int generations = 0;

        switch (m) {
            case 3:
                switch (problem) {
                    case "DTLZ1":
                        generations = 400; // DTLZ1
                        break;
                    case "DTLZ2":
                        generations = 250; // DTLZ2
                        break;
                    case "DTLZ3":
                        generations = 1000; // DTLZ3
                        break;
                    case "DTLZ4":
                        generations = 600; // DTLZ4
                        break;
                    case "WFG6":
                        generations = 400; // WFG6
                        break;
                    case "WFG7":
                        generations = 400; // WFG7
                        break;
                }
                break;
            case 5:
                switch (problem) {
                    case "DTLZ1":
                        generations = 600; // DTLZ1
                        break;
                    case "DTLZ2":
                        generations = 350; // DTLZ2
                        break;
                    case "DTLZ3":
                        generations = 1000; // DTLZ3
                        break;
                    case "DTLZ4":
                        generations = 1000; // DTLZ4
                        break;
                    case "WFG6":
                        generations = 750; // WFG6
                        break;
                    case "WFG7":
                        generations = 750; // WFG7
                        break;
                }
                break;
            case 8:
                switch (problem) {
                    case "DTLZ1":
                        generations = 750; // DTLZ1
                        break;
                    case "DTLZ2":
                        generations = 500; // DTLZ2
                        break;
                    case "DTLZ3":
                        generations = 1000; // DTLZ3
                        break;
                    case "DTLZ4":
                        generations = 1250; // DTLZ4
                        break;
                    case "WFG6":
                        generations = 1500; // WFG6
                        break;
                    case "WFG7":
                        generations = 1500; // WFG7
                        break;
                }
                break;
            case 10:
                switch (problem) {
                    case "DTLZ1":
                        generations = 1000;  // DTLZ1
                        break;
                    case "DTLZ2":
                        generations = 750;   // DTLZ2
                        break;
                    case "DTLZ3":
                        generations = 1500;  // DTLZ3
                        break;
                    case "DTLZ4":
                        generations = 2000;  // DTLZ4
                        break;
                    case "WFG6":
                        generations = 2000;  // WFG6
                        break;
                    case "WFG7":
                        generations = 2000;  // WFG7
                        break;
                }
                break;
            case 15:
                switch (problem) {
                    case "DTLZ1":
                        generations = 1500;  // DTLZ1
                        break;
                    case "DTLZ2":
                        generations = 1000;  // DTLZ2
                        break;
                    case "DTLZ3":
                        generations = 2000;  // DTLZ3
                        break;
                    case "DTLZ4":
                        generations = 3000;  // DTLZ4
                        break;
                    case "WFG6":
                        generations = 3000;  // WFG6
                        break;
                    case "WFG7":
                        generations = 3000;  // WFG7
                        break;
                }
                break;
            default:
                throw new JMetalException("There is no configurations for " + m + " objectives");
        }
        return generations;
    }

    public static int getPopSize(int m) {
        int popSize = 0;
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
        return popSize;
    }

    @Override
    public void run() {
        JMetalLogger.logger.info("ExecuteAlgorithms: Preparing output directory");
        super.prepareOutputDirectory();

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
                "" + this.experiment.getNumberOfCores());

        experiment.getAlgorithmList().get(0).runAlgorithm(id, experiment);

    }

    public static void main(String[] args) {
        
        if (args.length != 5) {
            throw new JMetalException("Needed arguments: "
                    + "experimentBaseDirectory algorithm problem m id");
        }

        int i = 0;
        String experimentBaseDirectory = args[i++];
        String algorithm = args[i++];
        String problem = args[i++];
        int m = Integer.parseInt(args[i++]);
        int id = Integer.parseInt(args[i++]);

        JMetalRandom.getInstance().setSeed(id);

        List<ExperimentProblem<DoubleSolution>> problemList = getProblemList(problem, m);
        int generations = getGenerationsNumber(problem, m);
        int popSize = getPopSize(m);

        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

        algorithms.add(new ExperimentAlgorithm<>(AlgorithmConfigurationFactory.getAlgorithmConfiguration(algorithm).cofigure(problemList.get(0).getProblem(), popSize, generations), problemList.get(0).getTag()));

        ExperimentBuilder<DoubleSolution, List<DoubleSolution>> study = new ExperimentBuilder<>(Integer.toString(m));
        study.setAlgorithmList(algorithms);
        study.setProblemList(problemList);
        study.setExperimentBaseDirectory(experimentBaseDirectory);
        study.setOutputParetoFrontFileName("FUN");
        study.setOutputParetoSetFileName("VAR");
        study.setIndependentRuns(1);
        study.setNumberOfCores(1);
        Experiment<DoubleSolution, List<DoubleSolution>> experiment = study.build();

        new ParameterizedRunner(experiment, id).run();

    }

}
