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
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.IslandAlgorithm;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.algorithm.multiobjective.dea.islandAlgorithms.MOEADDIsland;
import org.uma.jmetal.solution.Solution;

/**
 * Generic island that executes an IslandAlgorithm. The generic island is
 * responsible for handling concurrency.
 *
 * @author Gian Fritsche <gmfritsche@inf.ufpr.br>
 * @param <S>
 */
public class Island<S extends Solution<?>> implements Runnable {

    // the thread name
    private String threadName;
    // the buffer of Solutions received from other islands
    private final ConcurrentLinkedQueue<S> buffer;

    private final int bufferLimit;
    // the list of neighbor to send information to
    private final List<Island> neighbors;
    // the algorithm to be executed by the island
    private final IslandAlgorithm algorithm;

    private boolean acceptingMigrants = true;

    private CyclicBarrier barrier = null;

    public Island(IslandAlgorithm algorithm, int bufferLimit) {
        this.algorithm = algorithm;
        this.buffer = new ConcurrentLinkedQueue<>();
        this.neighbors = new ArrayList<>();
        this.bufferLimit = bufferLimit;
    }

    public void setBarrier(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    public void await() {
        if (barrier != null) {
            try {
//                JMetalLogger.logger.log(Level.INFO, "island waiting: {0}", barrier.getNumberWaiting());
                int index = barrier.await();
//                JMetalLogger.logger.log(Level.INFO, "island executing: {0}", index);
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(MOEADDIsland.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isAcceptingMigrants() {
        return acceptingMigrants;
    }

    public void setAcceptingMigrants(boolean acceptingMigrants) {
        this.acceptingMigrants = acceptingMigrants;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * This method return the next migrant solution from the queue. To get all
     * buffer use getNext until the value is null.
     *
     * @return and removes the migrant solution that has been on the buffer the
     * longest time, or returns null if this queue is empty.
     */
    public S getNextMigrant() {
        return buffer.poll();
    }

    public List<S> getMigrantQueue() {
        List<S> list = new ArrayList<>();
        S aux = getNextMigrant();
        for (int i = 0; i < bufferLimit && aux != null; ++i) {
            list.add(aux);
            aux = getNextMigrant();
        }
        return list;
    }

    /**
     * Adds the migrant solution from other islands to a queue buffer on this
     * island.
     *
     * @param migrant solution from other island
     */
    public void migrateSolution(S migrant) {
        if (isAcceptingMigrants()) {
            if (buffer.size() + 1 > bufferLimit) { // if the queueLimit will be exceeded
                S aux = buffer.peek(); // get the head
                buffer.offer((S) migrant.copy()); // add to tail
                buffer.remove(aux); // remove the head if it still exists
            } else {
                buffer.offer((S) migrant.copy());
            }
        }
//        else { JMetalLogger.logger.log(Level.WARNING, "buffer size: {0}", buffer.size()); }
    }

    /**
     * Share a list of solutions from this island to all neighbor islands.
     *
     * @param solutions to be sent
     */
    public void sendSolutions(List<S> solutions) {
        neighbors.forEach((neighbor) -> {
            solutions.forEach((s) -> {
                neighbor.migrateSolution(s);
            });
        });
//        JMetalLogger.logger.log(Level.WARNING, "buffer size: {0}", neighbors.get(0).buffer.size());         
    }

    public void addNeighbor(Island neighbor) {
        this.neighbors.add(neighbor);
    }

    public IslandAlgorithm<S> getAlgorithm() {
        return algorithm;
    }

    @Override
    public void run() {
        /**
         * WARNING: To launch a thread use the method: public void start();
         */

        /**
         * The algorithm may or may not use the sendSolutions and getNextMigrant
         * methods. To use the methods, it needs access to the island object.
         */
        algorithm.run();
    }

}
