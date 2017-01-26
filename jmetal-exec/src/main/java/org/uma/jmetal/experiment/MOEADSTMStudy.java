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
import java.util.List;
import org.uma.jmetal.experiment.methodology.AlgorithmConfiguration;
import org.uma.jmetal.experiment.methodology.MOEADSTMConfiguration;
import org.uma.jmetal.experiment.methodology.NSGAIIIConfiguration;
import org.uma.jmetal.experiment.methodology.NSGAIIIMethodology;
import org.uma.jmetal.util.JMetalException;

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

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new JMetalException("Needed arguments: experimentBaseDirectory and an integer m");
        }
        String experimentBaseDirectory = args[0];
        int m = Integer.parseInt(args[1]);

        List<AlgorithmConfiguration> configurations = new ArrayList<>();
        configurations.add(new NSGAIIIConfiguration());
        configurations.add(new MOEADSTMConfiguration());

        new NSGAIIIMethodology().execute(experimentBaseDirectory, experimentBaseDirectory, m, configurations);
    }

}
