package org.uma.jmetal.algorithm.multiobjective.dea;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

public class DEABuilder<S extends Solution<?>> implements AlgorithmBuilder<DEA<S>> {

    protected List<Island<S>> islands;

    public DEABuilder() {
        islands = new ArrayList<>();
    }

    public DEABuilder<S> addIsland(Island<S> island) {
        this.islands.add(island);
        return this;
    }

    @Override
    public DEA<S> build() {
        return new DEA(this);
    }

    List<Island<S>> getIslands() {
        return islands;
    }

}
