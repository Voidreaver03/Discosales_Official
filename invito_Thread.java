/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_discord;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author annal
 */
public class invito_Thread extends Thread {

 @Override
 public void run(){
     try {
         String invito=FXMLDocumentController.br.readLine();
         String[] msg=invito.split(",");
         
         if (!msg[0].equals("//invita//")) {
             FXMLDocumentController.porta=Integer.parseInt(invito);
         }else{
            FXMLDocumentController.Invito=msg[1]+" ti ha invitato nella chat: "+msg[2];
            FXMLDocumentController.fleg=true; 
         }
     } catch (IOException ex) {
         Logger.getLogger(invito_Thread.class.getName()).log(Level.SEVERE, null, ex);
     }
 }
}
