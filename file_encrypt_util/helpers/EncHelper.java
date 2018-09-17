package file_encrypt_util.helpers;

//Helper class to implement file encryption.
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

//Requires: An input stream, output stream, Cipher, MessageDigest, Base64 encoder.
//Methods: constructor, readfile, hashfile, encryptfile, writefile.

//Encryption helper class:
class EncHelper {
	//File input stream:
	private BufferedReader file_in=null;
	//Encrypted file output streams:
	private BufferedWriter file_out=null;
	//Base64 encoded contents of encrypted file:
	private String enc64=null;
	//MessageDigest for hashing:
	private MessageDigest hasher256=null;
	//Base64 encoded SHA-256 hash of original file:
	private String hashed64=null;
	//Base64 encoder:
	private Base64.Encoder b64_enc=null;
	//Input file name:
	private String orig_file_name=null;
	//Target file name:
	private String target_file_name=null;
	//Initialization Vector:
	private String IV64=null;
	
	//Supplied encryption key:
	private String passwd=null;
	
	//Original file contents:
	private String orig_file_contents=null;
	
	//Target file contents:
	private String target_file_contents=null;
	
	//Constructor, takes a file name, target file name and an encryption key:
	public EncHelper(String orig_file_name, String target_file_name, String passkey) throws IOException {
		//Save filenames:
		this.orig_file_name=orig_file_name;
		this.target_file_name=target_file_name;
		//Open input stream:
		this.file_in=new BufferedReader(new FileReader(orig_file_name));
		//Save password:
		this.passwd=passkey;
		//Create Base64 encoder:
		this.b64_enc=Base64.getEncoder();
	}
	
	//Function to read and store input file's contents:
	public void readInput() throws IOException {
		//Iteratively read through file, line by line:
		StringBuffer contents=new StringBuffer();
		String temp=null;
		while((temp=file_in.readLine())!=null) {
			contents.append(temp);
			contents.append('\n');
		}
		//Remove trailing newline:
		contents.deleteCharAt(contents.length()-1);
		//Save contents:
		this.orig_file_contents=contents.toString();
	}
	
	//Function to hash the file:
	public void hashInput() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		//Initialize MessageDigest:
		this.hasher256=MessageDigest.getInstance("SHA-256");
		//Hash and store:
		byte[] hash64=this.hasher256.digest(this.orig_file_contents.getBytes("UTF-8"));
		this.hashed64=this.b64_enc.encodeToString(hash64);
	}
	
	//Function to encrypt the file:
	public void encryptInput() throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		//Create random number generator:
		SecureRandom sr=new SecureRandom();
		byte[] iv=new byte[16];
		//Create Cipher for encryption:
		Cipher encrypter=Cipher.getInstance("AES/CBC/PKCS5Padding");
		//Create SecretKeySpec:
		SecretKeySpec key=new SecretKeySpec(this.passwd.getBytes("UTF-8"), "AES");
		//Initialize Cipher:
		sr.nextBytes(iv);
		encrypter.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		//Store Initialization Vector:
		this.IV64=b64_enc.encodeToString(iv);
		//Encrypt contents:
		byte[] enc=encrypter.doFinal(this.orig_file_contents.getBytes("UTF-8"));
		this.enc64=new String(enc, "UTF-8");
	}
	
	//Function to write and verify the output file (returns true if file is "good", else returns false. Should NEVER return false during normal usage):
	public boolean writeOutput() throws IOException {
		//Open output file:
		File out=new File(this.target_file_name);
		//Throw exception if file exists. The program should prompt the user at this point:
		if(out.exists()) throw new IOException("OUT_FILE_EXISTS");
		//Open the file:
		this.file_out=new BufferedWriter(new FileWriter(out));
		//Concatenate the data. First the Initialization vector (this.IV64), then the encrypted contents (this.enc64), finally the SHA-256 hash of the encrypted file (this.hashed64):
		StringBuffer out_builder=new StringBuffer();
		out_builder.append(this.IV64);
		out_builder.append('\n');
		out_builder.append(this.enc64);
		out_builder.append('\n');
		out_builder.append(this.hashed64);
		this.target_file_contents=out_builder.toString();
		//Hash the concatenated contents:
		byte[] prehash=this.hasher256.digest(this.target_file_contents.getBytes("UTF-8"));
		String prehash64=this.b64_enc.encodeToString(prehash);
		//Write data to the file:
		this.file_out.write(target_file_contents);
		this.file_out.flush();
		//Re-read data from file:
		BufferedReader check_reader=new BufferedReader(new FileReader(out));
		String temp=null; StringBuilder checc=new StringBuilder();
		while((temp=check_reader.readLine())!=null) {
			checc.append(temp);
			checc.append('\n');
		}
		checc.deleteCharAt(checc.length()-1);
		String check=checc.toString();
		//Hash re-read data:
		byte[] posthash=this.hasher256.digest(check.getBytes("UTF-8"));
		String posthash64=this.b64_enc.encodeToString(posthash);
		//Check if hashes match:
		if(prehash64.equals(posthash64))
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
