import java.awt.*;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.lang.Math;
import java.util.Arrays;





class mineSolver {

	public static void main( String[] args ) {
		
		MineSweeperField msfield = new MineSweeperField();

		msfield.setDebug(true);
		msfield.findMineField();	

	}

}


class MineSweeperField {

	private int [][] mineField;
	private int xDimension;
	private int yDimension;
	private int xPixelPosition;
	private int yPixelPosition;
	private int fieldPixelWidth;
	
	private boolean debugSwitch;
	
	private final int EMPTY = (-1);


/**
 *     Function: findMineField
 *        Input: /
 *       Output: Screen shot of users screen
 *  Description: This function returns screen shot of users screen
 */
 
	public boolean findMineField() {
	
		boolean retVal;
		boolean rowFound, colFound;
		BufferedImage screenShot;

		screenShot = getScreenShot();
		
		colFound = findGridColumns( screenShot );
		rowFound = findGridRows( screenShot );
		
		if( colFound && rowFound ) {
			retVal = true;
		} else {
			System.out.println("Mine field not found!");
			return false;
		}
		
		/* Debug */
		if( debugSwitch ) {
			System.out.println(" ---------------------------------------");
			System.out.println(" Grid coordinates: X: " + xPixelPosition + " Y: " + yPixelPosition );
			System.out.println(" Number of columns: " +  xDimension + " | Number of rows: " + yDimension );	
			System.out.println(" Width of mine field in pixels: " + fieldPixelWidth );
		}
		
		return retVal;	

	}

	
/**
 *     Function: findGridColumns
 *        Input: screenShot - buffered image of users screen
 *       Output: true or false
 *  Description: This function sets class variables connected to mine field grid columns
 */

 	private boolean findGridColumns( BufferedImage screenShot )
	{
		boolean retVal;
		GridLines gridLines = new GridLines();
		
		retVal = findVerticalGridLines( screenShot , gridLines );
		
		/* Set class variables */
		xDimension = gridLines.numberOfLines;
		xPixelPosition = gridLines.firstLineOffset;
		fieldPixelWidth = gridLines.distanceBetweenLines;
		
		if( debugSwitch ) {
			debugPrintImage( screenShot , "gridCol.bmp" );
		}
		
		return retVal;
		
	}
	
 /**
  *     Function: findGridRows
  *        Input: screenShot - buffered image of users screen
  *       Output: true or false
  *  Description: This function sets class variables connected to mine field grid rows
  */
 
	private boolean findGridRows( BufferedImage screenShot )
	{
		int i , j;
		boolean retVal;
		final int IMG_WIDTH;
		final int IMG_HEIGHT;
		BufferedImage rotScreenShot;
		GridLines gridLines = new GridLines();

		IMG_WIDTH  = screenShot.getWidth();
		IMG_HEIGHT = screenShot.getHeight();

		/* Rotate screenshot for re-use of findVerticalGridLines function */
		rotScreenShot = new BufferedImage( IMG_HEIGHT , IMG_WIDTH , screenShot.getType() );

		for( i = 0 ; i< IMG_WIDTH ; i++ ) {
			for( j=0; j < IMG_HEIGHT ; j++ ) {
				rotScreenShot.setRGB( IMG_HEIGHT -1 - j , i , screenShot.getRGB(i,j));
			}
		}
	
		/* Same procedure as in method findGridColumns */
		
		retVal = findVerticalGridLines( rotScreenShot , gridLines );

		yDimension = gridLines.numberOfLines;
		yPixelPosition = gridLines.firstLineOffset;
		fieldPixelWidth = gridLines.distanceBetweenLines;
		
		if( debugSwitch ) {
			debugPrintImage( rotScreenShot , "gridRow.bmp" );
		}
		
		return retVal;
	
	}

/**
 *     Function: findVerticalGridLines
 *        Input: screenShot 
 *               gridLines 
 *       Output: 
 *  Description: This function returns screen shot of users screen
 */
	
