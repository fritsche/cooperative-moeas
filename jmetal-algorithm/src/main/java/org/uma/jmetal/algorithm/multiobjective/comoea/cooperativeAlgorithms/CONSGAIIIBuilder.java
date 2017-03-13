package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class CONSGAIIIBuilder extends NSGAIIIBuilder {
    
    public CONSGAIIIBuilder(Problem<Solution<?>> problem) {
        super(problem);
    }
    
    @Override
    public CONSGAIII build(){
        return new CONSGAIII(this);
    }
    
}
