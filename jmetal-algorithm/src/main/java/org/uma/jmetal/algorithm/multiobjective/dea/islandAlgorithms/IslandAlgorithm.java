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
package org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.solution.Solution;

/**
 * The IslandAlgorithm is executed by an Island. But it needs to known the
 * island to use the methods sendSolutions and getNextMigrant
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public interface IslandAlgorithm<S extends Solution<?>> extends Algorithm<List<S>> {

    public void setIsland(Island island);

    /**
     * Selection policy on the source island. Select the solutions to send.
     *
     * @return the set of selected solutions
     */
    public List<S> selectionPolicy();

    /**
     * Reads the buffer and select which solution to accept and which solution
     * from the current population to discard to keep the population size.
     */
    public void replacementPolicy();
}
