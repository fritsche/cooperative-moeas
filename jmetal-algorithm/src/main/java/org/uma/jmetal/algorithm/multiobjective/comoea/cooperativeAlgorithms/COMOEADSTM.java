package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADSTM;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

public class COMOEADSTM<S extends Solution<?>> extends MOEADSTM implements CooperativeAlgorithm<DoubleSolution> {

    private int generation;
    private final DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public COMOEADSTM(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    }

    @Override
    public void init() {
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        initializeIdealPoint();
        initializeNadirPoint();
        generation = 0;
    }

    @Override
    public List<DoubleSolution> generateOffspring(List<DoubleSolution> offspringPopulation) {
        int[] permutation = new int[populationSize];
        MOEADUtils.randomPermutation(permutation, populationSize);
        offspringPopulation.clear();

        for (int i = 0; i < populationSize; i++) {
            int subProblemId = permutation[i];
//            frequency[subProblemId]++; 

            NeighborType neighborType = chooseNeighborType();
            List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

            differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
            List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

            DoubleSolution child = children.get(0);
            mutationOperator.execute(child);
            problem.evaluate(child);

            evaluations++;

            updateIdealPoint(child);
            updateNadirPoint(child);
            updateNeighborhood(child, subProblemId, neighborType);

            offspringPopulation.add(child);
        }

        return offspringPopulation;
    }

    @Override
    public void updatePopulation(List<DoubleSolution> offspringPopulation) {
        // Combine the parent and the current offspring populations
        jointPopulation.clear();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        // selection process
        stmSelection();

        generation++;
        if (generation % 30 == 0) {
            utilityFunction();
        }
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

}
