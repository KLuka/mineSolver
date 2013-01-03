import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;
/**
 *     Class: MineMap
 */
 
class MineMap {
	
	private int [][] mineMap;
	private int minePixelWidth;
	
	/* Mine field states */
	public final int CLOSED  = 9;
	public final int MINE    = 10;
	public final int EMPTY   = 0;
	public final int ONE_M   = 1;
	public final int TWO_M   = 2;
	public final int THREE_M = 3;
	public final int FOUR_M  = 4;
	public final int FIVE_M  = 5;
	public final int SIX_M   = 6;
	public final int SEVEN_M = 7;
	public final int EIGHT_M = 8;
	
	private class ColorInfo {
		public int red;
		public int blue;
		public int green;
	}
	
/**
 *     Function: initMineMap
 *        Input: MineSweeperField mineField
 *       Output: /
 *  Description: This function initializes mine map
 */
 
	public void initMineMap( MineSweeperField mineField ) 
	{
		
		int i , j;
		int x , y;
		
		x = mineField.getDimensionX();
		y = mineField.getDimensionY();
		minePixelWidth = mineField.getWidthOfOneField();
		
		mineMap = new int[y][x];
		
		/* Init map */
		for( i=0; i < y; i++)
			for( j=0 ; j < x ; j++)
				mineMap[i][j] = CLOSED;
		
		return;
	
	}

/**
 *     Function: updateMineMap
 *        Input: MineSweeperField mineField
 *       Output: /
 *  Description: This function updates mine map
 */

	public void updateMineMap( MineSweeperField mineField )
	{
		int j , i;
		int x , y;
		int centerOfMine;
		BufferedImage mineFieldImage;
		
		
		x = mineField.getDimensionX();
		y = mineField.getDimensionY();
		
		centerOfMine = minePixelWidth / 2;
		
		/* TODO: Must call delay function */
		try {
			Thread.sleep(50);
		} catch(InterruptedException ex) {
			System.out.println("Thread.sleep() Failed!");
		}	
		
		mineFieldImage = mineField.captureMineField();
		
		/* Scan whole grid */
		for( i=0; i < y; i++) {
			for( j=0 ; j < x ; j++) {
				
				mineMap[i][j] = readOneField( centerOfMine + (j*minePixelWidth) , 
											  centerOfMine + (i*minePixelWidth) ,
											  mineFieldImage );
				System.out.printf("%2d " , mineMap[i][j] );	
			}
			System.out.printf("\n");
		}
		
		/* Debug */
		debugPrintImage( mineFieldImage , "mineMap.bmp" );
		
		return;
		
	}
	
/**
 *     Function: readOneField
 *        Input: x - 
 *               y - 
 *               mineFieldImage - screen shot of mine field
 *       Output: EMPTY, CLOSED, ONE_M ...
 *  Description: This function returns value of one field
 */
 
	private int readOneField( int x , int y , BufferedImage mineFieldImage )
	{
		int i, j, k;
		int retVal = 1;
		int xOffset, yOffset;
		int xStart, yStart;
		int [] sampledPixels = new int[9];
		
		xOffset = minePixelWidth / 4;
		yOffset = minePixelWidth / 4;
		xStart  = x - xOffset;
		yStart  = y - yOffset;
		
		/* Sample 3x3 array around center of mine field */
		for( i=0,k=0 ; i<3 ; i++ ) {
			for( j=0 ; j<3 ; j++ ) {
				sampledPixels[k] = mineFieldImage.getRGB(xStart+(j*xOffset), yStart+(i*yOffset));
				mineFieldImage.setRGB( xStart+(j*xOffset), yStart+(i*yOffset), 0x00FF0000 );
				k++;
			}
		}
		
		retVal = getValue( sampledPixels );
		
		switch( retVal ) {
			case EMPTY :
				mineFieldImage.setRGB( x, y , 0x0000FF00 );
				mineFieldImage.setRGB( x+1, y , 0x0000FF00 );
				mineFieldImage.setRGB( x-1, y , 0x0000FF00 );
				mineFieldImage.setRGB( x, y+1 , 0x0000FF00 );
				mineFieldImage.setRGB( x, y-1 , 0x0000FF00 );
				break;
			case TWO_M : 
				mineFieldImage.setRGB( x, y , 0x0000FFFF );
				mineFieldImage.setRGB( x+1, y , 0x0000FFFF );
				mineFieldImage.setRGB( x-1, y , 0x0000FFFF );
				mineFieldImage.setRGB( x, y+1 , 0x0000FFFF );
				mineFieldImage.setRGB( x, y-1 , 0x0000FFFF );
				break;
		}
		
		return retVal;
		
	}

/**
 *     Function: getValue
 *        Input: 
 *       Output: EMPTY, CLOSED, ONE_M ...
 *  Description: This function returns value of one field
 */
	
