package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

public class COMOEADDBuilder extends MOEADBuilder{
    
    public COMOEADDBuilder(Problem<DoubleSolution> problem) {
        super(problem, null);
    }
    
    @Override
    public COMOEADD build(){
        return new COMOEADD(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }
    
}
