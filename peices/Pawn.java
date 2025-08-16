/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package piece;

import main.GamePanel;
import main.Type;

/**
 *
 * @author kowsh
 */
public class Pawn extends Piece{
    
    public Pawn(int color, int col, int row) {
        super(color, col, row);
    
        type=Type.PAWN;
       if(color==GamePanel.white){
             image=getImage("/images/wpawn");
       }else{
           image=getImage("/images/bpawn");
       }
    } 
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow)==false){
             //Define move value based on its color
             int moveValue;
             if(color==GamePanel.white){
                moveValue=-1;
             }else{
                moveValue=1;
             }
             hittingP=getHittingP(targetCol, targetRow);
             //1 square movement
             if(targetCol ==precol && targetRow ==prerow+moveValue &&hittingP==null){
                 return true;
             }
               //2 square movement
               if(targetCol==precol && targetRow== prerow+moveValue*2 && hittingP==null && moved==false){
                    if(pieceIsOnStraightLine(targetCol, targetRow)==false){
                        return true; // Valid move if the piece is not blocked
                    }
               }
                //diagonal movement and capture opponent's piece
                   if(Math.abs(targetCol- precol)==1 && targetRow==prerow+moveValue && hittingP!=null && hittingP.color!=color){
                   return  true;
                   }
        //En passant capture
         if(Math.abs(targetCol- precol)==1 && targetRow==prerow+moveValue){
            for(Piece piece: GamePanel.simPieces){
                if(piece.col==targetCol && piece.row==prerow && piece.twoStepped==true){
                    hittingP= piece;
                    return true;
                }
            }
         }
        }
        return false;
    }   
}
