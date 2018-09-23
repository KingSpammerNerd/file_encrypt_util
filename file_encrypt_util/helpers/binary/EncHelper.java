package file_encrypt_util.helpers.binary;

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
public class EncHelper extends file_encrypt_util.helpers.Helper {
	//File input stream:
	private FileInputStream file_in=null;
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
	private byte[] orig_file_contents=null;
	
	//Target file contents:
	private String target_file_contents=null;
	
	//Constructor, takes a file name, target file name and an encryption key:
	public EncHelper(String orig_file_name, String target_file_name, String passkey) throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		//Save filenames:
		this.orig_file_name=orig_file_name;
		this.target_file_name=target_file_name;
		//Open input stream:
		File in=new File(orig_file_name);
		this.file_in=new FileInputStream(in);
		this.orig_file_contents=new byte[(int)in.length()];
		//Initialize MessageDigest:
		this.hasher256=MessageDigest.getInstance("SHA-256");
		//Create Base64 encoder:
		this.b64_enc=Base64.getEncoder();
		//Save password:
		this.passwd=new String(Arrays.copyOf(this.b64_enc.encode(this.hasher256.digest(passkey.getBytes("UTF-8"))), 16), "UTF-8");
	}
	
	//Function to read and store input file's contents:
	public void readInput() throws IOException {
		//Read file into this.orig_file_contents:
		file_in.read(this.orig_file_contents);
	}
	
	//Function to hash the file:
	public void hashInput() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		//Hash and store:
		byte[] hash64=this.hasher256.digest(this.orig_file_contents);
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
		this.IV64=this.b64_enc.encodeToString(iv);
		//Encrypt contents:
		byte[] enc=encrypter.doFinal(this.orig_file_contents);
		byte[] ence=this.b64_enc.encode(enc);
		this.enc64=new String(ence, "UTF-8");
	}
	
	//Function to write and verify the output file (returns true if file is "good", else returns false. Should NEVER return false during normal usage):
	public boolean writeOutput() throws IOException {
		//Open the file:
		this.file_out=new BufferedWriter(new FileWriter(new File(this.target_file_name)));
		//Concatenate the data. First the Initialization vector (this.IV64), then the encrypted contents (this.enc64), finally the SHA-256 hash of the encrypted file (this.hashed64):
		StringBuffer out_builder=new StringBuffer();
		out_builder.append(this.IV64);
		out_builder.append(',');
		out_builder.append(this.enc64);
		out_builder.append(',');
		out_builder.append(this.hashed64);
		this.target_file_contents=out_builder.toString();
		//Hash the concatenated contents:
		byte[] prehash=this.hasher256.digest(this.target_file_contents.getBytes("UTF-8"));
		String prehash64=this.b64_enc.encodeToString(prehash);
		//Write data to the file:
		this.file_out.write(target_file_contents);
		this.file_out.flush();
		//Re-read data from file:
		BufferedReader check_reader=new BufferedReader(new FileReader(new File(this.target_file_name)));
		String check=check_reader.readLine();
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
