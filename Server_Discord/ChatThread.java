/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author annal
 */
public class ChatThread extends Thread{
  
    String nome;
    Socket socket;
    int porta;
    
    
    public ChatThread(String name, Socket so, int porta){
        
        this.nome=name;
        this.socket=so;
        this.porta=porta;
   }
    
    
   @Override
     public void run(){
         
        try {
   
            OutputStream out=socket.getOutputStream();
            PrintWriter pw=new PrintWriter(out, true);
            ServerChat sc=new ServerChat(porta,nome);
            pw.println(porta);   //UTILIZZARE LATO CLIENT
            sc.execute();   
            
        } catch (IOException ex) {
            Logger.getLogger(ChatThread.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
