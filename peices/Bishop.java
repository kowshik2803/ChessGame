package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece{
    
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type=Type.BISHOP;
        if(color==GamePanel.white){
            image=getImage("/images/wbishop");
        }else{
            image=getImage("/images/bbishop");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow) &&isSameSquare(targetCol,targetRow)==false){
            if(Math.abs(targetCol-precol)==Math.abs(targetRow-prerow)&& pieceIsOnDiagonalLine(targetCol,targetRow)==false){
                if(isValidSquare(targetCol, targetRow))
                  return true;
            }
        }
        return false;
    }
    
}
