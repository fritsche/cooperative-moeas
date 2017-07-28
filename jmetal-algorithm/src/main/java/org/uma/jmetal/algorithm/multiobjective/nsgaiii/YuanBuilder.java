package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

public class YuanBuilder<S extends Solution<?>> implements AlgorithmBuilder<Yuan<S>> {

    public YuanBuilder() {}

    @Override
    public Yuan<S> build() {
        return new Yuan<>();
    }
}
