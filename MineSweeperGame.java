import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *     Class: MineSweeperGame
 */

class MineSweeperGame {
	
	private boolean [][] fieldSolved;
	
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
	
	private final int SQUARE = 1;
	private final int COLUMN = 2;
	private final int ROW    = 3;
	
	public int solveGame()
	{
		int maxAttempts = 80;
		boolean retVal;
		
		MouseControl mouse = new MouseControl();
		MineSweeperField mineField = new MineSweeperField();
		MineMap mineMap = new MineMap();
				
		/* Find mine field on screen and extract data */
		retVal = mineField.findMineField();
		if( !retVal ) {
			System.out.println("End solveGame!");
			return -1;
		}
		
		/* Init mine map */
		mineMap.initMineMap( mineField );
		initSolveGame( mineField );
		
		/* Solve game */
		System.out.println(" ---------------------------------------");
		System.out.println(" Start game solving ...");
		mouse.initClick( mineField );
		
		while( maxAttempts > 0 ) {
			
			mouse.moveAway();
			mineMap.updateMineMap( mineField );
			
			
			
			retVal = tryToSolveGame( mineMap , mineField );
			if( retVal ) {
				mineMap.printMineMap();
				System.out.println(" WIN : Game solved!");
				break;
			}
			
			maxAttempts--;
			
		} /* END Solve game */
		
		
		if( maxAttempts == 0 ){
			mineMap.printMineMap();
			System.out.println(" Game not solved!");
		}
	
		return 0;
		
	}
	
	
	public void initSolveGame( MineSweeperField mineField )
	{
		int x, y;
		int xDim = mineField.getDimensionX();
		int yDim = mineField.getDimensionY();
		
		fieldSolved = new boolean [yDim][xDim];
	
		for( y=0 ; y < yDim ; y++ ) 
			for( x=0 ; x < xDim ; x++ ) 
				fieldSolved[y][x] = false;
				
		return;
		
	}
	
	
/**
 *     Function: tryToSolveGame
 *        Input: mineMap - MineMap
 *               mineField - MineSweeperField
 *       Output: 
 *  Description: This function trys to solve the game 
 */
	
	private boolean tryToSolveGame( MineMap mineMap , MineSweeperField mineField ) 
	{
		int x, y;
		int xDim, yDim;
		int fieldVal;
		boolean retVal = true;
		
		xDim = mineField.getDimensionX();
		yDim = mineField.getDimensionY();
		
		for( y=0 ; y < yDim ; y++ ) {
			for( x=0 ; x < xDim ; x++ ) {
				
				/* If already solved, move to next */
				if( fieldSolved[y][x] ) continue;
				else retVal = false;
				
				fieldVal = mineMap.getFieldValue( x , y );
				switch( fieldVal ) {
					
					/* Do nothing */
					case CLOSED :
						break;
					
					/* Set fieldSolved flag to true */
					case EMPTY  :
					case MINE   :
						fieldSolved[y][x] = true;
						break;
						
					/* Try to solve */
					case ONE_M   :
					case TWO_M   :
					case THREE_M :
					case FOUR_M  :
					case FIVE_M  :
					case SIX_M   :
					case SEVEN_M :
					case EIGHT_M :
						trySolveField( fieldVal , x , y , mineMap , mineField );
						break;
					
					default :
						System.out.printf("Unknown field value : %d\n", fieldVal );
				}
			}
		}
		
		return retVal;
		
	}

/**
 *     Function: trySolveField
 *        Input: fieldValue - 
 *               
 *               mineMap - MineMap
 *               mineField - MineSweeperField
 *       Output: /
 *  Description: This function trys to solve the game 
 */
	
