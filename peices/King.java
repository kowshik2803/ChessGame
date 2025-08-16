package piece;

import main.GamePanel;
import main.Type;

public class King extends Piece{
    
    public King(int color, int col, int row) {
        super(color, col, row);
        type=Type.KING;
        if(color==GamePanel.white){
            image=getImage("/images/wking");
        }else{
            image=getImage("/images/bking");
        }
    }
    public boolean isOpponentAttacking(int targetCol, int targetRow){
        if(targetCol<precol){
            for(int c=1;c<=4;c++){
                for(Piece piece:GamePanel.simPieces){
                     if(c>0&& c<4){
                            if(piece!=this&&piece.col==c && piece.row==targetRow){
                                return true;
                            }
                        }
                    if((piece.color!=this.color &&piece.type!=Type.KING && piece.canMove(c,targetRow)) && c>1 ){
                       return true;
                    }
                }
            }
        }else{
            for(int c=4;c<7;c++){
                for(Piece piece:GamePanel.simPieces){
                    if(c>4&& c<7){
                            if(this!=piece&&piece.col==c && piece.row==targetRow){
                                return true;
                            }
                        }
                    if((piece.color!=this.color && piece.type!=Type.KING&& piece.canMove(c,targetRow))){
                        return true;
                        }
                    }
                }
            }

        
        return false;
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow)){
            // Check if the target position is one square away in any direction
            if(Math.abs(targetCol - precol) <= 1 && Math.abs(targetRow - prerow) <= 1){
                if(isValidSquare(targetCol,targetRow) &&isSameSquare(targetCol, targetRow)==false){
                return true; // The king can move to the target position
                }
            }
        
        
        if(moved== false){
            //right castling
            if(targetCol==precol+2 &&targetRow== prerow  ){
                for(Piece piece:GamePanel.simPieces){  
                   if(piece.col==precol+3 && piece.row==prerow && piece.moved== false && isOpponentAttacking(targetCol, targetRow)==false){
                        // Check if the square is not under attack
                    GamePanel.castlingP=piece;
                    return true;

                   }
                }
            }
            //left castling
            if(targetCol==precol-2 && targetRow ==prerow ){
                for(Piece piece: GamePanel.simPieces){  
                    if(piece.col== precol-4 && piece.row== targetRow && piece.moved==false &&isOpponentAttacking(targetCol, targetRow)==false){
                         GamePanel.castlingP=piece;
                        return true;
                    }
                }
            }
        }
    }
        return false;
    }
    
}