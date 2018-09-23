package file_encrypt_util.helpers;

//Abstract class for Encryption/Decryption helpers:

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;

public abstract class Helper {
	//Function to read data:
	public abstract void readInput() throws IOException;
	//Function to write output:
	public abstract boolean writeOutput() throws IOException;
	//Function to close all streams:
	public abstract void finish() throws IOException;
}
