package ga;


import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RouletteWheelSelection<Type> implements ISelection<Type>
{
    private static Random m_rand = new Random();
    
    public IPopulation<Type> select(IFitness<Type> fit, IPopulation<Type> pop)
    {
        List<IChromosome<Type>> l = pop.getIndividuals();
        Collections.sort(l, new ChromosomeComparator<Type>(fit));
        
        IPopulation<Type> newPop = pop.create();
        
        double totalFitness = 0.0;
        for (IChromosome<Type> c : l)
        {
            totalFitness += fit.fitness(c);
        }
        
        for (int i = 0; i < pop.getSize(); i++)
        {
            double slice = 0.0;
            int num = m_rand.nextInt(100);
            for (IChromosome<Type> c : l)
            {
                double ifit = (fit.fitness(c)/totalFitness) * 100;
                slice += ifit;
                if (num < slice)
                {
                    newPop.addIndividual(c);
                    break;
                }
            }
        }
        return newPop;
    }

}
