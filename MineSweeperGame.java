import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *     Class: MineSweeperGame
 */

class MineSweeperGame {
	
	public int solveGame()
	{
		boolean retVal;
		MouseControl mouse = new MouseControl();
		MineSweeperField mineField = new MineSweeperField();
		MineMap mineMap = new MineMap();
		
		mineField.setDebug( true );
		
		retVal = mineField.findMineField();
		if( !retVal ) {
			System.out.println("End solveGame!");
			return -1;
		}
		
		/* Click to center of mine field */
		mouse.clickCenter( mineField );
		mouse.clickCenter( mineField );
		
		mineMap.initMineMap( mineField );
		
		/* Solve game */
		while( true ) {
			
			mineMap.updateMineMap( mineField );
			/* Do some stuff .. */
			if( true ) break;
			
		} /* END Solve game */

		return 0;
		
	}
	
}
