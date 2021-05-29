/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author annal
 */
public class ServerChat {
    
    Vector <UserThread> UserThreads=new Vector(); 
    Vector <String> Username= new Vector();
    int port;
    String nome;
    String aggiorna="";
    static boolean cancella=false;
  //  String path="C:\\Users\\annal\\Desktop\\Discosales";

    public ServerChat(int porta,String name){
        
        port=porta;
        nome=name;
    }   


    public void execute() throws IOException{

       ServerSocket ss=new ServerSocket(port);
       System.out.println("Chat Server is listening on port " + port);
       leggi();
       
       while(true){

           Socket soc=ss.accept();
           System.out.println("New user connected");
           UserThread us=new UserThread(soc,this);
           us.copia(aggiorna);
           UserThreads.add(us);
           us.start();

        }   
    }

    public void AggiungiUserName(String nome){

        Username.add(nome);


    }

    public void Broadcast(UserThread ust, String message){

        for (UserThread i: UserThreads) {

            if (i!=ust) {
                   i.MandaMessaggio(message);

            }
        }
        
        if (!message.equals("//cancella_chat//")) {
            aggiorna+=message+"\n";   
        }
    }


    public void UserSave(String path) throws IOException{

        File f=new File(path);

       if (!f.exists()) {
           f.mkdir();
       }
       File f2=new File(path+"\\user.txt");

       if (!f2.exists()) {
           f2.createNewFile();
       }

       FileOutputStream out=new FileOutputStream(f2);
       ObjectOutputStream ob=new ObjectOutputStream(out);
       ob.writeObject(ServerDiscord.lis);
       ob.flush();
       ob.close();
    }
    
    
    
    public void Save() throws IOException{
        
        File f0=new File(ServerDiscord.path+"\\Discosales"+"\\chat\\");
        
        if(!f0.isDirectory() || !f0.exists()){
            f0.mkdir();
        }
        
        File f2=new File(ServerDiscord.path+"\\Discosales"+"\\chat\\"+nome);
        
        if(!f2.isDirectory() || !f2.exists()){
            f2.mkdir();
        }
        
       
        File f1= new File(ServerDiscord.path+"\\Discosales"+"\\chat\\"+nome+"\\chat.txt");

        if(!f1.exists()){
            f1.createNewFile();
        }

        if(f1.exists()){
            
            FileWriter fw1= new FileWriter(f1);
            BufferedWriter fb1=new BufferedWriter(fw1);
            fb1.write(aggiorna);
            fb1.flush();

            fb1.close();
            fw1.close();

        }
        
        if(cancella){
            
            f1.delete();
            f2.delete();
            
        }
    }
    

    public void leggi() throws IOException{
         File f= new File(ServerDiscord.path+"\\Discosales"+"\\chat\\"+nome+"\\chat.txt");

         if (f.exists()){

            FileReader fr=new FileReader(f);
            BufferedReader bw=new BufferedReader(fr);

            String temp=bw.readLine();

            while(temp!=null){

            aggiorna=aggiorna+temp+"\n";
            temp=bw.readLine();
         }

         bw.close();
       }
    }


    public void cancella(){
        aggiorna="";
    }
}
