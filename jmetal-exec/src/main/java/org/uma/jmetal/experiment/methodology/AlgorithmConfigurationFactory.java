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

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.APPROACH;
import org.uma.jmetal.experiment.methodology.COMOEAConfiguration.SUB_ALGORITHM;

/**
 *
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class AlgorithmConfigurationFactory {

    public static AlgorithmConfiguration getAlgorithmConfiguration(String algorithm) {
        switch (algorithm) {
            case "NSGAIII":
                return new NSGAIIIConfiguration();
            case "MOEADD":
                return new MOEADDConfiguration();
            case "MOEADSTM":
                return new MOEADSTMConfiguration();
            case "COMOEA-MOEADSTM,NSGAIII":
                return new COMOEAConfiguration(APPROACH.SPLIT_POPULATION, SUB_ALGORITHM.COMOEADSTM, SUB_ALGORITHM.CONSGAIII);
            case "MOEADSTM1":
                return new MOEADSTM1Configuration();
            case "COMOEA-MOEADSTM1,NSGAIII":
                return new COMOEAConfiguration(APPROACH.SPLIT_POPULATION, SUB_ALGORITHM.COMOEADSTM1, SUB_ALGORITHM.CONSGAIII);
            case "dEA":
                return new DEAConfiguration();
            default:
                throw new JMetalException("There is no configurations for " + algorithm + " algorithm");
        }
    }

}
