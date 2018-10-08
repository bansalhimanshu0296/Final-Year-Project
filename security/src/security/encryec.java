package security;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import static java.awt.SystemColor.text;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Key;
import java.sql.SQLException;
import javax.swing.*;
import java.util.Random ;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
public class encryec {
   byte[] skey = new byte[1000];
    String skeyString;
    static byte[] raw;
    String inputMessage, encryptedData, decryptedMessage;
    KeyPairGenerator kpg;
    EllipticCurve curve;
    ECParameterSpec ecSpec;
    KeyPair aKeyPair;
    static KeyAgreement aKeyAgree;
    KeyPair bKeyPair;
    KeyAgreement bKeyAgree;
    KeyFactory keyFac;
    static String msg;
static String filename;
 Connection con=null;
    PreparedStatement pst=null;
    ResultSet rs=null;
   public encryec(String fn)
{ filename=fn;
con=security.Security.connectdb();
func();
}
void AESgenerateSymmetricKey() {
try {
Random r = new Random();
int num = 100000;
String knum = String.valueOf(num);
byte[] knumb = knum.getBytes();
skey=AESgetRawKey(knumb);
skeyString = new String(skey);
}
catch(Exception e) {
System.out.println(e);
}
}
private static byte[] AESgetRawKey(byte[] seed) throws Exception {
KeyGenerator kgen = KeyGenerator.getInstance("AES");
SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
sr.setSeed(seed);
kgen.init(128, sr); // 192 and 256 bits may not be available
SecretKey skey = kgen.generateKey();
raw = skey.getEncoded();
return raw;
}
encryec()
{
    AESgenerateSymmetricKey();
}
private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
Cipher cipher = Cipher.getInstance("AES");
cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
byte[] encrypted = cipher.doFinal(clear);
return encrypted;
}
static String readFile(String fileName) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
     try {
        StringBuilder sb = new StringBuilder();
        String s = br.readLine();
        while (s != null) {
            sb.append(s);
            sb.append("\n");
            s = br.readLine();
        }
        return sb.toString();
    }
    finally {
        br.close();
    }
 }
   public void func()
 {
     String s2="";
     long then = System.currentTimeMillis();
  Security.addProvider(new BouncyCastleProvider());
  Scanner ss=new Scanner(System.in);
  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
 encryec en=new encryec();
   try{
    String path = "C:\\Users\\Kannu\\Documents\\NetBeansProjects\\security\\A";
  File filePublicKey = new File(path+"\\public.key");
   FileInputStream fis = new FileInputStream(path+"\\public.key");
   byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
   fis.read(encodedPublicKey);
   fis.close();
     // Read Private Key.
   File filePrivateKey = new File(path+"\\private.key");
   fis = new FileInputStream(path+"\\private.key");
   byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
   fis.read(encodedPrivateKey);
   fis.close();
     // Generate KeyPair.
   KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
   X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
     encodedPublicKey);
   PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
     PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
     encodedPrivateKey);
   PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
      aKeyAgree.init(privateKey);
      aKeyAgree.doPhase(publicKey, true);
        byte[] aBys = aKeyAgree.generateSecret(); 
      KeySpec aKeySpec = new DESKeySpec(aBys);
      SecretKeyFactory aFactory = SecretKeyFactory.getInstance("DES");
      Key aSecretKey = aFactory.generateSecret(aKeySpec);
      Cipher aCipher = Cipher.getInstance(aSecretKey.getAlgorithm());   
      aCipher.init(Cipher.ENCRYPT_MODE, aSecretKey);  
      String s = readFile(filename);
      byte[] encText = aCipher.doFinal(s.getBytes());
      byte[] ebyte=encrypt(raw, encText);
      try{
            String s1=new String(Base64.encodeBase64String(ebyte));
            s2=filename;
            s2=s2.substring(s2.lastIndexOf('\\')+1, s2.lastIndexOf('.'));
            pst=con.prepareStatement("insert into fatsecret values (?,?)");
            pst.setString(1,s2);
            pst.setString(2,s1);
            pst.execute();
            s2="test\\"+s2+".txt";
            FileOutputStream fileOutputStream=new FileOutputStream(s2);
            fileOutputStream.write(ebyte);
   
        JOptionPane.showMessageDialog(null,"File has been Encrypted and entered into database");
        }catch(MySQLIntegrityConstraintViolationException e){
        JOptionPane.showMessageDialog(null,"File already exist enter with new name");
        }
      catch (SQLException ex) {
            Logger.getLogger(MAIN.class.getName()).log(Level.SEVERE, null, ex);
         }
      catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }
        finally{}
      long duration = System.currentTimeMillis() - then;
System.out.println("Call took " + duration + " milliseconds.");
  }
  catch(Exception e)
  {
   e.printStackTrace();
  }
 }
}
