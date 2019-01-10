package com.buttongames.butterfly.xml;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class to help translating between binary and plaintext XML. Right now it's a very dumb
 * class and just uses mon's Python implementation until I can make a native Java one. This does
 * depend on you having done a <code>pip install kbinxml</code> to make <code>kbinxml</code>
 * a valid command.
 * See: https://github.com/mon/kbinxml
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class BinaryXmlUtils {

    /**
     * First bytes of a plaintext XML response, so we know if an array needs to be converted
     * to/from binary XML.
     */
    private static final byte[] XML_PREFIX = "<?xml".getBytes(StandardCharsets.UTF_8);

    /**
     * Converts the input to plaintext XML from binary.
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] binaryToXml(final byte[] input) {
        try {
            final ProcessBuilder builder = new ProcessBuilder("python",
                    "-c",
                    "import sys; " +
                        "from kbinxml import KBinXML; " +
                        "the_bytes = sys.stdin.buffer.read(); "+
                        "print(KBinXML(the_bytes).to_text())");
            final Process process = builder.start();
            final OutputStream stdin = process.getOutputStream();
            final InputStream stdout = process.getInputStream();

            stdin.write(input, 0, input.length);
            stdin.flush();
            stdin.close();

            return ByteStreams.toByteArray(stdout);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts the input to binary XML from plaintext XML.
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] xmlToBinary(final byte[] input) {
        try {
            final ProcessBuilder builder = new ProcessBuilder("python",
                    "-c",
                    "import sys; " +
                        "from kbinxml import KBinXML; " +
                        "the_bytes = sys.stdin.buffer.read(); " +
                        "sys.stdout.buffer.write(KBinXML(the_bytes).to_binary())");
            final Process process = builder.start();
            final OutputStream stdin = process.getOutputStream();
            final InputStream stdout = process.getInputStream();

            stdin.write(input, 0, input.length);
            stdin.flush();
            stdin.close();

            return ByteStreams.toByteArray(stdout);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Says whether or not the input is binary XML.
     * @param input
     * @return
     */
    public static boolean isBinaryXML(final byte[] input) {
        boolean isBinary = false;

        for (int i = 0; i < XML_PREFIX.length; i++) {
            if (input[i] != XML_PREFIX[i]) {
                isBinary = true;
                break;
            }
        }

        return isBinary;
    }
}
