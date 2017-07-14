package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;

public class COMOEADD<S extends Solution<?>> extends MOEADD<DoubleSolution> implements CooperativeAlgorithm<DoubleSolution> {

    public COMOEADD(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, CrossoverOperator crossoverOperator, MutationOperator mutation, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossoverOperator, mutation, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }

    @Override
    public void init() {

        population = new ArrayList(populationSize);

//        rankSolution = new HashMap<>();
//        associateDistSolution = new HashMap();
//        regionSolution = new HashMap();

        neighborhood = new int[populationSize][neighborSize];
        lambda = new double[populationSize][problem.getNumberOfObjectives()];

        idealPoint = new double[problem.getNumberOfObjectives()]; // ideal point for Pareto-based population
        nadirPoint = new double[problem.getNumberOfObjectives()]; // nadir point for Pareto-based population

        rankIdx = new int[populationSize][populationSize];
        subregionIdx = new int[populationSize][populationSize];
        subregionDist = new double[populationSize][populationSize];

        // STEP 1. Initialization
        initializeUniformWeight();
        initializeNeighborhood();
        initPopulation();
        initializeIdealPoint();
        initializeNadirPoint();

        // initialize the distance
        for (int i = 0; i < populationSize; i++) {
            double distance = calculateDistance2(population.get(i), lambda[i], idealPoint, nadirPoint);
            subregionDist[i][i] = distance;
        }

       ranking = computeRanking(population);
        for (int curRank = 0; curRank < ranking.getNumberOfSubfronts(); curRank++) {
            List<Solution> front = ranking.getSubfront(curRank);
            for (Solution s : front) {
                int position = this.population.indexOf(s);
                rankIdx[curRank][position] = 1;
            }
        }

    }

    @Override
    public List<DoubleSolution> generateOffspring(List<DoubleSolution> offspringPopulation) {

        evaluations = 0;
        maxEvaluations = populationSize; // to mimic one iteration
        offspringPopulation.clear();
        do {
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int cid = permutation[i];
                int type;
                double rnd = randomGenerator.nextDouble();

                // mating selection style
                if (rnd < neighborhoodSelectionProbability) {
                    type = 1; // neighborhood
                } else {
                    type = 2; // whole population
                }
                DoubleSolution[] parentvect;
                parentvect = matingSelection(cid, type);
                ArrayList<DoubleSolution> parents = new ArrayList<>(Arrays.asList(parentvect));

                List<DoubleSolution> children = crossoverOperator.execute(parents);

                DoubleSolution child = children.get(0);
                mutationOperator.execute(child);
                problem.evaluate(child);

                evaluations++;

                updateIdealPoint(child);
                updateNadirPoint(child);
                updateArchive(child);
                offspringPopulation.add(child);
            } // for
        } while (evaluations < maxEvaluations);

        return offspringPopulation;
    }

    @Override
    public void updatePopulation(List<DoubleSolution> offspringPopulation) {
        for (DoubleSolution solution : offspringPopulation) {
            updateIdealPoint(solution);
            updateNadirPoint(solution);
            updateArchive(solution);
        }
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

}
