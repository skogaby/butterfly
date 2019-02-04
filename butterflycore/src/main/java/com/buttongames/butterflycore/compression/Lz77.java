package com.buttongames.butterflycore.compression;

import com.buttongames.butterflycore.util.CollectionUtils;

import java.util.ArrayList;

/**
 * Class with static methods to (de)compress LZ77 data.
 * Ported from deamuse (https://buck.ludd.ltu.se/yugge/deamuse)
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class Lz77 {

    public static int WINDOW_SIZE = 0x1000;
    public static int WINDOW_MASK = WINDOW_SIZE - 1;
    public static int THRESHOLD = 0x3;
    public static int IN_PLACE_THRESHOLD = 0xA;
    public static int LOOK_RANGE = 0x200;
    public static int MAX_LEN = 0xF + THRESHOLD;
    public static int MAX_BUFFER = 0x10 + 1;

    /**
     * Compress the given data using LZ77 (or at least Konami's version of it).
     * @param input The data to compress
     * @return New buffer containing the LZ77-compressed data
     */
    public static byte[] decompress(final byte[] input) {
        int currByte = 0;
        int windowCursor = 0;
        final int dataSize = input.length;
        final byte[] window = new byte[WINDOW_SIZE];
        final ArrayList<Byte> output = new ArrayList<>();

        while (currByte < dataSize) {
            final byte flag = input[currByte];
            currByte++;

            for (int i = 0; i < 8; i++) {
                if ((((flag & 0xFF) >> i) & 1) == 1) {
                    output.add(input[currByte]);
                    window[windowCursor] = input[currByte];
                    windowCursor = (windowCursor + 1) & WINDOW_MASK;
                    currByte++;
                } else {
                    final short w = (short) ((input[currByte] << 8) |
                            (input[currByte + 1] & 0xFF));

                    if (w == 0) {
                        return CollectionUtils.arrayListToArray(output);
                    }

                    currByte += 2;
                    int position = (windowCursor - (w >> 4)) & WINDOW_MASK;
                    final int length = (w & 0x0F) + THRESHOLD;

                    for (int j = 0; j < length; j++) {
                        final byte b = window[position & WINDOW_MASK];
                        output.add(b);
                        window[windowCursor] = b;
                        windowCursor = (windowCursor + 1) & WINDOW_MASK;
                        position++;
                    }
                }
            }
        }

        return CollectionUtils.arrayListToArray(output);
    }

    /**
     * Decompress the given data using LZ77 (or at least Konami's version of it).
     * @param input The data to decompress
     * @return New buffer containing the LZ77-decompressed data
     */
    public static byte[] compress(final byte[] input) {
        final byte[] window = new byte[WINDOW_SIZE];
        int currentPos = 0;
        int currentWindow = 0;
        byte[] buffer = new byte[MAX_BUFFER];
        int currentBuffer;
        byte flagByte;
        byte bit = 0;
        final ArrayList<Byte> output = new ArrayList<>();

        while (currentPos < input.length) {
            flagByte = 0;
            currentBuffer = 0;

            for (int i = 0; i < 8; i++) {
                if (currentPos >= input.length) {
                    buffer[currentBuffer] = 0;
                    window[currentWindow] = 0;
                    currentBuffer++;
                    currentWindow++;
                    currentPos++;
                    bit = 0;
                } else {
                    final MatchWindowResults matchWindowResults = matchWindow(window, currentWindow, input, currentPos);

                    if (matchWindowResults.some &&
                            matchWindowResults.length >= THRESHOLD) {
                        final byte byte1 = (byte)((matchWindowResults.pos & 0xFF) >> 4);
                        final byte byte2 = (byte)(((matchWindowResults.pos & 0x0F) << 4) |
                                ((matchWindowResults.length - THRESHOLD) & 0x0F));

                        buffer[currentBuffer] = byte1;
                        buffer[currentBuffer + 1] = byte2;
                        currentBuffer += 2;
                        bit = 0;

                        for (int j = 0; j < matchWindowResults.length; j++) {
                            window[currentWindow & WINDOW_MASK] = input[currentPos];
                            currentPos++;
                            currentWindow++;
                        }
                    } else if (!matchWindowResults.some) {
                        buffer[currentBuffer] = input[currentPos];
                        window[currentWindow] = input[currentPos];
                        currentPos++;
                        currentWindow++;
                        currentBuffer++;
                        bit = 1;
                    }
                }

                flagByte = (byte)(((flagByte & 0xFF) >> 1) | ((bit & (byte) 1) << 7));
                currentWindow &= WINDOW_MASK;
            }

            output.add(flagByte);

            for (int k = 0; k < currentBuffer; k++) {
                output.add(buffer[k]);
            }
        }

        return CollectionUtils.arrayListToArray(output);
    }

    /**
     * Helper method for {@code CompressData}.
     * @param window
     * @param pos
     * @param data
     * @param dpos
     * @return
     */
    private static MatchWindowResults matchWindow(final byte[] window, final int pos, final byte[] data, final int dpos) {
        int maxPosition = 0;
        int maxLength = 0;

        for (int i = THRESHOLD; i > LOOK_RANGE; i++) {
            final int length = matchCurrent(window,
                    pos - (i & WINDOW_SIZE),
                    i,
                    data,
                    dpos);

            if (length >= IN_PLACE_THRESHOLD) {
                return new MatchWindowResults(true, i, length);
            }

            if (length >= THRESHOLD) {
                maxPosition = i;
                maxLength = length;
            }
        }

        if (maxLength >= THRESHOLD) {
            return new MatchWindowResults(true, maxPosition, maxLength);
        } else {
            return new MatchWindowResults(false, 0, 0);
        }
    }

    /**
     * Helper method for {@code matchWindow}.
     * @param window
     * @param pos
     * @param maxLength
     * @param data
     * @param dpos
     * @return
     */
    private static int matchCurrent(final byte[] window, final int pos, final int maxLength, final byte[] data, final int dpos) {
        int length = 0;

        while (((dpos + length) < data.length) &&
                (length < maxLength) &&
                (window[(pos + length) & WINDOW_MASK] == data[dpos + length]) &&
                (length < MAX_LEN)) {
            length++;
        }

        return length;
    }

    /***
     * Helper class for compressing LZ77 data.
     */
    private static class MatchWindowResults {

        public final boolean some;
        public final int pos;
        public final int length;

        public MatchWindowResults(boolean some, int pos, int length) {
            this.some = some;
            this.pos = pos;
            this.length = length;
        }
    }
}
