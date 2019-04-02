package gne;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BinaryFileReader {
	byte[] data;
	int index;
	public BinaryFileReader(String path) {
		try {
			File file = new File(path);
			data = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			System.err.println("file: "+ path +" not found!");
			e.printStackTrace();
		}
		index=0;
	}
	public int readUint8() {
		return data[index++]+128;
	}
	public boolean readBool() {
		return data[index++]==1;
	}
	public int readInt16() {
		return ((readUint8() << 8) + (readUint8() << 0));
	}
	public int readInt32() {
		return ((readUint8() << 24) + (readUint8() << 16) + (readUint8() << 8) + (readUint8() << 0));
	}
	public String readString() {
		int length = readInt16();
		char[] string = new char[length];
		for (int i = 0;i<length;i++)
			string[i] = (char)data[index++];
		return new String(string);
	}
}
