package ga;

public interface IChromosome<Type>
{
    public int getGeneCount();
    public Type getGene(int i);
    public void setGene(int i, Type g);
    public void mutateGene(int i);
    public IChromosome<Type> copy();
    public IChromosome<Type> create();
}
