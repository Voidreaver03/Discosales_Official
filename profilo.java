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
