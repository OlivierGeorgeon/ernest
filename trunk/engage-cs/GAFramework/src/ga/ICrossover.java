package ga;

public interface ICrossover<Type>
{
    public void setCrossoverRate(double rate);
    public IPopulation<Type> crossover(IPopulation<Type> pop);
}
