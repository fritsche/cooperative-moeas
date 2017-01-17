package org.uma.jmetal.algorithm.multiobjective.comoea;

import java.util.List;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class COMOEA<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {

    public COMOEA(Problem<S> problem) {
        super(problem);
    }

    @Override
    protected void initProgress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateProgress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean isStoppingConditionReached() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        List<List<S>> offspringPopulation;

        // foreach algorithm in cooperativeAlgorithmList
            this.setPopulation(createInitialPopulation());
            this.evaluatePopulation(this.getPopulation());
        //
        initProgress();
        while (!isStoppingConditionReached()) {
            // foreach algorithm in cooperativeAlgorithmList
                // offspringPopulationList[algorithm] = generateOffspring (algorithm.getPopulation());
            //
            
            // foreach algorithm in cooperativeAlgorithmList
                // algorithm.updatePopulation(offspringPopulationList);
            //
            
            updateProgress();
        }
    }
    
}

