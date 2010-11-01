package ga;

import java.util.Random;

public class BasicMutation<Type> implements IMutation<Type>
{
    private static Random m_rand = new Random();
    private double m_rate = 0.0;
    
    public void setMutationRate(double rate)
    {
        m_rate = rate;
    }

    public void mutate(IPopulation<Type> pop)
    {
        for (int i = 0; i < pop.getSize(); i++)
        {
            IChromosome<Type> chrom = pop.getIndividuals().get(i);
            for (int g = 0; g < chrom.getGeneCount(); g++)
            {
                double roll = m_rand.nextDouble();
                if (roll < m_rate)
                {
                    chrom.mutateGene(g);
                }
            }
        }
    }

}
