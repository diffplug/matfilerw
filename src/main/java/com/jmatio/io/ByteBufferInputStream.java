/**
 * 
 */
package com.jmatio.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

class ByteBufferInputStream extends InputStream
{
    private ByteBuffer buf;

    private int limit;

    public ByteBufferInputStream(final ByteBuffer buf, final int limit)
    {
        this.buf = buf;
        this.limit = limit;
    }

    @Override
    public synchronized int read() throws IOException
    {
        if (!(limit > 0))
        {
            return -1;
        }
        limit--;
        return buf.get() & 0xFF;
    }
    
    @Override
    public synchronized int read(byte[] bytes, int off, int len)
            throws IOException
    {
        if (!(limit > 0))
        {
            return -1;
        }
        len = Math.min(len, limit);
        // Read only what's left
        buf.get(bytes, off, len);
        limit -= len;
        return len;
    }
}