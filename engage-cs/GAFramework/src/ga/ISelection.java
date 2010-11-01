package ga;
public interface ISelection<Type>
{
    public IPopulation<Type> select(IFitness<Type> fit, IPopulation<Type> pop);
}
