package ernest;

import java.awt.Color;

/**
 * A static feature in the visual field
 * @author Olivier
 */
public interface IIcon 
{
	void setDirection(int direction);
	
	void setDistance(int distance);
	
	void setSpan(int span);
	
	void setColor(Color color);
	
	void setAttractiveness(int attractiveness);

	int getDirection();
	
	int getDistance();
	
	int getSpan();
	
	Color getColor();

	int getAttractiveness();

	String getHexColor();
}
