/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece{
    
    public Rook(int color, int col, int row) {
        super(color, col, row);
        type=Type.ROOK;
        if(color==GamePanel.white){
            image=getImage("/images/wrook");
        }else{
            image=getImage("/images/brook");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow)&&isSameSquare(targetCol,targetRow)==false){
            if(targetCol == precol || targetRow ==prerow){
                if(isValidSquare(targetCol,targetRow) && pieceIsOnStraightLine(targetCol,targetRow)==false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
