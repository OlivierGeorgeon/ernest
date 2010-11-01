import java.util.Collections;

import ga.BasicMutation;
import ga.BasicPopulation;
import ga.BooleanChromosome;
import ga.ChromosomeComparator;
import ga.GeneticAlgorithm;
import ga.IChromosome;
import ga.ICrossover;
import ga.IFitness;
import ga.IMutation;
import ga.IPopulation;
import ga.IPopulationDisplay;
import ga.ISelection;
import ga.PopulationPrinter;
import ga.RouletteWheelSelection;
import ga.SinglePointCrossover;
import XSquared.XSquaredFitness;

public class Main
{
    public static void main(String[] args)
    {
        IPopulationDisplay<Boolean> disp = new PopulationPrinter<Boolean>();
        IFitness<Boolean> fit = new XSquaredFitness<Boolean>();
        IChromosome<Boolean> proto = new BooleanChromosome(5);
        IPopulation<Boolean> pop = new BasicPopulation<Boolean>();
        ISelection<Boolean> sel = 
            new RouletteWheelSelection<Boolean>();
        ICrossover<Boolean> cross = 
            new SinglePointCrossover<Boolean>();
        IMutation<Boolean> mut = new BasicMutation<Boolean>();
        
        GeneticAlgorithm<Boolean> ga = new GeneticAlgorithm<Boolean>();
        ga.run(disp, fit, proto, pop, sel,cross, mut);
        
        Collections.sort(pop.getIndividuals(), 
                new ChromosomeComparator<Boolean>(fit));
        
        System.out.println(pop.getIndividuals().get(0));
    }

}
