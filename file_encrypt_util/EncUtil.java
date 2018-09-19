package file_encrypt_util;
import file_encrypt_util.helpers.*;

//Application to encrypt and decrypt raw files.
//Encryption is done by:
//1. Encoding the file contents to Base64.
//2. Finding the SHA-256 hash of the Base64-encoded contents.
//3. Getting a password from the user, then encrypting the Base64-encoded contents with 256-bit AES/CBC/PKCS5Padding.
//4. Encode the encrypted data to Base64.
//5. Store the file as <filename>.fenc, with: Initialization Vector, Encrypted contents, Base64 encoded-hash
//Reverse the process for decryption.

//For file and terminal I/O:
import java.util.*;
import java.io.*;

//Main application Class:
public class EncUtil {
	//Scanner. We'll be reusing this one:
	private static Scanner s=null;
	
	//Main menu method:
	private static int mainMenu() {
		//Open keyboard input:
		s=new Scanner(System.in);
		//Show menu:
		System.out.println("\nChoose option:");
		System.out.println("\n1. Encrypt a file");
		System.out.println("2. Decrypt an encrypted file");
		System.out.println("3. Exit");
		System.out.print("\nEnter option: ");
		//Get input:
		int choice=Integer.parseInt(s.nextLine());
		//Return choice:
		return choice;
	}
	
	public static void main(String[] args) {
		//Check arguments:
		//No arguments taken as of now.
		if(args.length!=0) {
			System.out.println("Usage: file_enc_util");
			System.exit(1);
		}
		
		//Actual program code:
		while(true) {
			//Show menu:
			int action=mainMenu();
			//Execute selected action:
			switch(action) {
				//Encrypt file:
				case(1): {
					//Get input file name:
					System.out.print("Enter path to file to be encrypted: ");
					String filename=s.nextLine();
					//Check if file exists:
					if(!(new File(filename).exists())) {
						System.out.println("Error: " + filename + " does not exist.");
						break;
					}
					
					//Get output file name:
					System.out.print("Enter path to new output file: ");
					String outfilename=s.nextLine();
					//Check if output file already exists:
					File out_checker=new File(outfilename);
					if(out_checker.exists()) {
						System.out.print("Warning: " + outfilename + " already exists! Overwrite? [y/N]");
						char yn=s.nextLine().charAt(0);
						//If user responds with 'y', delete the file and continue, else break.
						if(yn=='y' || yn=='Y') {
							out_checker.delete();
						} else {
							break;
						}
					}
					
					//Get encryption key for file:
					System.out.print("Enter passcode to encrypt file (8+ chars recommended): ");
					String epass=s.nextLine();
					
					//Create EncHelper and read input file:
					EncHelper e=null;
					try {
						e=new EncHelper(filename, outfilename, epass);
						System.out.println("Reading file...");
						e.readInput();
					} catch(Exception e1) {
						System.out.println("I/O Error: " + e1.getMessage());
						break;
					}
					
					//Get and store input hash:
					try {
						System.out.println("Hashing file...");
						e.hashInput();
					} catch(Exception e2) {
						System.out.println("Error hashing input file: " + e2.getMessage());
						break;
					}
					
					//Encrypt file:
					try {
						System.out.println("Encrypting file...");
						e.encryptInput();
					} catch(Exception e3) {
						System.out.println("Error while encrypting file: " + e3.getMessage());
						break;
					}
					
					//Write output file and finish:
					try {
						System.out.println("Writing output...");
						if(!e.writeOutput()) throw new IOException("Hashes do not match!");
						System.out.println("Finishing up...");
						e.finish();
						System.out.println("Done.");
					} catch(IOException i) {
						System.out.println("I/O Error: " + i.getMessage());
						break;
					}
					break;
				}
				
				//Decrypt file:
				case(2): {
					//Get input file name:
					System.out.print("Enter path to file to be decrypted: ");
					String filename=s.nextLine();
					//Check if file exists:
					if(!(new File(filename).exists())) {
						System.out.println("Error: " + filename + " does not exist.");
						break;
					}
					
					//Get output file name:
					System.out.print("Enter path to output file: ");
					String outfilename=s.nextLine();
					//Check if output file already exists:
					File out_checker=new File(outfilename);
					if(out_checker.exists()) {
						System.out.print("Warning: " + outfilename + " already exists! Overwrite? [y/N]");
						char yn=s.nextLine().charAt(0);
						//If user responds with 'y', delete the file and continue, else break.
						if(yn=='y' || yn=='Y') {
							out_checker.delete();
						} else {
							break;
						}
					}
					
					//Get encryption key for file:
					System.out.print("Enter passcode to decrypt file: ");
					String dpass=s.nextLine();
					
					//Create DecHelper:
					DecHelper d=null;
					try {
						d=new DecHelper(filename, outfilename, dpass);
					} catch(Exception e1) {
						System.out.println("Error: " + e1.getMessage());
						break;
					}
					
					//Read input file:
					try {
						System.out.println("Reading file...");
						d.readInput();
					} catch(IOException i) {
						System.out.println("I/O Error: " + i.getMessage());
						break;
					}
					
					//Decrypt and verify file contents:
					try {
						System.out.println("Decrypting...");
						d.decryptInput();
						System.out.println("Verifying...");
						d.verifyContents();
					} catch(Exception e2) {
						System.out.println("Error while decrypting: " + e2.getMessage());
						break;
					}
					
					//Write output file and finish:
					try {
						System.out.println("Writing output...");
						if(!d.writeOutput()) throw new IOException("Hashes do not match!");
						System.out.println("Finishing up...");
						d.finish();
						System.out.println("Done.");
					} catch(IOException i) {
						System.out.println("I/O Error: " + i.getMessage());
						break;
					}
					break;
				}
				
				//Exit:
				case(3): {
					System.out.println("Exiting...");
					System.exit(0);
				}
				
				default: {
					System.out.println("Invalid option!");
					break;
				}
			}
		}
	}
}
