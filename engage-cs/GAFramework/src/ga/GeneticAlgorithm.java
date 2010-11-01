package ga;

public class GeneticAlgorithm<Type>
{
    public GeneticAlgorithm()
    {}
    
    public void run(
            IPopulationDisplay<Type> disp,
            IFitness<Type> fit,
            IChromosome<Type> proto,
            IPopulation<Type> pop,
            ISelection<Type> sel,
            ICrossover<Type> cross,
            IMutation<Type> mut
            )
    {
        pop.intialize(100, proto);
        
        disp.display(pop);
        
        while (!pop.hasConverged(fit))
        {
            pop = sel.select(fit, pop);
            disp.display(pop);
            
            cross.setCrossoverRate(0.80);
            pop = cross.crossover(pop);
            disp.display(pop);
            
            mut.setMutationRate(0.01);
            mut.mutate(pop);

            disp.display(pop);
        }
       
        disp.display(pop);
    }
}
