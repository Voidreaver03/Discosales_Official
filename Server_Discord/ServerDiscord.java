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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.Serializable;

/**
 *
 * @author annal
 */
public class profilo implements Serializable {
 String Username;
    String cognome;
    String Email;
    String nome;
    String password;
    transient ServerThread s;
    private static final long serialVersionUID=23284109;
    public profilo(String nome,String cognome,String Email,String login, String password){
        this.nome=nome;
        this.Username=login;
        this.cognome=cognome;
        this.Email=Email;
        this.password=password;
        
    }
    
    public String getNome(){
        return nome;
    }
    
    public String getCognome(){
        return cognome;
    }
    
    public String getEmail(){
        return Email;
    }
    
    public String getPassword(){
        return password;
    }
    
    public void setNome(String n){
        this.nome=n;
    }
    
    public void setCognome(String c){
        this.cognome=c;
    }
    
    public void setEmail(String e){
        this.Email=e;
    }  
    
    public void getPassword(String p){
        this.password=p;
    }  
    
    public boolean Restituisci(String usarname, String Password){
        
        if (Username.equals(usarname) & password.equals(Password)) {
           return true;
        }
        return false;
        
    }
    
    public String Stringa(){
         return nome+","+cognome+","+Email+","+Username+","+password;  
    }
    
}

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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverdiscord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/**
 *
 * @author annal
 */
public class ServerDiscord {
   
    static int port;
    static int contatore=1;
    static Vector <profilo> lis=new Vector(); 
    static Vector <ChatThread> ChatRoom=new Vector();
    static String path="";
    
    
    public ServerDiscord(int port){
        this.port=port;
    } 


    public void execute() throws IOException{
        
        ServerSocket sock=new ServerSocket(port);
        System.out.println("ServerDiscord is listening on port " + port);
        
        while(true){
            Socket sok= sock.accept();
            ServerThread st=new ServerThread(sok);
            st.start();
        }
    }


    public void lettura() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        File f=new File(path+"\\Discosales"+"\\user.txt");
        
        if (f.exists()) {

            FileInputStream In=new FileInputStream(f);
            ObjectInputStream obj=new ObjectInputStream(In);
            lis=(Vector<profilo>) obj.readObject();
            obj.close();

        }


    }

    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {

            Scanner sc =new Scanner(System.in);
            int port=21000;
            ServerDiscord sd=new ServerDiscord(port);
            while(true){
                System.out.println("inserire percorso a una cartella per salvataggio");
                //path=sc.next();
                path="C:\\Users\\annal\\Desktop";
                File f=new File(path);
                if(f.exists() && f.isDirectory()) break;
                else System.out.println("percorso non disponibile");
            }       
            sd.lettura();
            sd.execute();

    }
 
}
