package piece;

import main.GamePanel;
import main.Type;

public class Queen extends Piece{
    
    public Queen(int color, int col, int row) {
        super(color, col, row);
        type=Type.QUEEN;
        if(color==GamePanel.white){
            image=getImage("/images/wqueen");
        }else{
            image=getImage("/images/bqueen");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){
            if(isSameSquare(targetCol, targetRow)==false && isValidSquare(targetCol, targetRow)){
                if(targetCol==precol || targetRow==prerow){
                   if(pieceIsOnStraightLine(targetCol, targetRow)==false){
                    System.out.println("queen");
                    return true;
                    }
                }
                //diagonal
                if(Math.abs(targetCol-precol)==Math.abs(targetRow-prerow)){
                    if(pieceIsOnDiagonalLine(targetCol, targetRow)==false){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
