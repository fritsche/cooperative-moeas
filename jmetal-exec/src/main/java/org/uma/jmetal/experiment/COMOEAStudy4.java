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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.experiment.methodology.AlgorithmConfiguration;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.SUB_ALGORITHM;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.APPROACH;
import org.uma.jmetal.experiment.methodology.MOEADSTM1Configuration;
import org.uma.jmetal.experiment.methodology.NSGAIIIConfiguration;
import org.uma.jmetal.experiment.methodology.NSGAIIIMethodology;

/**
 * This experiment compares the CoMOEA using two different approaches
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class COMOEAStudy4 {

    public static void main(String[] args) throws IOException {

        String experimentBaseDirectory;
        String experimentName;
        int m;
        if (args.length != 3) {
            System.err.println("WARNING: executing with default values");
            experimentBaseDirectory = "teste";
            experimentName = "COMOEA";
            m = 3;
        } else {
            experimentBaseDirectory = args[0];
            experimentName = args[1];
            m = Integer.parseInt(args[2]);
        }
        List<AlgorithmConfiguration> configurations = new ArrayList<>();
        configurations.add(new NSGAIIIConfiguration());
        configurations.add(new MOEADSTM1Configuration());
        configurations.add(new COMOEAConfiguration(APPROACH.SPLIT_POPULATION,
                SUB_ALGORITHM.COMOEADSTM1,
                SUB_ALGORITHM.CONSGAIII
        ));

        new NSGAIIIMethodology().execute(experimentBaseDirectory, experimentName, m, configurations);

    }
}
