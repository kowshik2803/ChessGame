
package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece{
    
    public Knight(int color, int col, int row) {
        super(color, col, row);
        type=Type.KNIGHT;
        if(color==GamePanel.white){
            image=getImage("/images/wknight");
        }else{
            image=getImage("/images/bknight");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){
              if(isValidSquare(targetCol,targetRow)){
                   if(Math.abs(targetCol -precol)*Math.abs(targetRow-prerow)==2){
                    return true;
                   }
              }
        }
        return false;
    }
    
}
