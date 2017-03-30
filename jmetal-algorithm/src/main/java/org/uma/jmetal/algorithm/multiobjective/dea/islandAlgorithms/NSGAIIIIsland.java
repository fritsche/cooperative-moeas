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
import java.util.logging.Level;
import org.uma.jmetal.algorithm.multiobjective.dea.Island;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class NSGAIIIIsland<S extends Solution<?>> extends NSGAIII<S> implements IslandAlgorithm<S> {

    private Island island;
    private int migrationFrequency;
    private List<S> offspringPopulation;
    private List<S> matingPopulation;

    public NSGAIIIIsland(NSGAIIIIslandBuilder builder) {
        super(builder);
        this.migrationFrequency = builder.getMigrationFrequency();
    }

    @Override
    public void setIsland(Island island) {
        this.island = island;
    }

    @Override
    public List<S> selectionPolicy() {
        JMetalLogger.logger.log(Level.INFO, "sent migrants: {0}", offspringPopulation.size());
        return offspringPopulation;
    }

    @Override
    public void replacementPolicy() {
        List<S> migrants = island.getMigrantQueue();
        JMetalLogger.logger.log(Level.INFO, "received migrants: {0}", migrants.size());
        setPopulation(replacement(getPopulation(), migrants));
    }

    @Override
    public void run() {

        setPopulation(createInitialPopulation());
        setPopulation(evaluatePopulation(getPopulation()));
        initProgress();
        while (!isStoppingConditionReached()) {
            matingPopulation = selection(getPopulation());
            offspringPopulation = reproduction(matingPopulation);
            offspringPopulation = evaluatePopulation(offspringPopulation);
            setPopulation(replacement(getPopulation(), offspringPopulation));

            if (iterations % migrationFrequency == 0) {
                // send solutions
                island.sendSolutions(selectionPolicy());
                // receive solutions
                replacementPolicy();
            }

            updateProgress();
        }
    }
}
