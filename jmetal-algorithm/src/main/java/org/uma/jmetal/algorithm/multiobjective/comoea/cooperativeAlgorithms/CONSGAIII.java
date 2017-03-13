package org.uma.jmetal.algorithm.multiobjective.comoea.cooperativeAlgorithms;

import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.solution.Solution;

public class CONSGAIII<S extends Solution<?>> extends NSGAIII<S> implements CooperativeAlgorithm<S> {

    public CONSGAIII(NSGAIIIBuilder builder) {
        super(builder);
    }

    @Override
    public void init() {
        setPopulation(evaluatePopulation(createInitialPopulation()));
    }

    @Override
    public List<S> generateOffspring(List<S> offspringPopulation) {
        List<S> matingPopulation;
        matingPopulation = selection(getPopulation());
        offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        return offspringPopulation;
    }

    @Override
    public void updatePopulation(List<S> offspringPopulation) {
        setPopulation(replacement(getPopulation(), offspringPopulation));
    }

    @Override
    public int getPopulationSize() {
        return getMaxPopulationSize();
    }

}
