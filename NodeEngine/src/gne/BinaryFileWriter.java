package gne;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryFileWriter {
	String path;
	byte[] data;
	int index;
	public BinaryFileWriter(String path) {
		this.path = path;
		index = 0;
		data = new byte[100000];
	}
	public void writeUint8(int value) {
		data[index++]=(byte)((value%256)-128);
	}
	public void writeBool(boolean value) {
		data[index++]=(byte)(value?1:0);
	}
	public void writeInt16(int value) {
		writeUint8(value>>8);
		writeUint8(value>>0);
	}
	public void writeInt32(int value) {
		writeUint8(value>>24);
		writeUint8(value>>16);
		writeUint8(value>>8);
		writeUint8(value>>0);
	}
	public void writeString(String value) {
		writeInt16(value.length());
		for (int i = 0;i<value.length();i++)
			data[index++]=(byte)value.charAt(i);
	}
	public void flush() {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			outStream.write(data,0,index);
			outStream.flush();
			outStream.writeTo(fos);
			outStream.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
