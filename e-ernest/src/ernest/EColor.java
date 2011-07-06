package ernest;

public class EColor {
	
	public static EColor RED = new EColor(255, 0, 0);
	public static EColor BLACK = new EColor(0, 0, 0);
	
	public EColor(int r, int g, int b) {
		mRed = Math.max(0, r%256);
		mGreen = Math.max(0, g%256);
		mBlue = Math.max(0, b%256);
	}
	
	public String getHexCode() {
		//String s = Integer.toString(mRed, 16)+
		//Integer.toString(mGreen, 16)+
		//Integer.toString(mBlue, 16);
		String s = format(mRed) + format(mGreen) + format(mBlue);
		return s;
	}
	
	public int getRGB() {
		int color = mRed<<16 + mGreen<<8 + mBlue;
		color = mRed*65536 + mGreen*256 + mBlue;
		return color;
	}
	
	public boolean equals(EColor color) {
		return this.getRGB() == color.getRGB();
		//return false;
	}

	
	private String format(int i)
	{
		if (i == 0)
			return "00";
		else if (i < 16)
			return "0" + Integer.toString(i, 16).toUpperCase();
		else
			return Integer.toString(i, 16).toUpperCase();
	}
	
	private int mRed;
	private int mGreen;
	private int mBlue;
	
}
