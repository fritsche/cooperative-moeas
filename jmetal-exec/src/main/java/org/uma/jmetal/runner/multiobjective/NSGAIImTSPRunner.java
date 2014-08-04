//  NSGAII_MOTSP_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverFactory;
import org.uma.jmetal.operator.mutation.MutationFactory;
import org.uma.jmetal.operator.selection.SelectionFactory;
import org.uma.jmetal.problem.MultiObjectiveTSP;
import org.uma.jmetal.qualityindicator.QualityIndicatorGetter;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

import java.util.HashMap;
import java.util.logging.FileHandler;

/**
 * Class to configure and execute the NSGA-II algorithm. The experiment.settings are aimed
 * at solving the mTSP problem.
 */

public class NSGAIImTSPRunner {
  public static java.util.logging.Logger logger_;
  public static FileHandler fileHandler_; 

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException Usage:
   *                           - org.uma.jmetal.metaheuristic.multiobjective.nsgaII.NSGAII_mTSP_main
   */
  public static void main(String[] args) throws Exception {
    Problem problem; 
    Algorithm algorithm; 
    Operator crossover; 
    Operator mutation; 
    Operator selection; 

    QualityIndicatorGetter indicators;

    // Logger object and file to store log messages
    logger_ = JMetalLogger.logger;
    fileHandler_ = new FileHandler("NSGAII_main.log");
    logger_.addHandler(fileHandler_);

    indicators = null;
    problem = new MultiObjectiveTSP("Permutation", "kroA100.tsp", "kroB100.tsp");

    SolutionSetEvaluator evaluator = new SequentialSolutionSetEvaluator();
    //SolutionSetEvaluator executor = new MultithreadedSolutionSetEvaluator(4, problem) ;
    algorithm = new NSGAII(evaluator);
    algorithm.setProblem(problem);

    // Algorithm parameters
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 1000000);
    
    /* Crossover operator */
    HashMap<String, Object> crossoverParameters = new HashMap<String, Object>();
    crossoverParameters.put("probability", 0.95);
    //crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", parameters);
    crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover", crossoverParameters);
    
    /* Mutation operator */
    HashMap<String, Object> mutationParameters = new HashMap<String, Object>();
    mutationParameters.put("probability", 0.2);
    mutation = MutationFactory.getMutationOperator("SwapMutation", mutationParameters);                    
  
    /* Selection Operator */
    HashMap<String, Object> selectionParameters = new HashMap<String, Object>() ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", selectionParameters);

    // Add the operator to the algorithm
    algorithm.addOperator("crossover", crossover);
    algorithm.addOperator("mutation", mutation);
    algorithm.addOperator("selection", selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators);

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages 
    logger_.info("Total execution time: " + estimatedTime + "ms");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
  } 
} 
