import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *     Class: MineMap
 */
 
class MineMap {
	
	private int [][] mineMap;
	
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
		int pixelWidth;
		int centerOfMine;
		BufferedImage mineFieldImage;
		
		
		x = mineField.getDimensionX();
		y = mineField.getDimensionY();
		
		pixelWidth = mineField.getWidthOfOneField();
		centerOfMine = pixelWidth / 2;
		
		/* Delay function */
		mineFieldImage = mineField.captureMineField();
		
		for( i=0; i < y; i++) {
			for( j=0 ; j < x ; j++) {
				
				mineMap[i][j] = readOneField( centerOfMine + (j*pixelWidth) , 
											  centerOfMine + (i*pixelWidth) ,
											  mineFieldImage );
			}
		}
		
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
		mineFieldImage.setRGB( x , y ,  0x00FF0000 );
		return 1;
		/* TODO */
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