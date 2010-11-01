package ga;
import java.util.List;
public interface IPopulation<Type>
{
    public int getSize();
    public void intialize(int size, IChromosome<Type> proto);
    public List<IChromosome<Type>> getIndividuals();
    public void addIndividual(IChromosome<Type> ind);
    public boolean hasConverged(IFitness<Type> fit);
    public IPopulation<Type> create();
}