	private void trySolveField( int fieldValue , int x , int y , MineMap mineMap , MineSweeperField mineField )
	{
		int mineCount;
		int closedCount;
		int posibleMines;
		int tempMineCnt;
		int tempClosedCnt;

		/* Get basic information form sorounding fields */
		mineCount = checkForValue( x , y , MINE , SQUARE , mineMap );
		closedCount = checkForValue( x , y , CLOSED , SQUARE , mineMap );
		
		posibleMines = fieldValue - mineCount;
		
		/* Set mines */
		if( posibleMines == closedCount ) {	
			setMineToAllClosed( x , y , mineMap );
			fieldSolved[y][x] = true;
			return;
		}
		
		mineCount = checkForValue( x , y , MINE , SQUARE , mineMap );
		
		/* Solved */
		if( mineCount == fieldValue ) {
			if( closedCount > 0 ) openAllClosed( x , y , mineMap , mineField );
			fieldSolved[y][x] = true;
			return;
		} 
		
		int i=0;
		if( fieldValue == TWO_M ) {
			 if( mineMap.getFieldValue( x-1 , y ) == ONE_M ) {
				tempMineCnt = checkForValue( x+1 , y , MINE , COLUMN , mineMap );
				tempClosedCnt = checkForValue( x+1 , y , CLOSED , COLUMN , mineMap );
				if( tempMineCnt==0 && tempClosedCnt==1 ) {
					for( i=y-1 ; i<(y+2) ; i++ ) {
						if( mineMap.getFieldValue( x+1 , i ) == CLOSED ) {
							mineMap.setMineOnMap( x+1 , i );
						}
					}
				}
			 }
		}
		

		return;
		
	}
	
/**
 *     Function: checkForValue
 *        Input: x, y - map position
 *               selectValue - value we wish to count
 *               selectZone - SQUARE, COLUMN or ROW
 *               mineMap - MineMap
 *       Output: Selected value count
 *  Description: This function counts selected value in square, column or row around given position
 */
	
	private int checkForValue( int x , int y , int selectValue , int selectZone , MineMap mineMap )
	{
		int xTmp, yTmp;
		int valueCount = 0;
		
		/* Count slected values in 3x3 square */
		if( selectZone == SQUARE ) {
		
			for( yTmp=(y-1) ; yTmp<(y+2) ; yTmp++ ) {
				for( xTmp=(x-1) ; xTmp<(x+2) ; xTmp++) {
					if( mineMap.getFieldValue( xTmp , yTmp ) == selectValue ) {
						valueCount++;
					}
				}
			}
		}
		
		/* Count selected values in column */
		if( selectZone == COLUMN ) {
			for( yTmp=(y-1) ; yTmp<(y+2) ; yTmp++ ) {
				if( mineMap.getFieldValue( x , yTmp ) == selectValue ) {
					valueCount++;
				}
			}
		}
		
		/* Count slected values in row */
		if( selectZone == ROW ) {
			for( xTmp=(x-1) ; xTmp<(x+2) ; xTmp++) {
				if( mineMap.getFieldValue( xTmp , y ) == selectValue ) {
					valueCount++;
				}
			}
		}
		
		return valueCount;
	}

/**
 *     Function: setMineToAllClosed
 *        Input: x, y - map position
 *               mineMap - MineMap
 *               mineField - MineSweeperField
 *       Output: /
 *  Description: This function sets all closed fields to MINE in 3x3 square around given position
 */
	
	private void setMineToAllClosed( int x , int y , MineMap mineMap )
	{
		int xTmp, yTmp;
	
		for( yTmp=(y-1) ; yTmp<(y+2) ; yTmp++ ) {
			for( xTmp=(x-1) ; xTmp<(x+2) ; xTmp++) {
				if( mineMap.getFieldValue( xTmp , yTmp ) == CLOSED ) {
					mineMap.setMineOnMap( xTmp , yTmp );
				}
			}
		}
		return;
	}
	
/**
 *     Function: openAllClosed
 *        Input: x, y - map position
 *               mineMap - MineMap
 *               mineField - MineSweeperField
 *       Output: /
 *  Description: This function opens all closed field in 3x3 square around given position
 */
	
	private void openAllClosed( int x , int y , MineMap mineMap ,  MineSweeperField mineField )
	{
		int xTmp, yTmp;
		MouseControl mouse = new MouseControl();
		
		for( yTmp=(y-1) ; yTmp<(y+2) ; yTmp++ ) {
			for( xTmp=(x-1) ; xTmp<(x+2) ; xTmp++) {
				if( mineMap.getFieldValue( xTmp , yTmp ) == CLOSED ) {
					mouse.clickOnField( xTmp , yTmp , mineField );
				}
			}
		}
		return;
	}
	
}
