package ga;

public interface IFitness<Type>
{
    public double fitness(IChromosome<Type> ind);
}
