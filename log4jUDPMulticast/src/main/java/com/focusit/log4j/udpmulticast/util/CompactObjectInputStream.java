package com.focusit.log4j.udpmulticast.util;

import io.netty.handler.codec.serialization.*;

import java.io.*;

/**
 * Created by Denis V. Kirpichenkov on 28.01.15.
 */
public class CompactObjectInputStream extends ObjectInputStream {

	private final ClassResolver classResolver;

	public CompactObjectInputStream(InputStream in, ClassResolver classResolver) throws IOException {
		super(in);
		this.classResolver = classResolver;
	}

	@Override
	protected void readStreamHeader() throws IOException {
		int version = readByte() & 0xFF;
		if (version != STREAM_VERSION) {
			throw new StreamCorruptedException(
				"Unsupported version: " + version);
		}
	}

	@Override
	protected ObjectStreamClass readClassDescriptor()
		throws IOException, ClassNotFoundException {
		int type = read();
		if (type < 0) {
			throw new EOFException();
		}
		switch (type) {
			case CompactObjectOutputStream.TYPE_FAT_DESCRIPTOR:
				return super.readClassDescriptor();
			case CompactObjectOutputStream.TYPE_THIN_DESCRIPTOR:
				String className = readUTF();
				Class<?> clazz = classResolver.resolve(className);
				return ObjectStreamClass.lookupAny(clazz);
			default:
				throw new StreamCorruptedException(
					"Unexpected class descriptor type: " + type);
		}
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		Class<?> clazz;
		try {
			clazz = classResolver.resolve(desc.getName());
		} catch (ClassNotFoundException ex) {
			clazz = super.resolveClass(desc);
		}

		return clazz;
	}

}