	private final int BYTE = 8;
	private final int RED_MASK    = 0x00FF0000;
	private final int GREEN_MASK  = 0x0000FF00;
	private final int BLUE_MASK   = 0x000000FF;
	private final int RED_SHIFT   = BYTE * 2;
	private final int GREEN_SHIFT = BYTE * 1;


	private int getValue( int [] pixelArray )
	{
		ColorInfo colorAverage = new ColorInfo();
		ColorInfo colorMax = new ColorInfo();
		ColorInfo colorMin = new ColorInfo();
		
		/* */
		getAverageForRGB( pixelArray , colorAverage );
		getMaxForRGB( pixelArray , colorMax );
		getMinForRGB( pixelArray , colorMin );
		
		System.out.printf("%3d " , colorMax.red - colorMin.red );
		System.out.printf("%3d " , colorMax.green - colorMin.green );
		System.out.printf("%3d " , colorMax.blue - colorMin.blue );
		
		if( isFieldEmpty( colorMin , colorMax )) return EMPTY;
		if( isFieldTwo( colorMin , colorMax   )) return TWO_M;
			
		return CLOSED;
	
	}
	
	private boolean isFieldTwo( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( ( colorMin.red < 50 ) && (colorMin.blue < 20) && ( colorMax.green > 90 )) {
			return true;
		}
		return false;
	}
	
	private boolean isFieldEmpty( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( ((colorMax.red - colorMin.red) < 25) && 
			((colorMax.blue - colorMin.blue) < 15) &&
			((colorMax.green - colorMin.green) < 25)) {
			return true;
		}
		return false;
	}
	

	
	private void getMaxForRGB( int [] pixelArray , ColorInfo colorMax )
	{
		int [] tempArray = new int[pixelArray.length];
		
		tempArray = maskPixelArray( pixelArray , RED_MASK , RED_SHIFT );
		Arrays.sort(tempArray);
		colorMax.red = tempArray[ tempArray.length - 1] ;
		
		tempArray = maskPixelArray( pixelArray , GREEN_MASK , GREEN_SHIFT );
		Arrays.sort(tempArray);
		colorMax.green = tempArray[ tempArray.length - 1] ;
		
		tempArray = maskPixelArray( pixelArray , BLUE_MASK , 0 );
		Arrays.sort(tempArray);
		colorMax.blue = tempArray[ tempArray.length - 1];
		
		return;
	}
	
	private void getMinForRGB( int [] pixelArray , ColorInfo colorMax )
	{
		int [] tempArray = new int[pixelArray.length];
		
		tempArray = maskPixelArray( pixelArray , RED_MASK , RED_SHIFT );
		Arrays.sort(tempArray);
		colorMax.red = tempArray[ 0 ] ;
		
		tempArray = maskPixelArray( pixelArray , GREEN_MASK , GREEN_SHIFT );
		Arrays.sort(tempArray);
		colorMax.green = tempArray[ 0 ] ;
		
		tempArray = maskPixelArray( pixelArray , BLUE_MASK , 0 );
		Arrays.sort(tempArray);
		colorMax.blue = tempArray[ 0 ];
		
		return;
	}
	
	private int [] maskPixelArray( int [] pixelArray , int colorMask , int colorShift )
	{
		int [] maskedArray = new int[pixelArray.length];
		
		for( int i=0 ; i < pixelArray.length ; i++ ) {
			maskedArray[i] = (pixelArray[i]&colorMask) >> colorShift;
		}
		return maskedArray;
		
	}
	
	
/**
 *     Function: getAverageForRGB
 *        Input: 
 *       Output: 
 *  Description: This function returns value of one field
 */
	private void getAverageForRGB( int [] pixelArray , ColorInfo colorAverage )
	{
		int i;
		int redAvg = 0;
		int blueAvg = 0;
		int greenAvg = 0;
		
		/* Sum up by color component */
		for( i=0 ; i < pixelArray.length ; i++ ) {
			blueAvg = blueAvg + ((pixelArray[i]&BLUE_MASK));
			redAvg = redAvg + ((pixelArray[i]&RED_MASK) >> RED_SHIFT);
			greenAvg = greenAvg + ((pixelArray[i]&GREEN_MASK) >> GREEN_SHIFT);
		}
		
		/* Calculate average */
		colorAverage.red = redAvg / pixelArray.length;
		colorAverage.blue = blueAvg / pixelArray.length;
		colorAverage.green = greenAvg / pixelArray.length;
		
		return;
	
	}
	
	/* remove this */
	private void debugPrintImage( BufferedImage image , String fileName )
	{
		File outputFile = new File( fileName );		
		try {
			ImageIO.write( image , "bmp" , outputFile );
		} catch ( IOException e ) {
			System.out.println("Cannot write image to file!");
			return;
		}
		System.out.println("Debug: Screen shot saved to file " + fileName +"!");
		return;
	}

	
}