package org.uma.jmetal.algorithm.multiobjective.comoea;

import org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms.CooperativeAlgorithm;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

public class COMOEABuilder<S extends Solution<?>> implements AlgorithmBuilder<COMOEA<S>> {

    private final Problem<S> problem;
    private int maxEvaluations;
    private int N;
    protected List<CooperativeAlgorithm<S>> algorithms;

    public COMOEABuilder(Problem<S> problem) {
        this.problem = problem;
        maxEvaluations = 250 * 91;
        N = 1;
        algorithms = new ArrayList<>();
    }

    public int getN() {
        return N;
    }
    
    public List<CooperativeAlgorithm<S>> getAlgorithms() {
        return algorithms;
    }

    public COMOEABuilder<S> addAlgorithm(CooperativeAlgorithm<S> algorithm) {
        this.algorithms.add(algorithm);
        return this;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public COMOEABuilder<S> setN(int N) {
        this.N = N;
        return this;
    }

    public COMOEABuilder<S> setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    @Override
    public COMOEA<S> build() {
        return new COMOEA<>(this);
    }

}
