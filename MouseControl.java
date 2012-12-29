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
		
		return;

	}

/**
 *     Function: clickCenter
 *        Input: mineField - MineSweeperField
 *       Output: /
 *  Description: This function clicks to center of mine field
 */

	public void clickCenter( MineSweeperField mineField )
	{
		int x, y;
		
		/* Calculate center of mine field */
		x = mineField.getStartPosX();
		y = mineField.getStartPosY();
		
		x = x + (( mineField.getDimensionX() * mineField.getWidthOfOneField() ) / 2 );
		y = y + (( mineField.getDimensionY() * mineField.getWidthOfOneField() ) / 2 );
		
		clickPoint( x , y , LEFT );
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