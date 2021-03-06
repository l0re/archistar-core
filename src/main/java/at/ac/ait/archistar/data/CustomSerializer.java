package at.ac.ait.archistar.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import at.ac.ait.archistar.data.user.FSObject;

/**
 * convert incoming user file objects into byte arrays (and vice versa)
 * 
 * @author andy
 */
public class CustomSerializer implements Serializer {
	
	public byte[] serialize(FSObject input) {
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
			ObjectOutputStream out = new ObjectOutputStream(bos) ;
			out.writeObject(input);
			out.close();

			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public FSObject deserialize(byte[] data) {
		try{
			ByteArrayInputStream door = new ByteArrayInputStream(data);
			ObjectInputStream reader = new ObjectInputStream(door);
			return (FSObject)reader.readObject();
		}catch (IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
