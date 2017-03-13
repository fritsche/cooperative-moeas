package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;

public class COMOEADSTMBuilder extends MOEADBuilder{
    
    public COMOEADSTMBuilder(Problem<DoubleSolution> problem) {
        super(problem, null);
    }
    
    @Override
    public COMOEADSTM build(){
        return new COMOEADSTM(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }
    
}
