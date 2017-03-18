package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADSTM1;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

public class COMOEADSTM1<S extends Solution<?>> extends MOEADSTM1 implements CooperativeAlgorithm<DoubleSolution> {

    private int generation;
    private final DifferentialEvolutionCrossover differentialEvolutionCrossover;

    public COMOEADSTM1(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, AbstractMOEAD.FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
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

        List<Integer> permutation = tourSelection(10);
        offspringPopulation.clear();

        for (int i = 0; i < permutation.size(); i++) {
            int subProblemId = permutation.get(i);
            frequency[subProblemId]++;

            AbstractMOEAD.NeighborType neighborType = chooseNeighborType();
            List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

            differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
            List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

            DoubleSolution child = children.get(0);
            mutationOperator.execute(child);
            problem.evaluate(child);

            evaluations++;

            updateIdealPoint(child);
            updateNadirPoint((S) child);
            updateNeighborhood(child, subProblemId, neighborType);

            offspringPopulation.add(child);
        }

        return offspringPopulation;
    } // update the current nadir point

    protected void updateNadirPoint(S individual) {
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            if (individual.getObjective(i) > nadirPoint[i]) {
                nadirPoint[i] = individual.getObjective(i);
            }
        }
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
    public List<Integer> tourSelection(int depth) {
        List<Integer> selected = new ArrayList<>();
        List<Integer> candidate = new ArrayList<>();

        for (int n = 0; n < populationSize; n++) {
            candidate.add(n);
        }

        while (selected.size() < (int) (populationSize)) {
            int best_idd = (int) (randomGenerator.nextDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (randomGenerator.nextDouble() * candidate.size());
                s2 = candidate.get(i2);
                if (utility[s2] > utility[best_sub]) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

}
