package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

@SuppressWarnings("serial")
public class Yuan<S extends Solution<?>> implements Algorithm<List<S>> {

    protected List<S> population;

    public List<S> getPopulation() {
        return population;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("This wrapper is not intended to be executed."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }
    
    protected List<S> getNonDominatedSolutions(List<S> solutionList) {
        return SolutionListUtils.getNondominatedSolutions(solutionList);
    }

    @Override
    public String getName() {
        return "Yuan";
    }

    @Override
    public String getDescription() {
        return "Wrapper to Yuan NSGAIII";
    }


}
