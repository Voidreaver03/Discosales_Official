/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_discord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static jdk.nashorn.tools.ShellFunctions.input;

/**
 *
 * @author annal
 */
public class FXMLDocumentController implements Initializable {
static BufferedReader br; 
    InputStream in;
    static PrintWriter pr;
    OutputStream out;
    static Socket s;
    static String nome;
    static String cognome;
    static String mail;
    static String password;
    static String chat;
    static String username;
    static int porta;
    static String Invito;
    static boolean fleg=false;
    invito_Thread inv;
    static String nomeChat;
    @FXML
    private TextField NomeProf;
    @FXML
    private TextField CognomeProf;
    @FXML
    private TextField MailProf;
    @FXML
    private TextField UsernameProf;
    @FXML
    private TextField PasswordProf;
    @FXML
    private TextField Nome;
    @FXML
    private TextField Cognome;
    @FXML
    private TextField Mail;
    @FXML
    private TextField Usernamereg;
    @FXML
    private TextField Passwordreg;
    @FXML
    private TextField UsernameLog;
    @FXML
    private TextField PasswordLog;
    @FXML
    private TextField chat_then;
    @FXML
    private TextField Chat;
    @FXML
    private Pane Pane1;
    @FXML
    private Pane Pane2;
    @FXML
    private Pane Pane3;
    @FXML
    private Label error;
    @FXML
    private Label riuscito;
    @FXML
    private Label invito;
    @FXML
    private Button Modifica;
    @FXML
    private Button EnterChat;
    @FXML
    private Button btnReg;
    @FXML
    private Button btnLog;
  
  
    @FXML
    private void Registrati(ActionEvent event) {
        
    try { 
        s=new Socket("localhost", 21000);
        in = s.getInputStream();
        br=new BufferedReader(new InputStreamReader(in));
        out=s.getOutputStream();
        pr=new PrintWriter(out,true);
        
        if(!Nome.getText().isEmpty() && !Cognome.getText().isEmpty() && !Mail.getText().isEmpty() && !Usernamereg.getText().isEmpty() && !Passwordreg.getText().isEmpty()){
           
            String messaggio="1,"+Nome.getText()+","+Cognome.getText()+","+Mail.getText()+","+Usernamereg.getText()+","+Passwordreg.getText();
            pr.println(messaggio);
            
            if (br.readLine().equals("OK")) {
                riuscito.setTextFill(Color.web("#006400"));
                riuscito.setText("registrazione avvenuta con successo");
            }else{
                riuscito.setTextFill(Color.web("#DC143C"));
                riuscito.setText("Username non disponibile!");
            }
            
            
        }else{
            riuscito.setTextFill(Color.web("#DC143C"));
            riuscito.setText("Completare tutti i campi");
        }
        
        
        s.close();
        
    } catch (IOException ex) {
            riuscito.setTextFill(Color.web("#DC143C"));
            riuscito.setText("Errore di connessione al server!");
    }
        
    }
    
    @FXML
    private void Login(ActionEvent event) throws IOException{
        
        if (connected()==1) {
            
            cambiaFinestra();
            
        }else if (connected()==2) {
                inv=new invito_Thread();
                inv.start();
        }else {
            error.setText("nome o password errati");
        }
        
    }
    
    private int connected() throws IOException{
        
        s=new Socket("localhost", 21000); 
        in = s.getInputStream();
        br=new BufferedReader(new InputStreamReader(in));
        out=s.getOutputStream();
        pr=new PrintWriter(out,true);
        username=UsernameLog.getText();
        
        if(UsernameLog.getText().isEmpty() || PasswordLog.getText().isEmpty()) return 0;
        
        String messaggio="2,"+UsernameLog.getText()+","+Chat.getText()+","+PasswordLog.getText();
        pr.println(messaggio);
        nomeChat=Chat.getText();
        
        if (!"OK".equals(br.readLine())) {
            s.close();
            
            return 0;    
        } 
     
        String[] listaProfile=br.readLine().split(",");
        nome=listaProfile[0];
        cognome=listaProfile[1];
        mail=listaProfile[2];
        username=listaProfile[3];
        password=listaProfile[4];
     
        if (Chat.getText().isEmpty()) {
            
            Pane3.toFront();
            set();
            
            return 2;
        }
        
        porta =Integer.parseInt(br.readLine());
        s=new Socket("localhost",porta);
        in = s.getInputStream();
        br=new BufferedReader(new InputStreamReader(in));
        out=s.getOutputStream();
        pr=new PrintWriter(out,true);
        pr.println(username);
        
        chat=br.readLine();
        return 1;
    }
   
    
    public void set(){
        
        NomeProf.setPromptText(nome);
        CognomeProf.setPromptText(cognome);
        MailProf.setPromptText(mail);
        UsernameProf.setPromptText(username);
        PasswordProf.setPromptText(password);
        
    }
    
    
    public void  cambiaFinestra() throws IOException {
       
        Stage stage = (Stage) UsernameLog.getScene().getWindow();
        Parent root = FXMLLoader.load(this.getClass().getResource("ChatGrafica.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle(username + "");
       
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        
        stage.show();
        
    }
    
    
    @FXML
    public void cambiaPane(ActionEvent event){
        
        if (event.getSource().equals(btnReg)) {
            Pane2.toFront();
        }else if (event.getSource().equals(btnLog)) {
            Pane1.toFront();
        }
        
    }
    
    
    @FXML
    public void modifica(ActionEvent event){
        
        if(!NomeProf.getText().isEmpty()) nome=NomeProf.getText();
        if(!CognomeProf.getText().isEmpty()) cognome=CognomeProf.getText();
        if(!MailProf.getText().isEmpty()) mail=MailProf.getText();
        if(!UsernameProf.getText().isEmpty()) username=UsernameProf.getText();
        if(!PasswordProf.getText().isEmpty()) password=PasswordProf.getText();
        
        pr.println("mod_profilo,"+nome+","+cognome+","+mail+","+username+","+password);
        set();
        invito.setText("MODIFICATO");
        
    }
    
    
     @FXML
    public void inviaChat(ActionEvent event) throws InterruptedException, IOException{
            
        if(!chat_then.getText().isEmpty()){
                
                pr.println(chat_then.getText());
                nomeChat=chat_then.getText();

                if(inv.isAlive()){
                    inv.join();
                }

                if(fleg){
                  
                    porta=Integer.parseInt(br.readLine());
                    invito.setText(Invito);

                }
                
                s=new Socket("localhost",porta);
                OutputStream output = s.getOutputStream();
                InputStream input = s.getInputStream();
                br = new BufferedReader(new InputStreamReader(input));
                pr = new PrintWriter(output, true);
                pr.println(username);
                
                chat=br.readLine();
                cambiaFinestra();

                }else{
                    if(fleg){
                        invito.setText(Invito);
                    }
                }    
    }
    
    
    @FXML
    public void controlla(){
        if (fleg) {
            invito.setText(Invito);   
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
