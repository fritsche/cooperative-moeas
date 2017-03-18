package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

public class COMOEADSTM1Builder extends MOEADBuilder{
    
    public COMOEADSTM1Builder(Problem<DoubleSolution> problem) {
        super(problem, null);
    }
    
    @Override
    public COMOEADSTM1 build(){
        return new COMOEADSTM1(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }
    
}
