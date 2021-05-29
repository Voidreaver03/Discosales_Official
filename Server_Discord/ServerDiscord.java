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
                path=sc.next();
                File f=new File(path);
                if(f.exists() && f.isDirectory()) break;
                else System.out.println("percorso non disponibile");
            }       
            sd.lettura();
            sd.execute();

    }
 
}