	private boolean findVerticalGridLines( BufferedImage screenShot, GridLines gridLines )
	{
		int i;
		int xPoss, yPoss;
		int curCol;
		int okCounter;
		int pixelDiff;
		int medianIndex;
		int prevPixelDiff = 0;
		int columnsMatch = 0;
		int maxColumnsMatch = 0;
		int maxColumnPixelDiff = 0;
		int maxColumnStartIndex = 0;
		
		final int VERTICAL_STEP;
		final int IMG_WIDTH;
		final int IMG_HEIGHT;
		final int MAX_NUM_OF_COL = 100;
		int[] columnOffset = new int[MAX_NUM_OF_COL];
		
		final int STAT_DATA_LEN = 100;
		
		int statDataIndex = 0;
		int[] startCoordinate = new int[STAT_DATA_LEN];
		int[] numberOfColumns = new int[STAT_DATA_LEN];
		int[] oneColumnPixelWidth = new int[STAT_DATA_LEN];
		
		/* Init */
		for( i=0 ; i<STAT_DATA_LEN ; i++ ) {
			startCoordinate[i]  = EMPTY;
			numberOfColumns[i]  = EMPTY;
			oneColumnPixelWidth[i] = EMPTY;
		}
		
		VERTICAL_STEP = 10;
		IMG_WIDTH  = screenShot.getWidth();
		IMG_HEIGHT = screenShot.getHeight();

		
		/*************************************************************************/
		/*                       Find mine grid                                  */
		/*************************************************************************/
		
		/* Move down the lines with VERTICAL_STEP pixel step */
	    for( yPoss  = 0 ; yPoss < IMG_HEIGHT ; yPoss = yPoss + VERTICAL_STEP ) {
	
			okCounter = 0;
			for( i = 0 ; i < MAX_NUM_OF_COL ; i++ ) columnOffset[i] = EMPTY;
			
			/* Check line for columns */
			for( xPoss = 0 , curCol = 1 ; xPoss < IMG_WIDTH ; xPoss++ ) {
				if( isColorForGrid( screenShot.getRGB( xPoss , yPoss )) ) {

					/* Increase consecutive counter - for filtering grid rows */
					if( okCounter > 0 ) continue;
					else okCounter++;
			
					/* Debug - paint found pixel red */
					if( debugSwitch ) {
						screenShot.setRGB( xPoss , yPoss  , 0x00FF0000 );		
					}
					
					/* Store column possition */	
					columnOffset[curCol] = xPoss;
					curCol = curCol + 1;
					if( curCol == MAX_NUM_OF_COL ) break;

					/* When found move five pixels */
					xPoss = xPoss + 5;	
				} else {
					okCounter = 0; 	/* Reset OK counter */
				}
				

			} /* END Check line for grid columns */

			/* Find consecutive columns with same pixel differance - possible grid */
			for( curCol = 1 , columnsMatch = 0 ; columnOffset[curCol] != EMPTY  ; curCol++ ) {
				
				pixelDiff = columnOffset[curCol] - columnOffset[curCol-1];

				/* Use tolerance of 4 pixels */
				if( Math.abs( pixelDiff  - prevPixelDiff ) < 4 ) {
					columnsMatch++;
					if( columnsMatch > maxColumnsMatch ) {
						maxColumnsMatch = columnsMatch;
						maxColumnStartIndex = curCol - columnsMatch ;
						maxColumnPixelDiff = (columnOffset[curCol] - columnOffset[maxColumnStartIndex]) / maxColumnsMatch; 
					}
				} else {
					columnsMatch = 1;
				}
				prevPixelDiff = pixelDiff;

			}
		
			/*
			 *	If numbers of columns are in possible MineSweeper limits, than
			 *  store first column x-coordinate and number of grid columns
			 */
			 
			if( (maxColumnsMatch > 7) && (maxColumnsMatch < 32) ) {
				
				/* Store number of columns */
				numberOfColumns[statDataIndex] = maxColumnsMatch;
				/* Store first column x-coordinate */
			    startCoordinate[statDataIndex] = columnOffset[maxColumnStartIndex]; 
				/* Store pixel width of column */
				oneColumnPixelWidth[statDataIndex] = maxColumnPixelDiff; 

				statDataIndex++;
				if( statDataIndex == STAT_DATA_LEN ) {
					break;
				}
			}
			
			maxColumnsMatch = 0;
			maxColumnPixelDiff = 0;
			maxColumnStartIndex = 0;
			
			
		} /* END Move down the lines */
		
		/* Check if grid was found */
		if( statDataIndex < 5 ) {
			return false;
		}
	
		/*************************************************************************/
		/*           Statisticaly handle data with median filter                 */
		/*************************************************************************/

		Arrays.sort( numberOfColumns , 0 , statDataIndex );
		Arrays.sort( startCoordinate , 0 , statDataIndex );
		Arrays.sort( oneColumnPixelWidth , 0 , statDataIndex );
		
		medianIndex = statDataIndex / 2;

		gridLines.numberOfLines = numberOfColumns[ medianIndex ];
		gridLines.firstLineOffset = startCoordinate[ medianIndex ];
		gridLines.distanceBetweenLines = oneColumnPixelWidth[ medianIndex ];

		return true;

	}


/**
 *     Function: debugPrintImage
 *        Input: image - buffered image
 *               fileName - file name 
 *       Output: /
 *  Description: This function writes image to .bmp file
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

/**
 *     Function: getScreenShot
 *        Input: / 
 *       Output: Screen shot of users screen
 *  Description: This function returns screen shot of users screen
 */

