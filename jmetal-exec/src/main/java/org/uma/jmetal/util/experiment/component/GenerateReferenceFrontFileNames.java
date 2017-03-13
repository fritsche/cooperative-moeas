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
package org.uma.jmetal.util.experiment.component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class GenerateReferenceFrontFileNames implements ExperimentComponent {

    private final Experiment<?, ?> experiment;
    private final int m;

    public GenerateReferenceFrontFileNames(Experiment<?, ?> experimentConfiguration, int m) {
        this.experiment = experimentConfiguration;
        this.m = m;
    }

    @Override
    public void run() throws IOException {
        List<String> referenceFrontFileNames = new LinkedList<>();
        experiment.getProblemList().forEach((problem) -> {
            referenceFrontFileNames.add(problem.getTag() + "_" + m + ".ref");
        });
        experiment.setReferenceFrontFileNames(referenceFrontFileNames);
    }

}
