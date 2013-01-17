import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;

/**
 *     Class: MineMap
 */
 
class MineMap {
	
	private int xDimension;
	private int yDimension;
	private int minePixelWidth;
	private int [][] mineMap;
	
	/* Mine field states */
	public final int CLOSED  = 9;
	public final int MINE    = 88;
	public final int OFF_MAP = 11;
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
	
	private final int BYTE = 8;
	private final int RED_MASK    = 0x00FF0000;
	private final int GREEN_MASK  = 0x0000FF00;
	private final int BLUE_MASK   = 0x000000FF;
	private final int RED_SHIFT   = BYTE * 2;
	private final int GREEN_SHIFT = BYTE * 1;

	
/**
 *     Function: initMineMap
 *        Input: MineSweeperField mineField
 *       Output: /
 *  Description: This function initializes mine map
 */
 
	public void initMineMap( MineSweeperField mineField ) 
	{	
		int i , j;
		
		xDimension = mineField.getDimensionX();
		yDimension = mineField.getDimensionY();
		minePixelWidth = mineField.getWidthOfOneField();
		
		mineMap = new int[yDimension][xDimension];
		
		/* Init map */
		for( i=0; i < yDimension; i++)
			for( j=0 ; j < xDimension ; j++)
				mineMap[i][j] = CLOSED;
		
		return;
	}

	public int getFieldValue( int x , int y )
	{
		if( (x<0) || (y<0) ) return OFF_MAP;
		if( (x >= xDimension ) ||(y >= yDimension)) return OFF_MAP;
		
		return mineMap[y][x];
	}
	
	public void setMineOnMap( int x , int y )
	{
		if( (x<0) || (y<0) ) return;
		if( (x >= xDimension ) ||(y >= yDimension)) return;
		mineMap[y][x] = MINE;
		
		return;
	}
	
	public void printMineMap()
	{
		for( int i=0; i < yDimension; i++) {
			for( int j=0 ; j < xDimension ; j++) {
				System.out.printf("%2d ", mineMap[i][j]);
			}
			System.out.printf("\n");
		}	
	}
	
/**
 *     Function: updateMineMap
 *        Input: MineSweeperField mineField
 *       Output: /
 *  Description: This function updates mine map 
 */

	public void updateMineMap( MineSweeperField mineField )
	{
	
		BufferedImage mineFieldImage;
		
		/* Delay function */
		try {
			Thread.sleep(40);
		} catch(InterruptedException ex) {
			System.out.println("Thread.sleep() Failed!");
		}	
		
		mineFieldImage = mineField.captureMineField();
		
		readMineMap( mineFieldImage , 0 , 0 );
		readMineMap( mineFieldImage , 1 , 0 );

		/* Debug */
		/*
		debugPrintImage( mineFieldImage , "mineMap.bmp" );
		*/
		return;
		
	}
	
/**
 *     Function: readMineMap
 *        Input: mineFieldImage - screen shot of mine field 
 *               xOffset - corection factor in x direction
 *               yOffset - corection factor in y direction
 *       Output: /
 *  Description: This function sets mine map according to mine field image
 */
	
	private void readMineMap( BufferedImage mineFieldImage , int xOffset , int yOffset )
	{
		int j , i;
		int centerOfMine;
		
		centerOfMine = minePixelWidth / 2;
		
		for( i=0; i < yDimension ; i++) {
			for( j=0 ; j < xDimension ; j++) {
				if( mineMap[i][j] != CLOSED ) continue;		/* If already set skip this field */
				mineMap[i][j] = readOneField( centerOfMine + (j*minePixelWidth) + xOffset , 
											  centerOfMine + (i*minePixelWidth) + yOffset ,
											  mineFieldImage );	
			}
		}
		
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
				k++;
			}
		}
		
		retVal = getValue( sampledPixels );
		
		/* debug */
		/*
		switch( retVal ) {
			case EMPTY :
				debugMarkDot( x , y , 0x000000FF , mineFieldImage );
				break;

			case ONE_M : 
				debugMarkDot( x , y , 0x0000FF00 , mineFieldImage );
				break;
				
			case TWO_M : 
				debugMarkDot( x , y , 0x00FF0000 , mineFieldImage );
				break;
				
			case THREE_M :
				debugMarkDot( x , y , 0x00FFFF00 , mineFieldImage );
				break;
				
		}
		*/
		return retVal;
		
	}

