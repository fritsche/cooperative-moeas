package org.uma.jmetal.runner.multiobjective;

import java.util.List;
import java.util.logging.Level;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.comoea.COMOEABuilder;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.COMOEADSTM;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.COMOEADSTMBuilder;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CONSGAIII;
import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CONSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class COMOEARunner extends AbstractAlgorithmRunner {

    public static CONSGAIII configureCONSGAIII(Problem problem) {

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 30.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        selection = new BinaryTournamentSelection<>();

        return (CONSGAIII) new CONSGAIIIBuilder(problem)
                .setCrossoverOperator(crossover)
                .setMutationOperator(mutation)
                .setSelectionOperator(selection)
                .setUniformWeightFileName("MOEAD_Weights/W" + problem.getNumberOfObjectives() + "D_" + problem.getNumberOfVariables() + ".dat")
                .build();
    }

    private static COMOEADSTM configureCOMOEADSTM(Problem problem) {
        MutationOperator<DoubleSolution> mutation;
        DifferentialEvolutionCrossover crossover;
        double cr = 1.0;
        double f = 0.5;
        crossover = new DifferentialEvolutionCrossover(cr, f, "rand/1/bin");

        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        return (COMOEADSTM) new COMOEADSTMBuilder(problem)
                .setCrossover(crossover)
                .setMutation(mutation)
                .setNeighborhoodSelectionProbability(0.9)
                .setMaximumNumberOfReplacedSolutions(2)
                .setNeighborSize(20)
                .setPopulationSize(91)
                .setFunctionType(AbstractMOEAD.FunctionType.TCHE)
                .setDataDirectory("MOEAD_Weights")
                .build();
    }

    public static void main(String[] args) {

        Algorithm<List<DoubleSolution>> algorithm;
        int D = 91, m = 3;
        Problem problem = new DTLZ1(D, m);

        algorithm = new COMOEABuilder<>(problem)
                .setMaxIterations(500) // the COMOEA is responsible for sharing the maxIterations among the algorithms
                .setN(5)
                .addAlgorithm(configureCONSGAIII(problem))
                .addAlgorithm(configureCOMOEADSTM(problem))
                .build();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        List<DoubleSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        new SolutionListOutput(population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                .print();

        JMetalLogger.logger.log(Level.INFO, "Total execution time: {0}ms", computingTime);
        JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
        JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    }

}
