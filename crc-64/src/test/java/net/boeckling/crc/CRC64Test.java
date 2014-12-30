package net.boeckling.crc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author michaelboeckling
 */
public class CRC64Test
{

    /**
     *
     */
    @Test
    public void testBytes()
    {

        final byte[] TEST1 = "123456789".getBytes();
        final int TESTLEN1 = 9;
        final long TESTCRC1 = 0x995dc9bbdf1939faL; // ECMA.
        calcAndCheck(TEST1, TESTLEN1, TESTCRC1);

        final byte[] TEST2 = "This is a test of the emergency broadcast system.".getBytes();
        final int TESTLEN2 = 49;
        final long TESTCRC2 = 0x27db187fc15bbc72L; // ECMA.
        calcAndCheck(TEST2, TESTLEN2, TESTCRC2);

        final byte[] TEST3 = "IHATEMATH".getBytes();
        final int TESTLEN3 = 9;
        final long TESTCRC3 = 0x3920e0f66b6ee0c8L; // ECMA.
        calcAndCheck(TEST3, TESTLEN3, TESTCRC3);
    }

    /**
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testFile() throws IOException, URISyntaxException
    {
        File f = new File(getClass().getResource("crctest.txt").toURI());
        CRC64 crc = CRC64.fromFile(f);

        Assert.assertEquals("oh noes", 0x27db187fc15bbc72L, crc.getValue());
    }

    /**
     * @throws IOException
     */
    @Test
    public void testInputStream() throws IOException
    {
        InputStream in = getClass().getResourceAsStream("crctest.txt");
        CRC64 crc = CRC64.fromInputStream(in);

        Assert.assertEquals("oh noes", 0x27db187fc15bbc72L, crc.getValue());
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void testPerformance() throws IOException
    {
        byte[] b = new byte[65536];
        new Random().nextBytes(b);

        // warmup
        CRC64 crc = new CRC64();
        crc.update(b, b.length);

        // start bench
        long bytes = 0;
        long start = System.currentTimeMillis();
        crc = new CRC64();
        for (int i = 0; i < 100000; i++)
        {
            crc.update(b, b.length);
            bytes += b.length;
        }

        long duration = System.currentTimeMillis() - start;
        duration = (duration == 0) ? 1 : duration; // div0
        long bytesPerSec = (bytes / duration) * 1000;

        System.out.println(bytes / 1024 / 1024 + " MB processed in " + duration + " ms @ " + bytesPerSec / 1024 / 1024
                + " MB/s");
    }

    /**
     *
     * @param b
     * @param len
     * @param crcValue
     */
    private void calcAndCheck(byte[] b, int len, long crcValue)
    {

        /* Test CRC64 default calculation. */
        CRC64 crc = new CRC64(b, len);
        if (crc.getValue() != crcValue)
        {
            throw new RuntimeException("mismatch: " + String.format("%016x", crc.getValue()) + " should be "
                    + String.format("%016x", crcValue));
        }

        /* test combine() */
        CRC64 crc1 = new CRC64(b, (len + 1) >>> 1);
        CRC64 crc2 = new CRC64(Arrays.copyOfRange(b, (len + 1) >>> 1, b.length), len >>> 1);
        crc = CRC64.combine(crc1, crc2, len >>> 1);

        if (crc.getValue() != crcValue)
        {
            throw new RuntimeException("mismatch: " + String.format("%016x", crc.getValue()) + " should be "
                    + String.format("%016x", crcValue));
        }
    }

}
