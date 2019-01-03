package com.buttongames.butterfly.xml;

import com.buttongames.butterfly.util.CollectionUtils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class to help translating between binary and plaintext XML. Right now it's a very dumb
 * class and just uses mon's Python implementation until I can make a native Java one. This does
 * depend on you having done a <code>pip install kbinxml</code> to make <code>kbinxml</code>
 * a valid command.
 * See: https://github.com/mon/kbinxml
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class BinaryXmlUtils {

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

    public static byte[] xmlToBinary(final byte[] input) throws IOException {
        return binaryToXml(input);
    }
}
