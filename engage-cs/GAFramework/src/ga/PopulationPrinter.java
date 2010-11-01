package ga;


public class PopulationPrinter<Type> implements IPopulationDisplay<Type>
{
    public void display(IPopulation<Type> pop)
    {
        System.out.println("************************");
        for (IChromosome<Type> c : pop.getIndividuals())
        {
            System.out.println(c);
        }
        System.out.println("************************");
        System.out.println("");
    }

}
