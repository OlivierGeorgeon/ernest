package ga;
import java.util.Comparator;


public class ChromosomeComparator<Type> implements Comparator<IChromosome<Type>>
{
    private IFitness<Type> m_fit = null;
    
    public ChromosomeComparator(IFitness<Type> fit)
    {
        m_fit = fit;
    }

    public int compare(IChromosome<Type> arg0, IChromosome<Type> arg1)
    {
        double fit1 = m_fit.fitness(arg0);
        double fit2 = m_fit.fitness(arg1);
        
        if (fit1 == fit2)
            return 0;
        else if (fit1 > fit2)
            return -1;
        else
            return 1;
    }
}
