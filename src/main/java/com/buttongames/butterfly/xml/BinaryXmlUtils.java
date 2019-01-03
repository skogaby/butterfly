package com.buttongames.butterfly.xml;

import com.buttongames.butterfly.util.CollectionUtils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public static byte[] binaryToXml(final byte[] input) throws IOException {
        final String tmpPath = System.getProperty("user.home") + "\\tmpkbin";
        final DataOutputStream dos = new DataOutputStream(new FileOutputStream(tmpPath));
        dos.write(input, 0, input.length);
        dos.flush();
        dos.close();

        // shell out to mon's implementation for now
        final Process child = Runtime.getRuntime().exec("kbinxml " + tmpPath);
        final byte[] output = CollectionUtils.readInputStream(child.getInputStream());
        return output;
    }

    /**
     * Converts the input to binary XML from plaintext XML.
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] xmlToBinary(final byte[] input) throws IOException {
        return binaryToXml(input);
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
