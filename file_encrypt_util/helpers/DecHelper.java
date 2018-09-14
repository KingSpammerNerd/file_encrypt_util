package file_encrypt_helper.helpers;

//Helper class to implement file decryption.
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

//Requires: An input stream, output stream, Cipher, MessageDigest, Base64 decoder, File (for directory name)

//Decryption helper class:
class DecHelper {
	//File input streams:
	BufferedReader file_in=null;
	BufferedReader hash_in=null;
	//File output stream:
	BufferedWriter file_out=null;
	//Cipher for decryption:
	Cipher decryptor=null;
	//Base64 decoder:
	Base64.Decoder=null;
	
}
