package ernest;

public class Stimulation implements IStimulation 
{
	private EColor m_color;
	private int m_distance = 0;
	private int m_type;
	private int m_value;
	
	/**
	 * Create a visual stimulation
	 * @param red The red component
	 * @param green The green component
	 * @param blue The blue component
	 * @param distance The distance 
	 */
	public Stimulation(int red, int green, int blue, int distance)
	{
		m_color= new EColor(red, green, blue);
		m_distance = distance;
		m_type = Ernest.STIMULATION_VISUAL;
		m_value = m_color.getRGB(); 
	}

	/**
	 * Create a stimulation from its type and its value
	 * @param type The stimulation's type
	 * @param value The stimulation's value
	 */
	public Stimulation(int type, int value)
	{
		//if (type == Ernest.STIMULATION_GUSTATORY)
			//if (value == Ernest.STIMULATION_GUSTATORY_FISH.getValue())
			//	m_color= Ernest.COLOR_WATER;
//		if (type == Ernest.STIMULATION_TACTILE)
//		{
//			if (value == Ernest.STIMULATION_TOUCH_EMPTY)
//				m_color= Ernest.COLOR_TOUCH_EMPTY;
//			if (value == Ernest.STIMULATION_TOUCH_SOFT)
//				m_color= Ernest.COLOR_TOUCH_ALGA;
//			if (value == Ernest.STIMULATION_TOUCH_WALL)
//				m_color= Ernest.COLOR_TOUCH_WALL;
//		}
		m_type = type;
		m_value = value; 		
	}
	
	public EColor getColor() 
	{
		return m_color;
	}

	public void setDistance(int distance) 
	{
		m_distance = distance;
	}

	public int getDistance() 
	{
		return m_distance;
	}

	public int getType() 
	{
		return m_type;
	}

	public int getValue() 
	{
		return m_value;
	}

	public String getHexColor()
	{
//		String s = String.format("%06X", m_color.getRGB()  & 0x00ffffff); 
		String s = m_color.getHexCode();
		return s;
	}

	/**
	 * Stimulations are equal if they have the same type and value. 
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		
		if (o == this)
			ret = true;
		else if (o == null)
			ret = false;
		else if (!o.getClass().equals(this.getClass()))
			ret = false;
		else
		{
			IStimulation other = (IStimulation)o;
			ret = (other.getType() == m_type) && (other.getValue() == m_value);
		}
		
		return ret;
	}

}
