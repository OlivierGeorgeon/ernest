package ernest;

public class EColor {
	
	public static EColor RED = new EColor(255, 0, 0);
	public static EColor BLACK = new EColor(0, 0, 0);
	
	public EColor(int r, int g, int b) {
		mRed = Math.max(0, r%255);
		mGreen = Math.max(0, g%255);
		mBlue = Math.max(0, b%255);
	}
	
	public String getHexCode() {
		String s = Integer.toString(mRed, 16)+
		Integer.toString(mGreen, 16)+
		Integer.toString(mBlue, 16);
		return s;
	}
	
	public int getRGB() {
		return mRed<<16 + mGreen<<8 + mBlue;
	}

	
	private int mRed;
	private int mGreen;
	private int mBlue;
	
}
