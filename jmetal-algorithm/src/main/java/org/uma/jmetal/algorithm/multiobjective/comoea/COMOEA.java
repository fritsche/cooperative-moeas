package org.uma.jmetal.algorithm.multiobjective.comoea;

import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

public class COMOEA<S extends Solution<?>> implements Algorithm<List<S>> {

    protected String name = "COMOEA";
    
    protected int maxEvaluations;
    protected int N; // number of generations to share information
    protected Problem<S> problem;

    protected List<CooperativeAlgorithm<S>> algorithms;

    public COMOEA(COMOEABuilder builder) {

        this.problem = (builder.getProblem());
        this.maxEvaluations = (builder.getMaxEvaluations());
        this.N = (builder.getN());
        this.algorithms = (builder.getAlgorithms());
    }

    @Override
    public List<S> getResult() {
        // All population joint + remove dominated solutions

        List<S> result = new ArrayList<>();

        algorithms.stream().forEach((co) -> {
            result.addAll(co.getResult());
        });

        return SolutionListUtils.getNondominatedSolutions(result);
    }

    public List<CooperativeAlgorithm<S>> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<CooperativeAlgorithm<S>> algorithms) {
        this.algorithms = algorithms;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    public int getN() {
        return N;
    }

    public void setN(int N) {
        this.N = N;
    }

    public void setProblem(Problem<S> problem) {
        this.problem = problem;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getDescription() {
        return "Coopeartive Multi-objective Evolutionary Algorithms";
    }

    @Override
    public void run() {
        List<List<S>> offspringPopulation = new ArrayList<>();

        int inc = 0;
        for (CooperativeAlgorithm co : algorithms) {
            co.init();
            offspringPopulation.add(new ArrayList<>());
            inc += co.getPopulationSize();
        }

        // count initialization as one iteration
        for (int fe = inc, it = 1; fe < maxEvaluations; fe += inc, ++it) {

            for (int alg = 0; alg < algorithms.size(); ++alg) {
                offspringPopulation.set(alg, algorithms.get(alg).generateOffspring(offspringPopulation.get(alg)));
            }

            if (it % N == 0) {
                List<S> joint = new ArrayList<>();
                offspringPopulation.stream().forEach((offspring) -> {
                    joint.addAll(offspring);
                });
                for (int alg = 0; alg < algorithms.size(); ++alg) {
                    List<S> aux = new ArrayList<>();
                    joint.forEach((s) -> {
                        aux.add((S) s.copy());
                    });
                    offspringPopulation.set(alg, aux);
                }
            }

            for (int alg = 0; alg < algorithms.size(); ++alg) {
                algorithms.get(alg).updatePopulation(offspringPopulation.get(alg));
            }
        }

    }

}
