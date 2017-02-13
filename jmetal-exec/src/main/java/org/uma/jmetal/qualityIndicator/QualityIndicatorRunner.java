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
package org.uma.jmetal.qualityIndicator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.experiment.methodology.AlgorithmConfigurationFactory;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.runner.multiobjective.ParameterizedRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

/**
 * Needed arguments. experimentBaseDirectory indicator problem m algorithm1 ...
 * algorithmN
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class QualityIndicatorRunner {

    private static final int INDEPENDENT_RUNS = 20;

    private static List<GenericIndicator<DoubleSolution>> getIndicatorList(String indicator) {
        switch (indicator) {
            case "HV":
                return Arrays.asList(new PISAHypervolume<DoubleSolution>());
            case "IGD":
                return Arrays.asList(new InvertedGenerationalDistance<DoubleSolution>());
            case "IGD+":
                return Arrays.asList(new InvertedGenerationalDistancePlus<DoubleSolution>());
            case "GD":
                return Arrays.asList(new GenerationalDistance<DoubleSolution>());
            case "EP":
                return Arrays.asList(new Epsilon<DoubleSolution>());
            default:
                throw new JMetalException("There is no configurations for " + indicator + " indicator");
        }
    }

    public static void main(String[] args) throws IOException {

        int i = 0;
        String experimentBaseDirectory = args[i++];
        String indicator = args[i++];
        String problem = args[i++];
        int m = Integer.parseInt(args[i++]);

        List<ExperimentProblem<DoubleSolution>> problemList = ParameterizedRunner.getProblemList(problem, m);
        int generations = ParameterizedRunner.getGenerationsNumber(problem, m);
        int popSize = ParameterizedRunner.getPopSize(m);

        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList = new ArrayList<>();

        for (; i < args.length; i++) {
            algorithmList.add(new ExperimentAlgorithm<>(
                    AlgorithmConfigurationFactory.getAlgorithmConfiguration(args[i])
                    .cofigure(problemList.get(0).getProblem(), popSize, generations),
                    problemList.get(0).getTag()));
        }

        ExperimentBuilder<DoubleSolution, List<DoubleSolution>> study = new ExperimentBuilder<>(Integer.toString(m));
        study.setAlgorithmList(algorithmList);
        study.setProblemList(problemList);
        study.setExperimentBaseDirectory(experimentBaseDirectory);
        study.setOutputParetoFrontFileName("FUN");
        study.setOutputParetoSetFileName("VAR");
        study.setReferenceFrontDirectory(experimentBaseDirectory + File.separator + m + "/referenceFronts");
        study.setIndicatorList(getIndicatorList(indicator));
        study.setIndependentRuns(INDEPENDENT_RUNS);
        study.setNumberOfCores(Runtime.getRuntime().availableProcessors());
        Experiment<DoubleSolution, List<DoubleSolution>> experiment = study.build();
        new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
        new ComputeQualityIndicators<>(experiment).run();
    }

}
