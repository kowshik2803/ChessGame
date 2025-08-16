/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import javax.swing.JPanel;

import java.awt.*;
import java.util.ArrayList;

import piece.*;
public class GamePanel extends JPanel implements Runnable{
    public static final int width=1100; // width of the game panel
    public static final int height=650; // height of the game panel
    final int fps=60;
    Thread gameThread;
    Board board=new Board();
    Mouse mouse=new Mouse();
    //pieces
    public static ArrayList<Piece> pieces=new ArrayList<>();
    public static ArrayList<Piece> simPieces=new ArrayList<>();
    public static ArrayList<Piece> capturedPieces=new ArrayList<>();
    public static ArrayList<Positions> positions=new ArrayList<>();
    ArrayList<Piece> promoPieces =new ArrayList<>();
   Piece activeP, checkingP,recentlyMovedP;
   int recentlyMovedPprecol, recentlyMovedPprerow;
   public static Piece castlingP;
   //colors
    public static final int white=0;
    public static final int black=1;
    int currentColor=white;
    boolean canMove;
    boolean validSquare;
    boolean promotions;
    boolean gameOver;
    boolean Stalemate;
    public GamePanel(){
        setPreferredSize(new Dimension(width,height));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
       setPieces();
        // teatstalemate();
        copyPieces(pieces,simPieces);
    }
    @Override
    public Dimension getPreferredSize() {
    return new Dimension(width, height); // or any default size you want
}
    public void launchGame(){
        gameThread=new Thread(this);
        gameThread.start();
    }
    public void setPieces(){
        //white
        pieces.add(new Pawn(white,0,6));
        pieces.add(new Pawn(white,1,6));
        pieces.add(new Pawn(white,2,6));
        pieces.add(new Pawn(white,3,6));
        pieces.add(new Pawn(white,4,6));
        pieces.add(new Pawn(white,5,6));
        pieces.add(new Pawn(white,6,6));
        pieces.add(new Pawn(white,7,6));
        pieces.add(new Knight(white,1,7));
        pieces.add(new Knight(white,6,7));
        pieces.add(new Rook(white,0,7));
        pieces.add(new Rook(white,7,7));
        pieces.add(new Bishop(white,2,7));
        pieces.add(new Bishop(white,5,7));
        pieces.add(new Queen(white,3,7));
        pieces.add(new King(white,4,7));
        //black
        pieces.add(new Pawn(black,0,1));
        pieces.add(new Pawn(black,1,1));
        pieces.add(new Pawn(black,2,1));
        pieces.add(new Pawn(black,3,1));
        pieces.add(new Pawn(black,4,1));
        pieces.add(new Pawn(black,5,1));
        pieces.add(new Pawn(black,6,1));
        pieces.add(new Pawn(black,7,1));
        pieces.add(new Knight(black,1,0));
        pieces.add(new Knight(black,6,0));
        pieces.add(new Rook(black,0,0));
        pieces.add(new Rook(black,7,0));
        pieces.add(new Bishop(black,2,0));
        pieces.add(new Bishop(black,5,0));
        pieces.add(new Queen(black,3,0));
        pieces.add(new King(black,4,0));
    }
    void teatstalemate(){
        pieces.add(new Queen(black,4,1));
        pieces.add(new King(white,4,7));
        pieces.add(new Pawn(black,4,6));
        pieces.add(new Pawn(white, 7, 6));
        pieces.add(new King(black,7,4));
        pieces.add(new Rook(white, 0, 0));
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i=0;i<source.size();i++){
            target.add(source.get(i));
        }
    }
    public void run(){
        double drawInterval=1000000000/fps;
        double delta=0;
        long lastTime=System.nanoTime();
        long currentTime;
        while(gameThread!=null){
            currentTime=System.nanoTime();
            delta+=(currentTime-lastTime)/drawInterval;
            lastTime=currentTime;
            if(delta>=1){
                update();
                repaint(); // repaint the game panel
                delta--;
            }
        }
    }
    private void update(){
        if(promotions){
              promoting();
        }
        else if(gameOver==false && Stalemate==false){
         if(mouse.pressed){
             if(activeP==null){
                //
                 for(Piece piece:simPieces){
                    if(piece.color==currentColor && 
                         piece.col==mouse.x/Board.square_size && 
                         piece.row==mouse.y/Board.square_size){
                         activeP=piece;
                     }
                  }
             }else{
                 //if the player is holding a piece simulate its movement
                 if(positions.size()==0){
                      storePossibleMoves(activeP);
                 }
                
                 simulate();
              }
            }
    //mouse button released
    
    if(mouse.pressed==false){
        positions.clear();
        
        if(activeP!=null){
            if(validSquare){
                //move confirmed
                //update the piece list in case a piece has been captured and removed during simulation
                if(activeP.hittingP!=null){
                    capturedPieces.add(activeP.hittingP);
                }
                recentlyMovedP=activeP;
                recentlyMovedPprecol=activeP.precol;
                recentlyMovedPprerow=activeP.prerow;
                copyPieces(simPieces,pieces);
                activeP.updatePosition();
                
                if(castlingP!=null){
                    castlingP.updatePosition();
                }
                if(isKingInCheck() && isCheckmate()){
                    gameOver=true;

                 }else if(isStalemate() && isKingInCheck()==false){
                    System.out.println("checking Stalemate");
                     Stalemate=true;
                 }
                else{
                    if(canPromote()){
                    promotions=true;
                }else{
                      changePlayer();
                }
                }
                
                
            }else{
                copyPieces(pieces,simPieces);//
                activeP.resetPosition();
                activeP=null;
                // if(castlingP!=null){
                //     castlingP.col=castlingP.precol;
                //     castlingP.x=castlingP.getx(castlingP.col);
                //     castlingP=null;
                // }
            }
         
        }
    }
   
}
        
}
    private void simulate(){
        canMove=false;    //
        validSquare=false; //
        //reset the piece list in every loop
        // this is basically for restoring the removed piece during simulation
        copyPieces(pieces,simPieces);
        if(castlingP!=null){    
            castlingP.col=castlingP.precol;
            castlingP.x=castlingP.getx(castlingP.col);
             castlingP=null;
        }
         //if a piece is being held update its position
        activeP.x=mouse.x-Board.half_square_size; // center the piece on the mouse
        activeP.y=mouse.y-Board.half_square_size;
        activeP.col=activeP.getCol(activeP.x);
        activeP.row=activeP.getRow(activeP.y);
        //check if the piece is hovering over a reachable square
        if(activeP.canMove(activeP.col, activeP.row)){
             canMove=true;
             if(activeP.hittingP!=null){
                simPieces.remove(activeP.hittingP.getIndex()); // remove the piece being hit
             }
             checkCastling();
             if(isIllegal(activeP)==false && opponentCanCaptureKing()==false){
                //if the piece is not in check and the opponent cannot capture the king
                
               validSquare=true;
             }
             
        }
        }
        private boolean isDoubleCheck(){
            int count=0;
            Piece king=getKing(true);
            for(Piece piece: simPieces){
                if(piece.color!=king.color && piece.canMove(king.col, king.row)){
                    count++;
                    if(count>1){
                        return true;
                    }
                }
            }
            return false;

        }
        private boolean isKingInCheck(){
            Piece king=getKing(true);
            for(Piece activeP: simPieces){
            if(activeP.canMove(king.col, king.row)){
                checkingP=activeP; // the piece that is checking the king
                return true; // The king is in check
            }
        }
               
               checkingP=null; // No piece is checking the king
               return false; // The king is not in check
            
        }
        private boolean isCheckmate(){
            Piece king =getKing(true);
            if(kingCanMove(king)){
                return false;
            }else{
                if(isDoubleCheck()){
                    return true;
                }
                //but you stiil have a chance
                // check if ypu can block the attack with yor piece
                // check the position of the checking piece and the king in check
                int colDiff=Math.abs(checkingP.col-king.col);
                int rowDiff=Math.abs(checkingP.row-king.row);
                if(colDiff==0){
                    // the checking piece is attacking vertically
                    if(checkingP.row<king.row){
                        // the checking piece is above the king
                        for(int row=checkingP.row;row<king.row;row++){
                            for(Piece piece :simPieces){
                                if(piece !=king && piece.color!=currentColor && piece.canMove(checkingP.col, row)){
                                    return false;
                                }
                            }
                        }

                    }
                    if(checkingP.row>king.row){
                        // the checking piece is below the king
                        for(int row=checkingP.row;row>king.row;row--){
                            for(Piece piece :simPieces){
                                if(piece !=king && piece.color!=currentColor && piece.canMove(checkingP.col, row)){
                                    return false;
                                }
                            }
                        }
                        
                    }
                }else if(rowDiff==0){
                    // the checking piece is attacking horizontally

                    if(checkingP.col<king.col){
                        // the checking piece is to the left
                        for(int col=checkingP.col;col<king.col;col++){
                            for(Piece piece :simPieces){
                                if(piece !=king && piece.color!=currentColor && piece.canMove(col, checkingP.row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col>king.col){
                        // the checking piece is to the right
                        for(int col=checkingP.col;col>king.col;col--){
                            for(Piece piece :simPieces){
                                if(piece !=king && piece.color!=currentColor && piece.canMove(col, checkingP.row)){
                                    return false;
                                }
                            }
                        }
                    }
                }else if(colDiff==rowDiff){
                    // the checking piece is attacking diagonally
                    if(checkingP.row<king.row){
                        //the checking piece is above the king
                        if(checkingP.col<king.col){
                            //the checking piece is upprer left
                            for(int col=checkingP.col, row=checkingP.row; col<king.col && row<king.row; col++, row++){
                                for(Piece piece: simPieces){
                                    if(piece !=king && piece.color!=currentColor && piece.canMove(col,row)){
                                        return false;
                                    }
                                }
                                
                            }
                        }
                        if(checkingP.col>king.col){
                            //the checking piece is upper right
                            for(int col=checkingP.col, row=checkingP.row; col>king.col && row<king.row; col--, row++){
                                for(Piece piece: simPieces){
                                    if(piece !=king && piece.color!=currentColor && piece.canMove(col,row)){
                                        return false;
                                    }
                                }
                                
                            }
                            
                        }
                    }
                    if(checkingP.row>king.row){
                        //the checking piece is below the king
                        if(checkingP.col<king.col){
                            //the checking piece is lower left
                            for(int col=checkingP.col, row=checkingP.row; col<king.col && row>king.row; col++, row--){
                                for(Piece piece: simPieces){
                                    if(piece !=king && piece.color!=currentColor && piece.canMove(col,row)){
                                        return false;
                                    }
                                }
                                
                            }
                        }
                        if(checkingP.col>king.col){
                            //the checking piece is lower right
                            for(int col=checkingP.col, row=checkingP.row; col>king.col && row>king.row; col--, row--){
                                for(Piece piece: simPieces){
                                    if(piece !=king && piece.color!=currentColor && piece.canMove(col,row)){
                                        return false;
                                    }
                                }
                                
                            }
                            
                        }
                    }
                }else{
                
                 }
            
         }
         return true;

        }
        private boolean isStalemate(){
            ArrayList<Piece> simPiecesCopy = new ArrayList<>(simPieces); // iterate over a copy
            //System.out.println("checking stalemate");
           for(Piece piece: simPiecesCopy){
            if(piece.color!=currentColor){
                for(int col=0;col<8;col++){
                    for(int row=0;row<8;row++){
                        if(piece.canMove(col, row)){
                            
                             piece.col=col;
                             piece.row=row;
                             Piece captured=null;
                             int capturedIndex=-1;
                             for(int i=0;i<simPieces.size();i++){
                                Piece p=simPieces.get(i);
                                if(p.col==col && p.row==row && p.color!=piece.color){
                                    captured=p;
                                    capturedIndex=i;
                                    break;
                                }
                             }
                             if(captured!=null){
                                simPieces.remove(capturedIndex);
                             }
                             //check the move not results in illegal move
                                if(opponent()==false){
                                    if(isIllegal(getKing(true))==false){
                                        //restore the captured piece
                                        if(captured!=null){
                                            simPieces.add(captured);
                                        }
                                        piece.resetPosition();
                                        // copyPieces(simPieces, pieces);
                                        return false; // a valid move is found
                                    }

                                }
                                    //restore the captured piece
                                    piece.resetPosition();
                                    if(captured!=null){
                                        simPieces.add(captured);
                                    }
                              
                           
                        }
                        
                    }
                }
            }
           } 
           return true;
        }
    private void storePossibleMoves(Piece piece){
            positions.clear();
        for (int col = 0; col < 8; col++) {
        for (int row = 0; row < 8; row++) {
            if (piece.canMove(col, row)) {
                // Simulate the move
                int oldCol = piece.precol;
                int oldRow = piece.prerow;
                Piece captured = null;
                int capturedIndex = -1;

                // Find if a piece would be captured
                for (int i = 0; i < simPieces.size(); i++) {
                    Piece p = simPieces.get(i);
                    if (p.col == col && p.row == row && p.color != piece.color) {
                        captured = p;
                        capturedIndex = i;
                        break;
                    }
                }

            piece.col = col;
            piece.row = row;
            if (captured != null) simPieces.remove(capturedIndex);

            boolean kingInCheck = opponentCanCaptureKing();

         // Restore state
        piece.col = oldCol;
        piece.row = oldRow;
        if (captured != null) simPieces.add(captured);
                // Only add if king is not in check after the move
                if (!kingInCheck) {
                    positions.add(new Positions(col, row, captured!=null));
                }
            }
        }
    }
}

        private boolean opponent(){
            Piece king=getKing(true);
            for(Piece piece: simPieces){
                if(piece.color!=king.color && piece.canMove(king.col, king.row)){
                    return true; // The opponent can capture the king
                }
            }
            return false; // The opponent cannot capture the king
        }
        private boolean kingCanMove(Piece king){
                if(isValidMove(king, -1, -1)){return true;}
                if(isValidMove(king, -1, 0)){return true;}
                if(isValidMove(king, -1, 1)){return true;}
                if(isValidMove(king, 0, -1)){return true;}
                if(isValidMove(king, 0, 1)){return true;}   
                if(isValidMove(king, 1, -1)){return true;}
                if(isValidMove(king, 1, 0)){return true;}   
                if(isValidMove(king, 1, 1)){return true;}
                return false;
        }
        private boolean isValidMove(Piece king, int colplus, int rowplus){
            boolean isValidMove= false;
            //update the kings position for a second
            king.col+=colplus;
            king.row+=rowplus;
            if(king.canMove(king.col, king.row)){
                if(king.hittingP!=null){
                    simPieces.remove(king.hittingP.getIndex()); // remove the piece being hit
                }
                if(isIllegal(king)==false){
                    isValidMove=true;
                }
            }
            //reset the kings position and restore the removed piece
            king.resetPosition();
            copyPieces(pieces, simPieces);
            return isValidMove;

        }
        private Piece getKing(boolean opponent){
            Piece king=null;
            for(Piece piece: simPieces){
                if(opponent){
                    if(piece.type==Type.KING && piece.color!=currentColor){
                        king=piece;
                    }
                }else{
                    if(piece.type==Type.KING && piece.color==currentColor){
                        king=piece;
                    }
                }
            }
            return king;
        }
       private void checkCastling() {
    if (castlingP != null ) {
        if(castlingP.col==0){
            castlingP.col+=3;
        }else if(castlingP.col==7){
            castlingP.col-=2;
        }
        castlingP.x=castlingP.getx(castlingP.col);
        
    }
}
        private void changePlayer(){
            if(currentColor ==white){
                currentColor=black;
                //Reset the blacks two stepped status
                for(Piece piece:pieces){
                    if(piece.color==black){
                        piece.twoStepped=false;
                    }
                }
            }else{
                currentColor=white;
                //reset white two stepped status
                for(Piece piece: pieces){
                    if(piece.color==white){
                        piece.twoStepped=false;
                    }
                }
            }
            activeP=null;
        }
        private boolean canPromote(){
            promoPieces.clear();
            if(activeP.type==Type.PAWN){
                if(currentColor==white && activeP.row==0 || currentColor==black && activeP.row==7){
                    promoPieces.add(new Rook(currentColor, 9, 2));
                    promoPieces.add(new Knight(currentColor, 9, 3));
                    promoPieces.add(new Bishop(currentColor, 9, 4));
                    promoPieces.add(new Queen(currentColor, 9, 5));
                    return true;

                }
            }
            return false;
        }
     private void promoting(){
             if(mouse.pressed){
                for(Piece piece : promoPieces){
                    if(piece.col== mouse.x/Board.square_size && piece.row==mouse.y/Board.square_size){
                        switch(piece.type){
                            case ROOK: simPieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
                            case KNIGHT: simPieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
                            case BISHOP: simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
                            case QUEEN: simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
                            default: break;
                        }
                        simPieces.remove(activeP.getIndex());
                        copyPieces(simPieces, pieces);
                        activeP=null;
                        promotions=false;
            
                        if(isKingInCheck()&&isCheckmate())
                        gameOver=true;
                        else if(isStalemate() && isKingInCheck()==false){
                            Stalemate=true;
                        }
                       else{
                         changePlayer();
                        }
                    }
                }
             }

     }
     private boolean opponentCanCaptureKing(){
        Piece king=getKing(false);
        for(Piece piece :simPieces){
            if(piece.color!=king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
     }
     private boolean isIllegal(Piece king){
        if(king.type==Type.KING){
            for(Piece piece:simPieces){
                if(piece!=king && piece.color!=king.color && piece.canMove(king.col, king.row)){
                    return true; // The king is in check
                }
            }
        }
        return false;
     }
    //  public void dsm(){
    //     Graphics2D g2=(Graphics2D)getGraphics();
    //     g2.setColor(Color.red);
    //     g2.setFont(new Font("Arial", Font.BOLD, 30));
    //     if(currentColor==white){
    //         g2.drawString("black is in check", 750, 650);
    //     }else{
    //         g2.drawString("white is in check", 750, 100);
    //  }
    // }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        board.draw(g2);
        //pieces
        for(Piece p:new ArrayList<>(simPieces)){
            p.draw(g2);
        }
        if(activeP!=null){
            if(canMove){
                if(isIllegal(activeP)  || opponentCanCaptureKing()){
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.square_size, activeP.row*Board.square_size, 
                    Board.square_size, Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
                }else{
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.square_size, activeP.row*Board.square_size, 
                    Board.square_size, Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f)); 
                }
            }
            g2.setColor(new Color(200,200,200,180));
              for(Positions pos: positions){
                     int x = pos.col * Board.square_size;
                     int y = pos.row * Board.square_size;
                     int diameter = Board.square_size / 3;
                     if(pos.opponentP){
                        g2.setColor(new Color(220, 0, 0, 200)); // semi-transparent red
                        g2.setStroke(new BasicStroke(4));
                        g2.drawOval(x + Board.square_size / 3, y + Board.square_size / 3, diameter, diameter);
                        g2.setStroke(new BasicStroke(1)); // reset stroke
                     }else{
                      g2.setColor(new Color(200,200,200,180));
                     g2.fillOval(x + Board.square_size / 3, y + Board.square_size / 3, diameter, diameter);
                     }
              }
               
            g2.setColor(new Color(255, 255, 153, 180)); // last value is alpha for transparency
            g2.fillRect(activeP.precol * Board.square_size, activeP.prerow * Board.square_size,
                Board.square_size, Board.square_size);
            activeP.draw(g2);// draw the active piece
        }else{
            if(recentlyMovedP!=null){
                g2.setColor(new Color(255, 255, 100, 180)); // last value is alpha for transparency
                g2.fillRect(recentlyMovedP.precol * Board.square_size, recentlyMovedP.prerow * Board.square_size,
                    Board.square_size, Board.square_size);
                g2.setColor(new Color(255, 255, 153, 180));
                g2.fillRect(recentlyMovedPprecol * Board.square_size, recentlyMovedPprerow* Board.square_size,
                    Board.square_size, Board.square_size);
                    if(recentlyMovedP.type!=Type.PAWN|| recentlyMovedP.row!=0 && recentlyMovedP.row!=7)
                        recentlyMovedP.draw(g2); // draw the recently moved piece
                
            }
        }
        //status meaaage
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.white);

        if(promotions){
            g2.drawString("Promote to:", 750, 100);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getx(piece.col), piece.gety(piece.row), 
                Board.square_size, Board.square_size, null);
            }
        }else{
            if (checkingP != null && activeP ==null) {
                  Piece king = getKing(false); // opponent king if you want to highlight opponent, use getKing(false) for current player
                  g2.setColor(Color.red);
                  g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // semi-transparent
                  g2.fillRect(king.col * Board.square_size, king.row * Board.square_size, Board.square_size, Board.square_size);
                  g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset transparency
                  king.draw(g2);
            }


            if(currentColor==white){
                g2.drawString("White's turn", 750, 500);
                if(checkingP!=null && checkingP.color==black){
                    g2.setColor(Color.red);
                    g2.drawString("white is in check", 750, 520);
                }
            }else{
                g2.drawString("Black's turn", 750, 200);
                if(checkingP!=null && checkingP.color==white){
                    g2.setColor(Color.red);
                    g2.drawString("black is in check", 750, 150);
                }
            }
             if(capturedPieces.size()>0){

                g2.setColor(Color.white);
                g2.fillRect(640, 10, 450, 80); // Adjust position and size as needed
                g2.fillRect(640, 550, 450, 80);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(Color.black);
                g2.drawString("Captured Pieces:", 650, 30);
                g2.drawString("Captured Pieces: ", 650, 570);
               int  wx=650;
               int  wy=40;
               int bx=650;
               int by=570;
               for(Piece piece: new ArrayList<>(capturedPieces)){
                   if(piece.color==white){
                          g2.drawImage(piece.image, wx, wy, Board.square_size/2, Board.square_size/2, null);
                          wx+=Board.square_size/2+5;
                     }else{
                        g2.drawImage(piece.image, bx, by, Board.square_size/2, Board.square_size/2, null);
                        bx+=Board.square_size/2+5;
                     }
                   }
            }
        if(gameOver){
            String s="";
            if(currentColor==white){
                s="White wins";
            }else{
                s="Black Wins";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 100));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 350);
        }
        if(Stalemate){
            g2.setFont(new Font("Arial", Font.PLAIN, 100));
            g2.setColor(Color.green);
            g2.drawString("Stalemate", 200, 350);
        }
        
    }
  }
}
