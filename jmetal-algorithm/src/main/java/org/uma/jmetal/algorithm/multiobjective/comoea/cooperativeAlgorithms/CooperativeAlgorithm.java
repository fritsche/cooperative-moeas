package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

public interface CooperativeAlgorithm<S extends Solution<?>> extends Algorithm<List<S>> {

    // initialize the specific characteristics of the algorithm
    // may be empty
    public void init();

    // COMOEA has the responsability of setting the population size of the sub-algorithms
    public void setPopulationSize(int popSize);

    public List<S> generateOffspring(List<S> offspringPopulation);

    public void updatePopulation(List<S> offspringPopulation);
}
