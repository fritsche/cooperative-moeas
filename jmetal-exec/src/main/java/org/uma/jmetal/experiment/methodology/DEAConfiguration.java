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
package org.uma.jmetal.experiment.methodology;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class DEAConfiguration implements AlgorithmConfiguration {

    public DEAConfiguration() {
    }

    @Override
    public Algorithm cofigure(Problem problem, int popSize, int generations) {

        /**
         * 1. Build each island; 2. Set the neighborhood; 3. Add the islands to
         * the dEA algorithm;
         */
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
