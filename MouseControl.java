import java.awt.*;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 *		Class: MouseControl
 */
 
class MouseControl {
	
	public final int LEFT = 1;
	public final int RIGHT = 2;
	
/**
 *     Function: clickPoint
 *        Input: x - x pixel offset from left side of screen
 *               y - y pixel offset from top side of screen
 *               buttonSelect - LEFT or RIGHT
 *       Output: /
 *  Description: This function clicks on provided coordinates
 */
	
	public void clickPoint( int x , int y , int buttonSelect )
	{
		/* Click */
		try {
			Robot robot = new Robot();
			robot.mouseMove( x , y );
			
			if( buttonSelect == LEFT ) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
			if( buttonSelect == RIGHT ) {
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
			}
			
		} catch ( AWTException e ) {
			System.out.println("Cannot create Robot!");
		}
		
		/* Delay */
		try {
			Thread.sleep(10);
		} catch(InterruptedException ex) {
			System.out.println("Thread.sleep() Failed!");
		}	
		
		return;

	}

/**
 *     Function: clickCenter
 *        Input: mineField - MineSweeperField
 *       Output: /
 *  Description: This function clicks to center of mine field
 */

	public void initClick( MineSweeperField mineField )
	{
		int x, y;
		
		/* Calculate center of mine field */
		x = mineField.getStartPosX();
		y = mineField.getStartPosY();
		
		x = x + (( mineField.getDimensionX() * mineField.getWidthOfOneField() ) / 2 );
		y = y + (( mineField.getDimensionY() * mineField.getWidthOfOneField() ) / 2 );
		
		clickPoint( x , y , LEFT );
		clickPoint( x , y , LEFT );
		return;
		
	}

/**
 *     Function: clickOnField
 *        Input: x - x field position
 *               y - y field position
 *               mineField - MineSweeperField
 *       Output: /
 *  Description: This function clicks to selected mine field
 */

	public void clickOnField( int x , int y , MineSweeperField mineField )
	{
		int xStart, yStart;
		int xCoordinate, yCoordinate;
		int centerOfMine;
		
		xStart = mineField.getStartPosX();
		yStart = mineField.getStartPosY();
		centerOfMine = mineField.getWidthOfOneField() / 2;
		
		xCoordinate = xStart + centerOfMine + ( x * mineField.getWidthOfOneField() );
		yCoordinate = yStart + centerOfMine + ( y * mineField.getWidthOfOneField() );
		
		clickPoint( xCoordinate , yCoordinate , LEFT );
		return;
		
	}
	
/**
 *     Function: moveAway
 *        Input: /
 *       Output: /
 *  Description: This function moves mouse cursor off mine field
 */

	public void moveAway()
	{
		try { 
			Robot robot = new Robot();
			robot.mouseMove( 0 , 0 );
		} catch ( AWTException e ) {
			System.out.println("Cannot create Robot!");
		}
		
		return;
	}

}