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
package org.uma.jmetal.algorithm.multiobjective.dea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;

/**
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 */
public class DEA<S extends Solution<?>> implements Algorithm<List<S>> {

    private String name;
    private final List<Island> islands;

    public DEA(DEABuilder builder) {
        islands = builder.getIslands();
    }

    @Override
    public void run() {

        List<Thread> threads = new ArrayList<>();

        // create the threads
        islands.forEach((island) -> {
            threads.add(new Thread(island));
        });

        // start the threads
        threads.forEach((thread) -> {
            thread.start();
        });

        // wait them to finish
        threads.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(DEA.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return (name == null || name.equals("")) ? "dEA" : name;
    }

    @Override
    public String getDescription() {
        return "Distributed Evolutionary Algorithm";
    }

    @Override
    public List<S> getResult() {
        List<S> result = new ArrayList<>();

        islands.forEach((island) -> {
            result.addAll((Collection<? extends S>) island.getAlgorithm().getResult());
        });

        return SolutionListUtils.getNondominatedSolutions(result);
    }

}
