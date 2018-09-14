package file_encrypt_util;

//Application to encrypt and decrypt raw files.
//Encryption is done by:
//1. Encoding the file contents to Base64.
//2. Finding the SHA-256 hash of the Base64-encoded contents.
//3. Store the hash as <filename>.sha256
//4. Getting a password from the user, then encrypting the Base64-encoded contents with 256-bit AES/CBC/PKCS5Padding.
//5. The encrypted string is Base64 encoded and stored as <filename>.enc
//(The folder to be used is created with the name of the file, minus the extension.)
//Reverse the process for decryption.

//For file and terminal I/O:
import java.util.*;
import java.io.*;

//Main application Class:
public class EncUtil {
	public static void main(String[] args) {
		//Check arguments:
		//TODO: IMPLEMENT HELPER CODE FIRST
	}
}
