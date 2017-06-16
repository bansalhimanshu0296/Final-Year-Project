/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
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
import static security.encryec.aKeyAgree;
import static security.encryec.raw;

/**
 *
 * @author Kannu
 */
public class decryec {
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
    public decryec(String fn) throws SQLException, Exception
    {
        filename=fn;
        con=security.Security.connectdb();
        func();
    }
 private static void decrypt1(String encrypted) throws Exception {
     java.security.Security.addProvider(new BouncyCastleProvider());
    try{
   String path = "C:\\Users\\Kannu\\Documents\\NetBeansProjects\\security\\X"; 
    File filePublicKey = new File(path +"\\public.key");
   FileInputStream fis = new FileInputStream(path + "\\public.key");
   byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
   fis.read(encodedPublicKey);
   fis.close();
     // Read Private Key.
   File filePrivateKey = new File(path + "\\private.key");
   fis = new FileInputStream(path + "\\private.key");
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
      aCipher.init(Cipher.DECRYPT_MODE, aSecretKey); 
      byte[] decText = aCipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
      String text = new String(decText);
      JOptionPane.showMessageDialog(null,text);
       }
 catch(Exception e)
  {
   e.printStackTrace();
  }
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
kgen.init(128, sr);
SecretKey skey = kgen.generateKey();
raw = skey.getEncoded();
return raw;
}
 private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
Cipher cipher = Cipher.getInstance("AES");
cipher.init(Cipher.DECRYPT_MODE, skeySpec);
String en=new String(encrypted);
byte[] decrypted = cipher.doFinal(encrypted);
return decrypted;
}
    public void func() throws SQLException, Exception
    {
        
        pst=con.prepareStatement("select content from fatsecret where filename=?");
    pst.setString(1, filename);
    rs=pst.executeQuery();
    if(rs.next()){
    String content=rs.getString(1);
    byte[] ebyte=content.getBytes();
    FileInputStream fileInputStream=null;
    String s2="test\\"+filename+".txt";
                File file=new File(s2);
                byte[] dbyte1=new byte[(int)file.length()];
                try{
                    fileInputStream=new FileInputStream(file);
                    fileInputStream.read(dbyte1);
                    fileInputStream.close();
                }catch(Exception e){}
    AESgenerateSymmetricKey();
    byte[] dbyte= decrypt(raw,dbyte1);
    String dstr=new String(Base64.encodeBase64String(dbyte));
     decrypt1(dstr);
    }}
}
