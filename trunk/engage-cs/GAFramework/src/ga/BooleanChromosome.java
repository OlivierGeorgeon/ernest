package ga;

public class BooleanChromosome implements IChromosome<Boolean>
{
    private static java.util.Random m_rand = new java.util.Random();
    
    private int m_size;
    private Boolean[] m_bits = null;
    
    public BooleanChromosome(int size)
    {
        m_size = size;
        m_bits = new Boolean[m_size];
        for (int i = 0; i < m_size; i++)
        {
            int num = m_rand.nextInt(2);
            m_bits[i] = num == 0 ? Boolean.FALSE : Boolean.TRUE;
        }
    }
    
    public int getGeneCount()
    {
        return m_size;
    }

    public Boolean getGene(int i)
    {
        return m_bits[i];
    }

    public void setGene(int i, Boolean g)
    {
        m_bits[i] = g;
    }

    public void mutateGene(int i)
    {
        if (m_bits[i] == Boolean.FALSE)
            m_bits[i] = Boolean.TRUE;
        else
            m_bits[i] = Boolean.FALSE;
    }
    
    public IChromosome<Boolean> copy()
    {
        IChromosome<Boolean> copy = create();
        for (int i = 0; i < getGeneCount(); i++)
        {  
            copy.setGene(i, getGene(i));
        }
        return copy;
    }
    
    public IChromosome<Boolean> create()
    {
        return new BooleanChromosome(getGeneCount());
    }
    
    public String toString()
    {
        String s = "";
        for (int i = 0; i < getGeneCount(); i++)
            s += getGene(i) + " ";
        return s;
    }
    
}
