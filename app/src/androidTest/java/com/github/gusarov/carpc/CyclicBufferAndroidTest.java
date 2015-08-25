package com.github.gusarov.carpc;

import android.test.AndroidTestCase;

import junit.framework.Assert;

public class CyclicBufferAndroidTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void Should_01_simple() {
        Assert.assertEquals(true, false);

        Sample s = new Sample();
    }
}


