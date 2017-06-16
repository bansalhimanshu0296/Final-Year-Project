/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author sonaligoyal
 */
public class Security {

    /**
     * @param args the command line arguments
     */
    Connection conn=null;
public static Connection connectdb(){  
try{  
//step1 load the driver class  
Class.forName("com.mysql.jdbc.Driver").newInstance();  
  
//step2 create  the connection object  
Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:3305/c","root","re@lm@drid");  
  
//step3 create the statement object  
return conn;  
}
catch(Exception e){
    JOptionPane.showMessageDialog(null, e);
    return null;
}
}
    
}
