package com.github.gusarov.carpc;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class RingBufferTest {

	RingBuffer buf;

	@Before
	public void Init() {
		buf = new RingBuffer(10);
	}

	@Test
	public void Should_10_simple_read_write() throws IOException {
		byte[] arr1 = {
				1,2,3
		};
		byte[] arr2 = {
				4,5
		};
		buf.write(arr1, 0, 3);
		Assert.assertEquals(0, buf.bufStart);
		Assert.assertEquals(3, buf.bufFillLen);
		buf.write(arr2, 0, 2);
		Assert.assertEquals(0, buf.bufStart);
		Assert.assertEquals(5, buf.bufFillLen);

		byte[] read = new byte[1024];
		int r = buf.read(read, 0, 2);
		Assert.assertEquals(2, r);
		Assert.assertEquals(1, read[0]);
		Assert.assertEquals(2, read[1]);
		Assert.assertEquals(0, read[2]);
		Assert.assertEquals(2, buf.bufStart);
		Assert.assertEquals(3, buf.bufFillLen);
		r = buf.read(read, 0, 1024);
		Assert.assertEquals(3, r);
		Assert.assertEquals(3, read[0]);
		Assert.assertEquals(4, read[1]);
		Assert.assertEquals(5, read[2]);
		Assert.assertEquals(0, read[3]);
		Assert.assertEquals(5, buf.bufStart);
		Assert.assertEquals(0, buf.bufFillLen);
	}

	@Test
	public void Should_20_run_over_border() throws IOException {
		byte[] arr = {
				1,2,3,4,5,6,
		};
		buf.write(arr);
		Assert.assertEquals(6, buf.bufFillLen);
		Assert.assertEquals(0, buf.bufStart);
		byte[] r = buf.read(4);
		Assert.assertEquals(4, r.length);
		Assert.assertEquals(1, r[0]);
		Assert.assertEquals(4, r[3]);
		Assert.assertEquals(2, buf.bufFillLen);
		Assert.assertEquals(4, buf.bufStart);
		buf.write(arr);
		Assert.assertEquals(8, buf.bufFillLen);
		Assert.assertEquals(4, buf.bufStart);
		r = buf.read(8);
		Assert.assertEquals(8, r.length);
		Assert.assertEquals(0, buf.bufFillLen);
		Assert.assertEquals(2, buf.bufStart);
		Assert.assertEquals(5, r[0]);
		Assert.assertEquals(6, r[1]);
		Assert.assertEquals(1, r[2]);
		Assert.assertEquals(2, r[3]);
		Assert.assertEquals(3, r[4]);
		Assert.assertEquals(4, r[5]);
		Assert.assertEquals(5, r[6]);
		Assert.assertEquals(6, r[7]);
	}
}

