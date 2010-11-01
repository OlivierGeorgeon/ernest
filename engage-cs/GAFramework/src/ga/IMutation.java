package ga;

public interface IMutation<Type>
{
    public void setMutationRate(double rate);
    public void mutate(IPopulation<Type> pop);
}
