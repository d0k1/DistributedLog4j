package com.focusit.log4j.udpmulticast.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * Created by Denis V. Kirpichenkov on 27.01.15.
 */
public class CompactObjectOutputStream extends ObjectOutputStream {

	static final int TYPE_FAT_DESCRIPTOR = 0;
	static final int TYPE_THIN_DESCRIPTOR = 1;

	public CompactObjectOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	protected void writeStreamHeader() throws IOException {
		writeByte(STREAM_VERSION);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
		Class<?> clazz = desc.forClass();
		if (clazz.isPrimitive() || clazz.isArray() || clazz.isInterface() ||
			desc.getSerialVersionUID() == 0) {
			write(TYPE_FAT_DESCRIPTOR);
			super.writeClassDescriptor(desc);
		} else {
			write(TYPE_THIN_DESCRIPTOR);
			writeUTF(desc.getName());
		}
	}
}
