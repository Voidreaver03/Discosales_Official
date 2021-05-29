/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static serverdiscord.ServerDiscord.ChatRoom;
import static serverdiscord.ServerDiscord.contatore;
import static serverdiscord.ServerDiscord.lis;
import static serverdiscord.ServerDiscord.port;

/**
 *
 * @author annal
 */
public class ServerThread extends Thread {
     Socket sok;
    int chat=0;
    String listas[];
    boolean login=false;
    int posizione;
    PrintWriter pr; 
    int trovato=0;
    
    public ServerThread(Socket socket){
        this.sok=socket;   
    }
    
    
    @Override
    public void run(){
         
        InputStream inp;
        
        try {
            
            inp=sok.getInputStream(); 
            BufferedReader bre=new BufferedReader(new InputStreamReader(inp));
            OutputStream out=sok.getOutputStream();
            pr=new PrintWriter(out,true);
            String message=bre.readLine();
            listas=message.split(",");
            String room="";
            
            if ("1".equals(listas[0])) {     //SCRIVERE DAL CLIENT 1,NOME,COGNOME,MAIL,USERNAME,PASSWORD
               
               for (int i=0; i<lis.size(); i++) {
                
                    if ( lis.get(i).Username.equals(listas[4])) {
                       
                        trovato++;
                    }
                    
                } 
               if(trovato==0){ 
                   
                    profilo p=new profilo(listas[1], listas[2], listas [3], listas[4],listas[5]);
                    lis.add(p);
                    UserSave(ServerDiscord.path);
                    pr.println("OK");
                    
               }else{
                   
                   pr.println("NO");
                   trovato=0;
                   
               }
               
               
            }else if ("2".equals(listas[0])) {          //scrivere dal client per login 2, username,password,nomechet
               
                for (int i=0; i<lis.size(); i++) {
                
                    if ( lis.get(i).Restituisci(listas[1],listas[3])) {
                       
                        lis.get(i).s=this;
                        pr.println("OK"); 
                        room=listas[2];
                        posizione=i;    //FARE CONTROLLO LATO CLIENT
                        login=true;
                    }
                    
                }
                
                if (login==false) {
                    pr.println("ERRORE"); 
                }

            }
            
            if (login) {
            
                pr.println(lis.get(posizione).Stringa());

                if (listas[2].isEmpty()) {
                    
                    
                    room=bre.readLine();
                    String m[]=room.split(",");
                    
                    while(m[0].equals("mod_profilo")){

                        //gestione modifica
                        lis.get(posizione).setNome(m[1]);
                        lis.get(posizione).setCognome(m[2]);
                        lis.get(posizione).setEmail(m[3]);
                        lis.get(posizione).Username=m[4];
                        lis.get(posizione).password=m[5];


                       room=bre.readLine();
                       m=room.split(",");
                    }
                }


                for (int i=0; i <ChatRoom.size(); i++) {
                   
                    if (ChatRoom.get(i).nome.equals(room)) {
                        chat++;
                        pr.println(ChatRoom.get(i).porta);      //scrivo al client la porta
                       
                    }
                }
                
                if (chat==0) {
                    
                    ChatThread ct=new ChatThread(room,sok,port+contatore);
                    contatore++;
                    ChatRoom.add(ct);  
                    ct.start();         
                }
            }

            } catch (IOException ex) {
                   System.out.println("DISCONNESSO");
            } 
    }
    
    
    
     public void UserSave(String path) throws IOException{

        File f=new File(path+"\\Discosales");

       if (!f.exists()) {
           f.mkdir();
       }
       File f2=new File(path+"\\Discosales"+"\\user.txt");

       if (!f2.exists()) {
           f2.createNewFile();
       }

       FileOutputStream out=new FileOutputStream(f2);
       ObjectOutputStream ob=new ObjectOutputStream(out);
       ob.writeObject(ServerDiscord.lis);
       ob.flush();
       ob.close();
    }
  }

