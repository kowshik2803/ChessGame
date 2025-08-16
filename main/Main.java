/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import javax.swing.JFrame;
public class Main {
    public static void main(String[] args){
        JFrame window =new JFrame("CHESS");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        GamePanel gp=new GamePanel();
        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gp.launchGame();
    }
}