	private BufferedImage getScreenShot()
	{
		
		Toolkit toolKit;
		Dimension wholeScreenDim;
		Rectangle wholeScreenRet; 
		BufferedImage screenShot;
	
		screenShot = new BufferedImage( 1 , 1 , 1); 
		toolKit = Toolkit.getDefaultToolkit();

		wholeScreenDim = toolKit.getScreenSize();
		wholeScreenRet = new Rectangle( 0 , 0 , wholeScreenDim.width , wholeScreenDim.height );

		/* Make screen shot of users screen */	
		try {
			screenShot = new Robot().createScreenCapture( wholeScreenRet );
		} catch ( AWTException awte ) {
			System.out.println("Cannot capture screen!");
		}

		return screenShot;
		
	}

/**
 *     Function: isColorForGrid
 *        Input: hexColor - 
 *       Output: True or false
 *  Description: This function determines if pixel is wright color to be part of minesweeper grid
 */

	private boolean isColorForGrid( int hexPixelColor ) 
	{
		/* Red componet in borders */	
		if( ((hexPixelColor & 0x00FF0000) < 0x003F0000 ) && 
			((hexPixelColor & 0x00FF0000) > 0x00001E00 )) {

			/* Green commponent in borders */
			if( ((hexPixelColor & 0x0000FF00) < 0x00006E00 ) && 
				((hexPixelColor & 0x0000FF00) > 0x00001000 )) {

				/* Blue commponent in borders */
				if( ((hexPixelColor & 0x000000FF) < 0x0000008E )) {
					return true;
				}
			}
		}

		return false;
	}

/**
 *     Function: setDebug
 *        Input: state - true or false
 *       Output: /
 *  Description: This function handles debugSwitch
 */

	public void setDebug( boolean state )
	{
		debugSwitch = state;
		return;
	}
	
 /**
 *     Function: getMineField
 *        Input: /
 *       Output: True or false
 *  Description: This function determines if pixel is wright color to
 */
	public int [][] getMineField() {
		return mineField;
	}

	public int getDimensionX() {
		return xDimension;
	}

	public int getDimensionY() {
		return yDimension;
	}

	public int getPixelPosX() {
		return xPixelPosition;
	}
	
	public int getPixelPosY() {
		return yPixelPosition;
	}

}


class GridLines {

	public int numberOfLines;
	public int firstLineOffset;
	public int distanceBetweenLines;

}