/**
 *     Function: getValue
 *        Input: 
 *       Output: EMPTY, CLOSED, ONE_M ...
 *  Description: This function returns value of one field
 */
	
	private int getValue( int [] pixelArray )
	{
		ColorInfo colorAverage = new ColorInfo();
		ColorInfo colorMax = new ColorInfo();
		ColorInfo colorMin = new ColorInfo();
		
		/* Calcualte some info from sampled array */
		getAverageForRGB( pixelArray , colorAverage );
		getMaxForRGB( pixelArray , colorMax );
		getMinForRGB( pixelArray , colorMin );
		
		/* Dont change order of next statments */
		if( isFieldEmpty( colorMin , colorMax )) 	return EMPTY;
		if( isFieldTwo( colorMin , colorMax )) 		return TWO_M;
		if( isFieldOne( colorMin ,  colorMax )) 	return ONE_M;
		if( isFieldThree( colorMin ,  colorMax )) 	return THREE_M;
		if( isFieldFour(  colorMin ,  colorMax ))   return FOUR_M;
		
		/*
		if( isFieldFive(  colorMin ,  colorMax ))   return FIVE_M;
		*/
		
		return CLOSED;
	
	}
	
/**
 *     Function: isFieldOne
 */
	
	private boolean isFieldOne( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( ((colorMax.red - colorMin.red) > 90) && 
			((colorMax.green - colorMin.green) > 90) &&
			((colorMax.blue - colorMin.blue) < 60 )) {
			return true;
		}
		return false;
	}
	
/**
 *     Function: isFieldTwo
 */
 
	private boolean isFieldTwo( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( (colorMin.red < 50 ) && 
			(colorMin.blue < 20) && 
			(colorMax.green > 90 )) {
			return true;
		}
		return false;
	}
	
/**
 *     Function: isFieldThree
 */
	
	private boolean isFieldThree( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( ((colorMax.red - colorMin.red) < 65) && 
			((colorMax.green - colorMin.green) > 190) &&
			((colorMax.blue - colorMin.blue) > 190 )) {
			return true;
		}
		return false;
	}
	
/**
 *     Function: isFieldFour
 */
	
	private boolean isFieldFour( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( (colorMin.red   < 10) && 
			(colorMin.green < 10) &&
			(colorMax.blue > 110)) {
			return true;
		}
		return false;
	}

/**
 *     Function: isFieldFive
 */
	
	private boolean isFieldFive( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( (colorMax.red   > 100) && 
			(colorMin.green < 10) &&
			(colorMin.blue  < 10)) {
			return true;
		}
		return false;
	}

	
/**
 *     Function: TODO other numbers
 */
	
/**
 *     Function: isFieldEmpty
 */
	
	private boolean isFieldEmpty( ColorInfo colorMin , ColorInfo colorMax )
	{
		if( ((colorMax.red - colorMin.red) < 25) && 
			((colorMax.blue - colorMin.blue) < 15) &&
			((colorMax.green - colorMin.green) < 25)) {
			return true;
		}
		return false;
	}
	
/**
 *     Function: getAverageForRGB
 *        Input: pixelArray   - 
 *               colorAverage - for return
 *       Output: /
 *  Description: This function calculates average for each color commponent 
 *               from provided pixel array
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

/**
 *     Function: getMinForRGB
 *        Input: pixelArray   - 
 *               colorMin - for return
 *       Output: /
 *  Description: This function calculates minimum for each color commponent 
 *               from provided pixel array
 */
	
	private void getMinForRGB( int [] pixelArray , ColorInfo colorMin )
	{
		int [] tempArray = new int[pixelArray.length];
		
		tempArray = maskPixelArray( pixelArray , RED_MASK , RED_SHIFT );
		Arrays.sort(tempArray);
		colorMin.red = tempArray[0] ;
		
		tempArray = maskPixelArray( pixelArray , GREEN_MASK , GREEN_SHIFT );
		Arrays.sort(tempArray);
		colorMin.green = tempArray[0] ;
		
		tempArray = maskPixelArray( pixelArray , BLUE_MASK , 0 );
		Arrays.sort(tempArray);
		colorMin.blue = tempArray[0];
		
		return;
	}

/**
 *     Function: getMaxForRGB
 *        Input: pixelArray - 
 *               colorMax - for return
 *       Output: /
 *  Description: This function calculates maximum for each color commponent 
 *               from provided pixel array
 */
	
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
	
/**
 *     Function: maskPixelArray
 *        Input: pixelArray - 
 *               colorMask - select color component
 *               colorShift -
 *       Output: maskedArray
 *  Description: This function masks pixel array
 */
	
	private int [] maskPixelArray( int [] pixelArray , int colorMask , int colorShift )
	{
		int [] maskedArray = new int[pixelArray.length];
		
		for( int i=0 ; i < pixelArray.length ; i++ ) {
			maskedArray[i] = (pixelArray[i]&colorMask) >> colorShift;
		}
		return maskedArray;
		
	}
	
/**
 *     Debug functions
 */
 
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
	
	private void debugMarkDot( int x , int y , int color , BufferedImage image )
	{
		image.setRGB( x, y  , color );
		image.setRGB( x+1, y , color );
		image.setRGB( x-1, y , color );
		image.setRGB( x, y+1 , color );
		image.setRGB( x, y-1 , color );
		return;
	}
	
}