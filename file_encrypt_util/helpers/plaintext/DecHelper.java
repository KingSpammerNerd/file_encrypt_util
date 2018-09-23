package file_encrypt_util.helpers.plaintext;

//Helper class to implement file decryption.
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

//Requires: An input stream, output stream, Cipher, MessageDigest, Base64 decoder

//Decryption helper class:
public class DecHelper {
	//File input streams:
	private BufferedReader file_in=null;
	//File output stream:
	private BufferedWriter file_out=null;
	//Base64 decoder:
	private Base64.Decoder b64_dec=null;
	//MessageDigest, for hashing:
	private MessageDigest hasher256=null;
	//Encryption key:
	private String passwd=null;
	
	//Original file name:
	private String orig_file_name=null;
	//Target file name:
	private String target_file_name=null;
	
	//Original file contents:
	//Initialization Vector;
	private String IV64=null;
	//Encrypted contents:
	private String enc64=null;
	//SHA-256 hash of original file contents (Base64-encoded):
	private String hash64=null;
	
	//Decrypted contents:
	private String dec=null;
	
	//Constructor, takes the original file name, target file name and the decryption key:
	public DecHelper(String orig_file_name, String target_file_name, String passkey) throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		//Save filenames:
		this.orig_file_name=orig_file_name;
		this.target_file_name=target_file_name;
		//Open input stream:
		this.file_in=new BufferedReader(new FileReader(orig_file_name));
		//Initialize MessageDigest:
		this.hasher256=MessageDigest.getInstance("SHA-256");
		//Create Base64 encoder:
		this.b64_dec=Base64.getDecoder();
		//Save password:
		this.passwd=new String(Arrays.copyOf(Base64.getEncoder().encode(this.hasher256.digest(passkey.getBytes("UTF-8"))), 16), "UTF-8");
	}
	
	//Function to read and store input file's contents:
	public void readInput() throws IOException {
		//Read comma-delimited data:
		StringTokenizer full=new StringTokenizer(this.file_in.readLine(), ",");
		//Splits data:
		this.IV64=full.nextToken();
		this.enc64=full.nextToken();
		this.hash64=full.nextToken();
	}
	
	//Decrypt file contents:
	public void decryptInput() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		//Decode initialization vector:
		byte[] iv=this.b64_dec.decode(this.IV64.getBytes("UTF-8"));
		//Create IvParameterSpec:
		IvParameterSpec IV=new IvParameterSpec(iv);
		//Create SecretKeySpec:
		SecretKeySpec key=new SecretKeySpec(this.passwd.getBytes("UTF-8"), "AES");
		//Create Cipher:
		Cipher decrypter=Cipher.getInstance("AES/CBC/PKCS5Padding");
		//Initialize Cipher:
		decrypter.init(Cipher.DECRYPT_MODE, key, IV);
		//Decode and decrypt contents:
		byte[] enc=this.b64_dec.decode(this.enc64.getBytes("UTF-8"));
		byte[] decc=decrypter.doFinal(enc);
		this.dec=new String(decc, "UTF-8");
	}
	
	//Verify the decrypted contents:
	public boolean verifyContents() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] hashed=hasher256.digest(this.dec.getBytes("UTF-8"));
		String hash=Base64.getEncoder().encodeToString(hashed);
		//Compare hashes and return result:
		if(hash.equals(this.hash64))
			return true;
		else
			return false;
	}
	
	//Function to write and verify the output file (returns true if file is "good", else returns false. Should NEVER return false during normal usage):
	public boolean writeOutput() throws IOException {
		//Open the file:
		this.file_out=new BufferedWriter(new FileWriter(new File(this.target_file_name)));
		//Write data to the file:
		this.file_out.write(this.dec);
		this.file_out.flush();
		//Re-read data from file:
		BufferedReader check_reader=new BufferedReader(new FileReader(new File(this.target_file_name)));
		String temp=null; StringBuilder checc=new StringBuilder();
		while((temp=check_reader.readLine())!=null) {
			checc.append(temp);
			checc.append('\n');
		}
		checc.deleteCharAt(checc.length()-1);
		String check=checc.toString();
		//Hash re-read data:
		byte[] posthash=this.hasher256.digest(check.getBytes("UTF-8"));
		String posthash64=Base64.getEncoder().encodeToString(posthash);
		//Check if hashes match:
		if(this.hash64.equals(posthash64))
			return true;
		else
			return false; //Once again, this should NEVER HAPPEN!
	}
	
	//Function to close all streams:
	public void finish() throws IOException {
		this.file_in.close();
		this.file_out.close();
	}
}
