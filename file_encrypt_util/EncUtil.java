package file_encrypt_util;

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
					Console pin=System.console();
					String epass=null;
					StringBuffer p=new StringBuffer();
					p.append(pin.readPassword());
					epass=p.toString();
					
					//Create Helper and read input file:
					//Lets user select between: 1. Plaintext, 2. Binary (basically all other files)
					file_encrypt_util.helpers.Helper e=null;
					System.out.println("Select the type of file:");
					System.out.println("1. Binary (default, unbuffered)");
					System.out.println("2. Plaintext (buffered)");
					System.out.print("Enter option: ");
					int type=Integer.parseInt(s.nextLine());
					try {
						switch(type) {
							case 1: {
								e=new file_encrypt_util.helpers.binary.EncHelper(filename, outfilename, epass);
								System.out.println("Reading as Binary file...");
								break;
							}
							case 2: {
								e=new file_encrypt_util.helpers.plaintext.EncHelper(filename, outfilename, epass);
								System.out.println("Reading as Plaintext file...");
								break;
							}
							default: {
								e=new file_encrypt_util.helpers.binary.EncHelper(filename, outfilename, epass);
								System.out.println("Invalid option, reading as Binary file...");
								break;
							}
						}
						e.readInput();
					} catch(Exception e1) {
						System.out.println("I/O Error: " + e1.getMessage());
						break;
					}
					
					//Get and store input hash:
					try {
						System.out.println("Hashing file...");
						//Call appropriate hashing method:
						if(e instanceof file_encrypt_util.helpers.binary.EncHelper) {
							((file_encrypt_util.helpers.binary.EncHelper)e).hashInput();
						} else if(e instanceof file_encrypt_util.helpers.plaintext.EncHelper) {
							((file_encrypt_util.helpers.plaintext.EncHelper)e).hashInput();
						}
					} catch(Exception e2) {
						System.out.println("Error hashing input file: " + e2.getMessage());
						break;
					}
					
					//Encrypt file:
					try {
						System.out.println("Encrypting file...");
						//Call appropriate encryption method:
						if(e instanceof file_encrypt_util.helpers.binary.EncHelper) {
							((file_encrypt_util.helpers.binary.EncHelper)e).encryptInput();
						} else if(e instanceof file_encrypt_util.helpers.plaintext.EncHelper) {
							((file_encrypt_util.helpers.plaintext.EncHelper)e).encryptInput();
						}
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
					Console pin=System.console();
					String dpass=null;
					StringBuffer p=new StringBuffer();
					p.append(pin.readPassword());
					dpass=p.toString();
					
					//Create Helper and read input file:
					//Lets user select between: 1. Plaintext, 2. Binary (basically all other files)
					file_encrypt_util.helpers.Helper d=null;
					System.out.println("Select the type of file that was encrypted:");
					System.out.println("1. Binary (default, unbuffered)");
					System.out.println("2. Plaintext (buffered)");
					System.out.print("Enter option: ");
					int type=Integer.parseInt(s.nextLine());
					try {
						switch(type) {
							case 1: {
								d=new file_encrypt_util.helpers.binary.DecHelper(filename, outfilename, dpass);
								System.out.println("Reading as Binary file...");
								break;
							}
							case 2: {
								d=new file_encrypt_util.helpers.plaintext.DecHelper(filename, outfilename, dpass);
								System.out.println("Reading as Plaintext file...");
								break;
							}
							default: {
								d=new file_encrypt_util.helpers.binary.DecHelper(filename, outfilename, dpass);
								System.out.println("Invalid option, reading as Binary file...");
								break;
							}
						}
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
						//Call appropriate decryption method:
						if(d instanceof file_encrypt_util.helpers.binary.DecHelper) {
							((file_encrypt_util.helpers.binary.DecHelper)d).decryptInput();
						} else if(d instanceof file_encrypt_util.helpers.plaintext.DecHelper) {
							((file_encrypt_util.helpers.plaintext.DecHelper)d).decryptInput();
						}
						System.out.println("Verifying...");
						//Call appropriate verification method:
						if(d instanceof file_encrypt_util.helpers.binary.DecHelper) {
							((file_encrypt_util.helpers.binary.DecHelper)d).verifyContents();
						} else if(d instanceof file_encrypt_util.helpers.plaintext.DecHelper) {
							((file_encrypt_util.helpers.plaintext.DecHelper)d).verifyContents();
						}
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
