package com.github.gusarov.carpc;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class StreamReaderTest {

	StreamReader sr;
	Reader r;

	@Before
	public void Init()
	{
		r = new Reader();
		sr = StreamReader.create(r);
	}

	class Reader implements IBlockReader {
		RingBuffer _buf = new RingBuffer();

		public Reader() {

		}

		@Override
		public int read(byte[] buf, int maxLen) throws IOException {
			return read(buf, 0, maxLen);
		}

		@Override
		public int read(byte[] buf, int offset, int maxLen) throws IOException {
			byte[] r = _buf.read(maxLen);
			System.arraycopy(r, 0, buf, offset, r.length);
			return r.length;
		}
	}

	@Test
	public void Should_10_read_return_minus_one_on_empty() throws IOException {
		Assert.assertEquals(true, sr.getIsEmpty());
		Assert.assertEquals(-1, sr.tryReadAndMove());
		Assert.assertEquals(true, sr.getIsEmpty());
	}

	@Test
	public void Should_30_read_lines() throws IOException {
		byte[] data = {'a', 'b', 'c', '\r', '\n', 'd', 'e', 'f', '\r', '\n', 'g', 'h', 'i', '\r', '\n'};
		r._buf.write(data);
		String s1 = sr.readLine();
		String s2 = sr.readLine();
		String s3 = sr.readLine();
		Assert.assertEquals("abc", s1);
		Assert.assertEquals("def", s2);
		Assert.assertEquals("ghi", s3);
	}
}
