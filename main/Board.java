/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import java.awt.Graphics2D;
import java.awt.Color;
public class Board {
    final int max_col=8;
    final int max_row=8;
    public static final int square_size=80;
    public static final int half_square_size=square_size/2;
    public void draw(Graphics2D g2){
           int c=0;
        for(int i=0;i<max_row;i++){    
            for(int j=0;j<max_col;j++){
                if(c==0){
                    g2.setColor(new Color(210,165,125));
                    c=1;
                }else{
                    g2.setColor(new Color(175,115,70));
                   c=0;
                }
                g2.fillRect(j*square_size, i*square_size, square_size, square_size);
            }
            if(c==0){
                c=1;
            }else{
                c=0;
            }
        }
    }
}
