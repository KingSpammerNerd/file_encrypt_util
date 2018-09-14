package file_encrypt_helper.helpers;

//Helper class to implement file encryption.
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

//Requires: An input stream, output stream, Cipher, MessageDigest, Base64 encoder, File (for directory name)

//Encryption helper class:
class EncHelper {
	//File input stream:
	BufferedReader file_in=null;
	//Encrypted file output streams:
	BufferedWriter hash_out=null;
	BufferedWriter enc_file_out=null;
	//Cipher for encryption:
	Cipher encryptor=null;
	//MessageDigest for hashing:
	MessageDigest hasher256=null;
	//Base64 encoder:
	Base64.Encoder b64_enc=null;
	
	//Original file contents:
	String orig_file_contents=null;
	
	//Constructor, takes a file name and a password for encryption:
	public EncHelper(String filename, String pass) throws IOException {
		//Open file input stream:
		this.file_in=new BufferedReader(new FileReader(new File(filename)));
		//Create destination files:
		this.createFiles();
		//Create Base64 encoder:
		this.b64_enc=Base64.getEncoder();
	}
	
	//Created target files:
	private void createFiles() throws IOException, RuntimeException {
		//Open encrypted file output and base64 hash file output:
		StringTokenizer efo=new StringTokenizer(filename, ".");
		String efo_filename=efo.nextToken();
		
		//Create target directory, throw RuntimeException if it already exists:
		if(new File(efo_filename).exists()) {
			Scanner s=new Scanner(System.in);
			System.out.print("Error: directory " + '"' + efo_filename + '"' + " already exists! Overwrite? [y/n]: ");
			char ch=s.nextLine().charAt(0);
			s.close();
			if(!(ch=='Y' || ch=='y')) throw new RuntimeException("ERROR_TARGET_FOLDER_EXISTS");
		}
		
		//Create target directory:
		File target_dir=new File(efo_filename).createDirectory();
		//Create files:
		File hash_file=new File(efo_filename + "/" + enc_filename + ".sha256");
		File enc_file=new File(efo_filename + "/" + efo_filename + ".enc");
		
		//Open file output streams:
		this.hash_out=new BufferedWriter(new FileWriter(hash_file));
		this.enc_file_out=new BufferedWriter(new FileWriter(enc_file));
	}
	
	//Reads contents of file to memory:
	private void readFile() throws IOException {
		String temp=null; StringBuffer con=new StringBuffer();
		
		//Read contents, with newlines:
		while(temp=this.file_in.readLine()) {
			con.append(temp);
			con.append('\n');
		}
		
		//Remove trailing newline:
		con.deleteCharAt(con.length()-1);
		
		//Store contents:
		this.orig_file_contents=con.toString();
	}
	
	//Hashes file:
	public void doHash() throws IOException {
		//Create MessageDigest:
		this.hasher256=MessageDigest.getInstance("SHA-256");
		
		//Hash text:
		byte[] hash=this.hasher256.digest(orig_file.getBytes("UTF-8"));
		
		//Encode hash to Base64:
		byte[] hash64=b64_enc.encode(hash);
		String hashed64=new String(hash64, "UTF-8");
		
		//Write hash to file:
		hash_out.write(hashed64);
	}
	
	//Encrypts file:
	public void doEncrypt() {
		//Create Cipher:
		this.
	}
	
	//Close all file streams (call from main!):
	public void finish() throws IOException {
		//Close input stream:
		file_in.close();
		//Close hash output stream:
		hash_out.close();
		//Close enc output stream:
		enc_file_out.close();
	}
}
