package ga;

import java.util.ArrayList;
import java.util.List;

public class BasicPopulation<Type> implements IPopulation<Type>
{
    private List<IChromosome<Type>> m_people = 
        new ArrayList<IChromosome<Type>>(); 
    
    public int getSize()
    {
        return m_people.size();
    }

    public void intialize(int size, IChromosome<Type> proto)
    {
        for (int i = 0; i < size; i++)
        {
            addIndividual(proto.create());
        }
    }

    public List<IChromosome<Type>> getIndividuals()
    {
        return m_people;
    }

    public void addIndividual(IChromosome<Type> ind)
    {
        m_people.add(ind);        
    }

    public boolean hasConverged(IFitness<Type> fit)
    {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (IChromosome<Type> c : m_people)
        {
            double f = fit.fitness(c);
            if (f > max)
                max = f;
            if (f < min)
                min = f;
        }
        return (max-min) < (max * 0.1);
    }
    
    public IPopulation<Type> create()
    {
        return new BasicPopulation<Type>();
    }

}
