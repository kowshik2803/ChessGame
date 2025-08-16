
package piece;
import java.awt.Graphics2D;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.*;
public class Piece {
    public Type type;
    public BufferedImage image;
    public int x,y;
    public int col, row, precol, prerow;
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;
    public Piece(int color,int col,int row){
        this.color=color;
        this.col=col;
        this.row=row;
        x=getx(col);
        y=gety(row);
        precol=col;
        prerow=row;
    }
    public BufferedImage getImage(String imagePath){
        BufferedImage image=null;
        // System.out.println(getClass().getResource("/images/wpawn.png"));

        try{
            image=ImageIO.read(getClass().getResourceAsStream(imagePath+".png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }
    public int getx(int col){
        return col* Board.square_size;
    }
    public int gety(int row){
        return row* Board.square_size;
    }
    public int getCol(int x){
        return (x+Board.half_square_size)/Board.square_size; // convert x to column
    }
    public int getRow(int y){
        return (y+Board.half_square_size)/Board.square_size; // convert y to row
    }
    public int getIndex(){
        for(int index=0;index<GamePanel.simPieces.size();index++){
             if(GamePanel.simPieces.get(index)==this){
                return index;
             }
        }
        return 0;
    }
    public void updatePosition(){
        if(type== Type.PAWN){
            if(Math.abs(row-prerow)==2){
                twoStepped =true;
            }
        }
        x=getx(col);// update x to the current position
        y=gety(row);  
        precol=getCol(x);// update precol and prerow To the current position
        prerow=getRow(y); 
        moved=true;
    }
    public void resetPosition(){
        col=precol; // reset to previous column
        row=prerow; // reset to previous row
        x=getx(col); // update x to the previous position
        y=gety(row); // update y to the previous position
    }                                                                   

    public boolean canMove(int targetCol,int targerRow){
        // This method should be overridden by subclasses to implement specific movement logic
        return false;// Default implementation returns false
    }
    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol>=0 && targetCol<=7 && targetRow>=0 && targetRow<=7){
            return true; // The target position is within the board
        }
        return false; // The target position is outside the board
    }
    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol==precol && targetRow==prerow){
            return true;
        }
        return false;
    }
    public Piece getHittingP(int targetCol, int targetRow){
        for(Piece piece: GamePanel.simPieces){
            if(piece.col==targetCol && piece.row==targetRow&& piece!=this){
                return piece; // Return the piece at the target position
            }
        }
        return null; // No piece at the target position
    }
    public boolean isValidSquare(int targetCol, int targetRow){
        hittingP=getHittingP(targetCol, targetRow);
            if(hittingP==null){
                return true; // The square is valid if there is no piece at the target position
            }else{
                if(hittingP.color!=this.color){
                    return true; // The square is valid if there is an opponent's piece at the target position
                }else{
                    hittingP=null; // Reset hittingP if the piece is of the same color
                }
            }
            return false; // The square is not valid if there is a piece of the same color at the target position
        
    }
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        //when the piece is moving left
        for(int c=precol-1;c>targetCol;c--){
            for(Piece piece:GamePanel.simPieces){
                if(piece.col==c && piece.row==targetRow){
                    hittingP=piece;
                    return true;
                }
            }
        }
        //when the piece is moving right
        for(int c=precol+1;c<targetCol;c++){
            for(Piece piece:GamePanel.simPieces){
                if(piece.col==c && piece.row==targetRow){
                    hittingP=piece;
                    return true;
                }
            }
        }
        //WHEN THE PIECE IS MOVING UP
        for(int r=prerow-1;r>targetRow;r--){
            for(Piece piece:GamePanel.simPieces){
                if(piece.col==targetCol && piece.row==r){
                    hittingP=piece;
                    return true;
                }
            }
        }
        //WHEN THE PIECE IS MOVING DOWN
        for(int r=prerow+1;r<targetRow;r++){        
            for(Piece piece:GamePanel.simPieces){
                if(piece.col==targetCol && piece.row==r){
                    hittingP=piece;
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){
        if(targetRow<prerow){
            //when the piece is moving left up
            for(int c=precol-1;c>targetCol;c--){
                 int diff=Math.abs(c-precol);
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==c && piece.row==prerow-diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }
            
            //up right
            for(int c=precol+1;c<targetCol;c++){
                 int diff=Math.abs(c-precol);
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==c && piece.row==prerow-diff){
                        hittingP=piece;
                        return true;
                    }
                }
            }
        }
        
              if(targetRow>prerow){
                //down left
                for(int c=precol-1;c>targetCol;c--){
                    int diff=Math.abs(c-precol);
                    for(Piece piece:GamePanel.simPieces){
                        if(piece.col==c && piece.row==prerow+diff){
                            hittingP=piece;
                            return true;
                        }
                    }
                }
                //down right
                for(int c=precol+1;c<targetCol;c++){
                    int diff=Math.abs(c-precol);
                    for(Piece piece:GamePanel.simPieces){
                        if(piece.col==c && piece.row==prerow+diff){
                            hittingP=piece;
                            return true;
                        }
                    }
                }
            }
         return false;
        }
    
    public void draw(Graphics2D g2){
        g2.drawImage(image,x,y, Board.square_size, Board.square_size,null);
    }
}

