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
import org.uma.jmetal.experiment.methodology.MOEADSTMConfiguration;
import org.uma.jmetal.experiment.methodology.NSGAIIIMethodology;
import org.uma.jmetal.util.JMetalException;

/**
 * This experiment compares the MOEA/D-STM with CoMOEA/D-STM. The objective is to
 * check if they are equivalent (as they should).
 * 
 * @author Gian M. Fritsche <gmfritsche@inf.ufpr.br>
 */
public class COMOEADSTMStudy {
        public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            throw new JMetalException("Needed arguments: experimentBaseDirectory experimentName m");
        }
        String experimentBaseDirectory = args[0];
        String experimentName = args[1];
        int m = Integer.parseInt(args[2]);

        List<AlgorithmConfiguration> configurations = new ArrayList<>();
        configurations.add(new MOEADSTMConfiguration());
        configurations.add(new COMOEAConfiguration(COMOEAConfiguration.SUB_ALGORITHM.COMOEADSTM));
        
        new NSGAIIIMethodology().execute(experimentBaseDirectory, experimentName, m, configurations);
        
    }
}
