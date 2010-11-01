package ga;

import java.util.List;
import java.util.Random;

public class SinglePointCrossover<Type> implements ICrossover<Type>
{
    private double m_rate = 0.0;
    private static Random m_rand = new Random();    

    public void setCrossoverRate(double rate)
    {
        m_rate = rate;
    }

    public IPopulation<Type> crossover(IPopulation<Type> pop)
    {
        List<IChromosome<Type>> l = pop.getIndividuals();
        
        IPopulation<Type> newPop = pop.create();
        
        for (int i = 0; i < (pop.getSize()/2); i++)
        {
            double roll = m_rand.nextDouble();
            int i1 = m_rand.nextInt(pop.getSize());
            int i2 = m_rand.nextInt(pop.getSize());
            IChromosome<Type> p1 = pop.getIndividuals().get(i1).copy();
            IChromosome<Type> p2 = pop.getIndividuals().get(i2).copy();
            
            if (roll < m_rate)
            {
                IChromosome<Type> temp = p2.copy();
                
                int point = m_rand.nextInt(p1.getGeneCount());
                for (int b = 0; b < point; b++)
                {
                    p2.setGene(b, p1.getGene(b));
                    p1.setGene(b, temp.getGene(b));
                }

            }
            newPop.addIndividual(p1);
            newPop.addIndividual(p2);
        }
        
        return newPop;
    }

}
