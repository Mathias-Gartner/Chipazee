package fh.mdp.chipazee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class Turn implements Serializable {

	public int turnNumber = 1;

	public void addColor(Colors color) {
		colors.add(color);
	}

	public Deque<Colors> getColors() {
		return colors;
	}

	public static Turn deserialize(byte[] data) throws IllegalArgumentException {
		InputStream stream = new ByteArrayInputStream(data, 0, data.length);
		try {
			ObjectInputStream ois = new ObjectInputStream(stream);
			return (Turn) ois.readObject();
		} catch (StreamCorruptedException e) {
			throw new IllegalArgumentException(e);
		} catch (OptionalDataException e) {
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
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
			throw e;
		}
	}

	private Deque<Colors> colors = new ArrayDeque<Colors>();
	/**
	 * Automatically added by Eclipse.
	 */
	private static final long serialVersionUID = 1L;

}
