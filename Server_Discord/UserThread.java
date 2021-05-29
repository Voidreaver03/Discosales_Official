
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author annal
 */
public class UserThread extends Thread {
    
    Socket soc;
    ServerChat sc;
    String UserName;
    PrintWriter pr;
    String text="";
    String connessi="";

    public UserThread(Socket soc, ServerChat sc){
        this.soc=soc;
        this.sc=sc;
    }

    @Override
    public void run(){
        

        try {
    
            InputStream   in = soc.getInputStream();
            BufferedReader bre=new BufferedReader(new InputStreamReader(in));
            OutputStream out=soc.getOutputStream();
            pr=new PrintWriter(out,true);
            UserName= bre.readLine();  //Inviare dal client lo UserName

            stampaUtenti();                   //gestire lato client

            sc.AggiungiUserName(UserName);
            pr.println(text+connessi);
            String ServerMessage="NUOVO UTENTE CONNESSO: "+UserName;
            sc.Broadcast(this, ServerMessage);
            String clientMessagio;
            String ServerMessagio;
            String[] modifica;

            do {

                clientMessagio=bre.readLine();
                modifica=clientMessagio.split(",");
                
                if ("mod_profilo".equals(modifica[0])) {
                    for(profilo a:ServerDiscord.lis){

                        if(a.Restituisci(modifica[1], modifica[2])){

                            a.setNome(modifica[3]);
                            a.setCognome(modifica[4]);
                            a.setEmail(modifica[5]);
                            a.Username=modifica[6];
                            a.password=modifica[7];

                            for(String s:sc.Username){
                                if(s.equals(modifica[1])) s=modifica[6];
                            }
                        }
                    }
                    
                }else if ("//cancella_chat//".equals(modifica[0])){
                    
                    sc.cancella();
                    sc.Broadcast(this, clientMessagio);
                    if(modifica.length==2){
                    if(modifica[1].equals("room//")){
                    ServerChat.cancella=true;
                        }
                        }
                
                }else if ("//invita//".equals(modifica[0])){ 
                
                    for (profilo p: ServerDiscord.lis) {
                        if (p.Username.equals(modifica[1])) {
                            if (p.s != null) {
                                p.s.pr.println("//invita//,"+UserName+","+modifica[2]);
                            } 
                        }
                    }
                    
                 }else{
                        ServerMessagio=UserName+": "+clientMessagio;
                        sc.Broadcast(this, ServerMessagio);
                }
                
            } while (!"exit".equals(clientMessagio));

            String ServerMessag="UTENTE DISCONNESSO: "+UserName;
            System.out.println(ServerMessag);
            this.MandaMessaggio("//exit//");
            sc.Broadcast(this, ServerMessag);    
            sc.Username.remove(UserName);
            sc.UserThreads.remove(this);
            sc.UserSave(ServerDiscord.path+"\\Discosales");
            sc.Save();
            

        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }


      public void stampaUtenti(){
          
          if (sc.Username.isEmpty()) {

                connessi="NESSUN UTENTE CONNESSO";
                
               
          }else{

                connessi="GLI UTENTI CONNESSI SONO: "+sc.Username;
                
          }

      }


      public void MandaMessaggio(String message){

          pr.println(message);
         
      }
      
      
      public void copia(String c){
            text=c;   
      }

}
