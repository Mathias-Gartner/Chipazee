package fh.mdp.chipazee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class Turn {

	public int turnNumber = 1;

	private Deque<Colors> colors = new ArrayDeque<Colors>();

	public void addColor(Colors color) {
		colors.add(color);
	}

	public Deque<Colors> getColors() {
		return colors;
	}

	public static Turn deserialize(byte[] data) throws IllegalArgumentException {
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		InputStream stream = new ByteArrayInputStream(buffer.array(), 0,
				buffer.limit());
		try {
			ObjectInputStream ois = new ObjectInputStream(stream);
			return (Turn) ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (OptionalDataException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public byte[] serialize() throws IOException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
	}

}
