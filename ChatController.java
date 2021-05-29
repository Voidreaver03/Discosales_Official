/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_discord;

import static client_discord.FXMLDocumentController.br;
import static client_discord.FXMLDocumentController.nomeChat;
import static client_discord.FXMLDocumentController.pr;
import static client_discord.FXMLDocumentController.s;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author annal
 */
public class ChatController extends Thread implements Initializable {
    
    @FXML
    private TextArea Chat;
    @FXML
    private TextField messaggio;
    @FXML
    private Pane Pane1;
    @FXML
    private Pane Pane2;
    @FXML
    private Button profilo;
    @FXML
    private Button back;
    @FXML
    private TextField nome_m;
    @FXML
    private TextField cognome_m;
    @FXML
    private TextField mail_m;
    @FXML
    private TextField username_m;
    @FXML
    private TextField password_m;
    @FXML
    private TextField use_inv;
    @FXML
    private Label nome;
    @FXML
    private Label invito;
    @FXML
    private TextField nchat;
    @FXML
    private Label titolo_chat;
    
    boolean fine=true;
    boolean fermato=false;
    String username_mod=FXMLDocumentController.username;
    
    String pass_mod=FXMLDocumentController.password;
    
    
    @Override
    public void run(){
        
        set();
        Chat.appendText(FXMLDocumentController.chat+"\n");
       
        while(fine){
        
           
                try {
             
                    
                    String message=br.readLine();
                    
                    String m[]=message.split(",");
                    if (message.equals("//cancella_chat//")) {
                        
                        Chat.clear();
                        
                    }else if(m[0].equals("//invita//")){
                        
                        invito.setText(m[1]+" ti ha invitato nella chat: "+m[2]);
                        
                    }
                     else{
                        Chat.appendText(message+ "\n");
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(ChatController.class.getName()).log(Level.SEVERE, null, ex);
                }
            
          

        }
        
        fermato=true;
    }
    
     @FXML
     public void profilPane(ActionEvent Event){
      if (Event.getSource().equals(profilo)) {
            Pane2.toFront();
        }else if (Event.getSource().equals(back)) {
            Pane1.toFront();
        }   
    }
     
    @FXML
    public void set(){

        nome.setText(FXMLDocumentController.username);
        nome_m.setPromptText(FXMLDocumentController.nome);
        cognome_m.setPromptText(FXMLDocumentController.cognome);
        mail_m.setPromptText(FXMLDocumentController.mail);
        username_m.setPromptText(FXMLDocumentController.username);
        password_m.setPromptText(FXMLDocumentController.password);
        titolo_chat.setText(FXMLDocumentController.nomeChat);
    }
    
    
    @FXML
    public void cambiaChat(ActionEvent event) throws IOException, InterruptedException{
       
        if(!nchat.getText().isEmpty()){
            
            fine=false;
            pr.println("exit");
            
            while(!fermato){
                
            }
            
            s=new Socket("localhost", 21000); 
            InputStream in = s.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            OutputStream out = s.getOutputStream();
            pr=new PrintWriter(out,true);

            String messaggio="2,"+FXMLDocumentController.username+","+nchat.getText()+","+FXMLDocumentController.password;
            pr.println(messaggio);
            nomeChat=nchat.getText();
            
            if (!"OK".equals(br.readLine())) {
                s.close();
            } 
            
            br.readLine();
            
            FXMLDocumentController.porta =Integer.parseInt(br.readLine());
            
            s.close();
            s=new Socket("localhost",FXMLDocumentController.porta);
            in = s.getInputStream();
            br=new BufferedReader(new InputStreamReader(in));
            out=s.getOutputStream();
            pr=new PrintWriter(out,true);
            pr.println(FXMLDocumentController.username);
            FXMLDocumentController.chat="";
            
            Stage stage = (Stage) Pane1.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("ChatGrafica.fxml"));
            stage.setOnCloseRequest(Event -> {
                System.exit(0);
            } );
            stage.setScene(new Scene(root));
            stage.setTitle(FXMLDocumentController.username + "");
       
            stage.show();
            
            
        }
    }
    
    
     @FXML
    public void InviaMessage(ActionEvent Event){
        
        String message=messaggio.getText();
        FXMLDocumentController.pr.println(message);
        Chat.appendText("Me: " +message+ "\n");
        messaggio.setText("");
        
        if (message.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
        
    }
    
    @FXML
    public void modificaProfilo(){
        if(!nome_m.getText().isEmpty()) FXMLDocumentController.nome=nome_m.getText();
        if(!cognome_m.getText().isEmpty()) FXMLDocumentController.cognome=cognome_m.getText();
        if(!mail_m.getText().isEmpty()) FXMLDocumentController.mail=mail_m.getText();
        if(!username_m.getText().isEmpty()) username_mod=username_m.getText();
        if(!password_m.getText().isEmpty()) pass_mod=password_m.getText();

        FXMLDocumentController.pr.println("mod_profilo,"+FXMLDocumentController.username+","+FXMLDocumentController.password+","+FXMLDocumentController.nome+","+FXMLDocumentController.cognome+","+FXMLDocumentController.mail+","+username_mod+","+pass_mod);
        FXMLDocumentController.username=username_mod;
        FXMLDocumentController.password=pass_mod;
        set();
    }
    
   
   @FXML
   public void cancella(ActionEvent Event){
         FXMLDocumentController.pr.println("//cancella_chat//");
            Chat.clear();
   }
   
   @FXML
   public void invita(ActionEvent Event){
        if (!use_inv.getText().isEmpty()) {
            FXMLDocumentController.pr.println("//invita//,"+use_inv.getText()+","+FXMLDocumentController.nomeChat); 
            invito.setText(use_inv.getText()+" : inivtato!");
            use_inv.setText("");
        }
    
   }
   @FXML
  public void cancella_room(ActionEvent Event){
        FXMLDocumentController.pr.println("//cancella_chat//,room//");
        Chat.clear();
        }
   
   @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.start();
    }    


}
