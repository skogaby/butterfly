package com.buttongames.butterfly.util;

import com.buttongames.butterfly.http.exception.CardCipherException;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;

/**
 * Utility class for converting between NFC IDs (the internal ID) and card numbers
 * (the ID on the back of the card) for e-amusement passes.
 * @author anonymous
 */
public class CardIdUtils {

    private static final int[] KEY = {
            0x20d0d03c, 0x868ecb41, 0xbcd89c84, 0x4c0e0d0d,
            0x84fc30ac, 0x4cc1890e, 0xfc5418a4, 0x02c50f44,
            0x68acb4e0, 0x06cd4a4e, 0xcc28906c, 0x4f0c8ac0,
            0xb03ca468, 0x884ac7c4, 0x389490d8, 0xcf80c6c2,
            0x58d87404, 0xc48ec444, 0xb4e83c50, 0x498d0147,
            0x64f454c0, 0x4c4701c8, 0xec302cc4, 0xc6c949c1,
            0xc84c00f0, 0xcdcc49cc, 0x883c5cf4, 0x8b0fcb80,
            0x703cc0b0, 0xcb820a8d, 0x78804c8c, 0x4fca830e,
            0x80d0f03c, 0x8ec84f8c, 0x98c89c4c, 0xc80d878f,
            0x54bc949c, 0xc801c5ce, 0x749078dc, 0xc3c80d46,
            0x2c8070f0, 0x0cce4dcf, 0x8c3874e4, 0x8d448ac3,
            0x987cac70, 0xc0c20ac5, 0x288cfc78, 0xc28543c8,
            0x4c8c7434, 0xc50e4f8d, 0x8468f4b4, 0xcb4a0307,
            0x2854dc98, 0x48430b45, 0x6858fce8, 0x4681cd49,
            0xd04808ec, 0x458d0fcb, 0xe0a48ce4, 0x880f8fce,
            0x7434b8fc, 0xce080a8e, 0x5860fc6c, 0x46c886cc,
            0xd01098a4, 0xce090b8c, 0x1044cc2c, 0x86898e0f,
            0xd0809c3c, 0x4a05860f, 0x54b4f80c, 0x4008870e,
            0x1480b88c, 0x0ac8854f, 0x1c9034cc, 0x08444c4e,
            0x0cb83c64, 0x41c08cc6, 0x1c083460, 0xc0c603ce,
            0x2ca0645c, 0x818246cb, 0x0408e454, 0xc5464487,
            0x88607c18, 0xc1424187, 0x284c7c90, 0xc1030509,
            0x40486c94, 0x4603494b, 0xe0404ce4, 0x4109094d,
            0x60443ce4, 0x4c0b8b8d, 0xe054e8bc, 0x02008e89,
    };

    private static final int[] LUT_A0 = {
            0x02080008, 0x02082000, 0x00002008, 0x00000000,
            0x02002000, 0x00080008, 0x02080000, 0x02082008,
            0x00000008, 0x02000000, 0x00082000, 0x00002008,
            0x00082008, 0x02002008, 0x02000008, 0x02080000,
            0x00002000, 0x00082008, 0x00080008, 0x02002000,
            0x02082008, 0x02000008, 0x00000000, 0x00082000,
            0x02000000, 0x00080000, 0x02002008, 0x02080008,
            0x00080000, 0x00002000, 0x02082000, 0x00000008,
            0x00080000, 0x00002000, 0x02000008, 0x02082008,
            0x00002008, 0x02000000, 0x00000000, 0x00082000,
            0x02080008, 0x02002008, 0x02002000, 0x00080008,
            0x02082000, 0x00000008, 0x00080008, 0x02002000,
            0x02082008, 0x00080000, 0x02080000, 0x02000008,
            0x00082000, 0x00002008, 0x02002008, 0x02080000,
            0x00000008, 0x02082000, 0x00082008, 0x00000000,
            0x02000000, 0x02080008, 0x00002000, 0x00082008,
    };

    private static final int[] LUT_A1 = {
            0x08000004, 0x00020004, 0x00000000, 0x08020200,
            0x00020004, 0x00000200, 0x08000204, 0x00020000,
            0x00000204, 0x08020204, 0x00020200, 0x08000000,
            0x08000200, 0x08000004, 0x08020000, 0x00020204,
            0x00020000, 0x08000204, 0x08020004, 0x00000000,
            0x00000200, 0x00000004, 0x08020200, 0x08020004,
            0x08020204, 0x08020000, 0x08000000, 0x00000204,
            0x00000004, 0x00020200, 0x00020204, 0x08000200,
            0x00000204, 0x08000000, 0x08000200, 0x00020204,
            0x08020200, 0x00020004, 0x00000000, 0x08000200,
            0x08000000, 0x00000200, 0x08020004, 0x00020000,
            0x00020004, 0x08020204, 0x00020200, 0x00000004,
            0x08020204, 0x00020200, 0x00020000, 0x08000204,
            0x08000004, 0x08020000, 0x00020204, 0x00000000,
            0x00000200, 0x08000004, 0x08000204, 0x08020200,
            0x08020000, 0x00000204, 0x00000004, 0x08020004,
    };

    private static final int[] LUT_A2 = {
            0x80040100, 0x01000100, 0x80000000, 0x81040100,
            0x00000000, 0x01040000, 0x81000100, 0x80040000,
            0x01040100, 0x81000000, 0x01000000, 0x80000100,
            0x81000000, 0x80040100, 0x00040000, 0x01000000,
            0x81040000, 0x00040100, 0x00000100, 0x80000000,
            0x00040100, 0x81000100, 0x01040000, 0x00000100,
            0x80000100, 0x00000000, 0x80040000, 0x01040100,
            0x01000100, 0x81040000, 0x81040100, 0x00040000,
            0x81040000, 0x80000100, 0x00040000, 0x81000000,
            0x00040100, 0x01000100, 0x80000000, 0x01040000,
            0x81000100, 0x00000000, 0x00000100, 0x80040000,
            0x00000000, 0x81040000, 0x01040100, 0x00000100,
            0x01000000, 0x81040100, 0x80040100, 0x00040000,
            0x81040100, 0x80000000, 0x01000100, 0x80040100,
            0x80040000, 0x00040100, 0x01040000, 0x81000100,
            0x80000100, 0x01000000, 0x81000000, 0x01040100,
    };

    private static final int[] LUT_A3 = {
            0x04010801, 0x00000000, 0x00010800, 0x04010000,
            0x04000001, 0x00000801, 0x04000800, 0x00010800,
            0x00000800, 0x04010001, 0x00000001, 0x04000800,
            0x00010001, 0x04010800, 0x04010000, 0x00000001,
            0x00010000, 0x04000801, 0x04010001, 0x00000800,
            0x00010801, 0x04000000, 0x00000000, 0x00010001,
            0x04000801, 0x00010801, 0x04010800, 0x04000001,
            0x04000000, 0x00010000, 0x00000801, 0x04010801,
            0x00010001, 0x04010800, 0x04000800, 0x00010801,
            0x04010801, 0x00010001, 0x04000001, 0x00000000,
            0x04000000, 0x00000801, 0x00010000, 0x04010001,
            0x00000800, 0x04000000, 0x00010801, 0x04000801,
            0x04010800, 0x00000800, 0x00000000, 0x04000001,
            0x00000001, 0x04010801, 0x00010800, 0x04010000,
            0x04010001, 0x00010000, 0x00000801, 0x04000800,
            0x04000801, 0x00000001, 0x04010000, 0x00010800,
    };

    private static final int[] LUT_B0 = {
            0x00000400, 0x00000020, 0x00100020, 0x40100000,
            0x40100420, 0x40000400, 0x00000420, 0x00000000,
            0x00100000, 0x40100020, 0x40000020, 0x00100400,
            0x40000000, 0x00100420, 0x00100400, 0x40000020,
            0x40100020, 0x00000400, 0x40000400, 0x40100420,
            0x00000000, 0x00100020, 0x40100000, 0x00000420,
            0x40100400, 0x40000420, 0x00100420, 0x40000000,
            0x40000420, 0x40100400, 0x00000020, 0x00100000,
            0x40000420, 0x00100400, 0x40100400, 0x40000020,
            0x00000400, 0x00000020, 0x00100000, 0x40100400,
            0x40100020, 0x40000420, 0x00000420, 0x00000000,
            0x00000020, 0x40100000, 0x40000000, 0x00100020,
            0x00000000, 0x40100020, 0x00100020, 0x00000420,
            0x40000020, 0x00000400, 0x40100420, 0x00100000,
            0x00100420, 0x40000000, 0x40000400, 0x40100420,
            0x40100000, 0x00100420, 0x00100400, 0x40000400,
    };

    private static final int[] LUT_B1 = {
            0x00800000, 0x00001000, 0x00000040, 0x00801042,
            0x00801002, 0x00800040, 0x00001042, 0x00801000,
            0x00001000, 0x00000002, 0x00800002, 0x00001040,
            0x00800042, 0x00801002, 0x00801040, 0x00000000,
            0x00001040, 0x00800000, 0x00001002, 0x00000042,
            0x00800040, 0x00001042, 0x00000000, 0x00800002,
            0x00000002, 0x00800042, 0x00801042, 0x00001002,
            0x00801000, 0x00000040, 0x00000042, 0x00801040,
            0x00801040, 0x00800042, 0x00001002, 0x00801000,
            0x00001000, 0x00000002, 0x00800002, 0x00800040,
            0x00800000, 0x00001040, 0x00801042, 0x00000000,
            0x00001042, 0x00800000, 0x00000040, 0x00001002,
            0x00800042, 0x00000040, 0x00000000, 0x00801042,
            0x00801002, 0x00801040, 0x00000042, 0x00001000,
            0x00001040, 0x00801002, 0x00800040, 0x00000042,
            0x00000002, 0x00001042, 0x00801000, 0x00800002,
    };

    private static final int[] LUT_B2 = {
            0x10400000, 0x00404010, 0x00000010, 0x10400010,
            0x10004000, 0x00400000, 0x10400010, 0x00004010,
            0x00400010, 0x00004000, 0x00404000, 0x10000000,
            0x10404010, 0x10000010, 0x10000000, 0x10404000,
            0x00000000, 0x10004000, 0x00404010, 0x00000010,
            0x10000010, 0x10404010, 0x00004000, 0x10400000,
            0x10404000, 0x00400010, 0x10004010, 0x00404000,
            0x00004010, 0x00000000, 0x00400000, 0x10004010,
            0x00404010, 0x00000010, 0x10000000, 0x00004000,
            0x10000010, 0x10004000, 0x00404000, 0x10400010,
            0x00000000, 0x00404010, 0x00004010, 0x10404000,
            0x10004000, 0x00400000, 0x10404010, 0x10000000,
            0x10004010, 0x10400000, 0x00400000, 0x10404010,
            0x00004000, 0x00400010, 0x10400010, 0x00004010,
            0x00400010, 0x00000000, 0x10404000, 0x10000010,
            0x10400000, 0x10004010, 0x00000010, 0x00404000,
    };

    private static final int[] LUT_B3 = {
            0x00208080, 0x00008000, 0x20200000, 0x20208080,
            0x00200000, 0x20008080, 0x20008000, 0x20200000,
            0x20008080, 0x00208080, 0x00208000, 0x20000080,
            0x20200080, 0x00200000, 0x00000000, 0x20008000,
            0x00008000, 0x20000000, 0x00200080, 0x00008080,
            0x20208080, 0x00208000, 0x20000080, 0x00200080,
            0x20000000, 0x00000080, 0x00008080, 0x20208000,
            0x00000080, 0x20200080, 0x20208000, 0x00000000,
            0x00000000, 0x20208080, 0x00200080, 0x20008000,
            0x00208080, 0x00008000, 0x20000080, 0x00200080,
            0x20208000, 0x00000080, 0x00008080, 0x20200000,
            0x20008080, 0x20000000, 0x20200000, 0x00208000,
            0x20208080, 0x00008080, 0x00208000, 0x20200080,
            0x00200000, 0x20000080, 0x20008000, 0x00000000,
            0x00008000, 0x00200000, 0x20200080, 0x00208080,
            0x20000000, 0x20208000, 0x00000080, 0x20008080,
    };

    private static final String VALID_CHARS = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";

    private static final ImmutableMap<String, String> CONV_CHARS = ImmutableMap.of("I", "1", "O", "0");

    public static String encodeCardId(final String id) {
        if (id.length() != 16) {
            throw new CardCipherException("Expected a 16-character card ID");
        }

        final byte[] cardint = CollectionUtils.hexStringToByteArray(id);

        // reverse bytes
        final byte[] reverse = new byte[8];

        for (int i = 0; i < 8; i++) {
            reverse[7 - i] = cardint[i];
        }

        // encipher
        final byte[] ciphered = encodeHelper(reverse);

        // convert 8 x 8 bit bytes into 13 x 5 bit groups (sort of)
        final byte[] bits = new byte[65];

        for (int i = 0; i < 64; i++) {
            bits[i] = (byte)((ciphered[i >> 3] >> (~i & 7)) & i);
        }

        final byte[] groups = new byte[16];

        for (int i = 0; i < 13; i++) {
            groups[i] = (byte)((bits[i * 5 + 0] << 4) |
                    (bits[i * 5 + 1] << 3) |
                    (bits[i * 5 + 2] << 2) |
                    (bits[i * 5 + 3] << 1) |
                    (bits[i * 5 + 4] << 0));
        }

        // smear 13 groups out into 14 groups
        groups[13] = 1;
        groups[0] ^= typeFromCardId(id);
        groups[0] ^= groups[15];

        for (int i = 1; i < 14; i++) {
            groups[i] ^= groups[i - 1];
        }

        // scheme field is 1 for old-style, 2 for felica cards
        groups[14] = (byte) typeFromCardId(id);
        groups[15] = checksum(groups);

        // convert to chars and return
        String encodedId = "";

        for (int i = 0; i < groups.length; i++) {
            encodedId += VALID_CHARS.charAt(groups[i]);
        }

        return encodedId;
    }

    private static final byte[] encodeHelper(final byte[] inBytes) {
        if (inBytes.length != 8) {
            throw new CardCipherException("Trouble encoding the card ID");
        }

        final byte[] inp = Arrays.copyOf(inBytes, inBytes.length);
        final byte[] out = { 0, 0, 0, 0, 0, 0, 0, 0 };

        fromInt(out, operatorA(0x00, toInt(inp)));
        fromInt(out, operatorB(0x20, toInt(out)));
        fromInt(out, operatorA(0x40, toInt(out)));

        return out;
    }

    private static final byte[] decodeHelper(final byte[] inBytes) {
        if (inBytes.length != 8) {
            throw new CardCipherException("Trouble decoding the card ID");
        }

        final byte[] inp = Arrays.copyOf(inBytes, inBytes.length);
        final byte[] out = { 0, 0, 0, 0, 0, 0, 0, 0 };

        fromInt(out, operatorB(0x40, toInt(inp)));
        fromInt(out, operatorA(0x20, toInt(out)));
        fromInt(out, operatorB(0x00, toInt(out)));

        return out;
    }

    private static int typeFromCardId(final String cardId) {
        final String upperCardId = cardId.toUpperCase();

        if (upperCardId.startsWith("E0")) {
            return 1;
        } else if (upperCardId.startsWith("01")) {
            return 2;
        } else {
            throw new CardCipherException("Unrecognized card type");
        }
    }

    private static byte checksum(final byte[] data) {
        byte checksum = 0;

        for (int i = 0; i < 16; i++) {
            checksum += (i % 3 + 1) * data[i];
        }

        while (checksum >= 0x20) {
            checksum = (byte)((checksum & 0x1F) + (checksum >> 5));
        }

        return checksum;
    }

    private static int toInt(final byte[] data) {
        final int inX = (data[0] & 0xFF) |
                ((data[1] & 0xFF) << 8) |
                ((data[2] & 0xFF) << 16) |
                ((data[3] & 0xFF) << 24);

        final int inY = (data[4] & 0xFF) |
                ((data[5] & 0xFF) << 8) |
                ((data[6] & 0xFF) << 16) |
                ((data[7] & 0xFF) << 24);

        final int v7 = ((((inX ^ (inY >> 4)) & 0xF0F0F0F) << 4) ^ inY) & 0xFFFFFFFF;
        final int v8 = (((inX ^ (inY >> 4)) & 0xF0F0F0F) ^ inX) & 0xFFFFFFFF;

        final int v9 =  ((v7 ^ (v8 >> 16))) & 0x0000FFFF;
        final int v10 = (((v7 ^ (v8 >> 16)) << 16) ^ v8) & 0xFFFFFFFF;

        final int v11 = (v9 ^ v7) & 0xFFFFFFFF;
        final int v12 = (v10 ^ (v11 >> 2)) & 0x33333333;
        final int v13 = (v11 ^ (v12 << 2)) & 0xFFFFFFFF;

        final int v14 = (v12 ^ v10) & 0xFFFFFFFF;
        final int v15 = (v13 ^ (v14 >> 8)) & 0x00FF00FF;
        final int v16 = (v14 ^ (v15 << 8)) & 0xFFFFFFFF;

        final int v17 = ror(v15 ^ v13, 1);
        final int v18 = (v16 ^ v17) & 0x55555555;

        final int v3 = ror(v18 ^ v16, 1);
        final int v4 = (v18 ^ v17) & 0xFFFFFFFF;

        return ((v3 & 0xFFFFFFFF) << 32) | (v4 & 0xFFFFFFFF);
    }

    private static void fromInt(final byte[] data, final int state) {
        final int v3 = (state >> 32) & 0xFFFFFFFF;
        final int v4 = state & 0xFFFFFFFF;

        final int v22 = ror(v4, 31);
        final int v23 = (v3 ^ v22) & 0x55555555;
        final int v24 = (v23 ^ v22) & 0xFFFFFFFF;

        final int v25 = ror(v23 ^ v3, 31);
        final int v26 = (v25 ^ (v24 >> 8)) & 0x00FF00FF;
        final int v27 = (v24 ^ (v26 << 8)) & 0xFFFFFFFF;

        final int v28 = (v26 ^ v25) & 0xFFFFFFFF;
        final int v29 = ((v28 >> 2) ^ v27) & 0x33333333;
        final int v30 = ((v29 <<  2) ^ v28) & 0xFFFFFFFF;

        final int v31 = (v29 ^ v27) & 0xFFFFFFFF;
        final int v32 = (v30 ^ (v31 >> 16)) & 0x0000FFFF;
        final int v33 = (v31 ^ (v32 << 16)) & 0xFFFFFFFF;

        final int v34 = (v32 ^ v30) & 0xFFFFFFFF;
        final int v35 = (v33 ^ (v34 >> 4)) & 0xF0F0F0F;

        final int outY = ((v35 << 4) ^ v34) & 0xFFFFFFFF;
        final int outX = (v35 ^ v33) & 0xFFFFFFFF;

        data[0] = (byte)(outX & 0xFF);
        data[1] = (byte)((outX >> 8) & 0xFF);
        data[2] = (byte)((outX >> 16) & 0xFF);
        data[3] = (byte)((outX >> 24) & 0xFF);
        data[4] = (byte)(outY & 0xFF);
        data[5] = (byte)((outY >> 8) & 0xFF);
        data[6] = (byte)((outY >> 16) & 0xFF);
        data[7] = (byte)((outY >> 24) & 0xFF);
    }

    private static int operatorA(final int off, final int state) {
        int v3 = (state >> 32) & 0xFFFFFFFF;
        int v4 = state & 0xFFFFFFFF;

        for (int i = 0; i < 32; i += 4) {
            final int v20 = ror(v3 ^ KEY[off + i + 1], 28);

            v4 ^= LUT_B0[(v20 >> 26) & 0x3F] ^
                    LUT_B1[(v20 >> 18) & 0x3F] ^
                    LUT_B2[(v20 >> 10) & 0x3F] ^
                    LUT_B3[(v20 >> 2) & 0x3F] ^
                    LUT_A0[((v3 ^ KEY[off + i]) >> 26) & 0x3F] ^
                    LUT_A1[((v3 ^ KEY[off + i]) >> 18) & 0x3F] ^
                    LUT_A2[((v3 ^ KEY[off + i]) >> 10) & 0x3F] ^
                    LUT_A3[((v3 ^ KEY[off + i]) >> 2) & 0x3F];

            final int v21 = ror(v4 ^ KEY[off + i + 3], 28);

            v3 ^= LUT_B0[(v21 >> 26) & 0x3F] ^
                    LUT_B1[(v21 >> 18) & 0x3F] ^
                    LUT_B2[(v21 >> 10) & 0x3F] ^
                    LUT_B3[(v21 >> 2) & 0x3F] ^
                    LUT_A0[((v4 ^ KEY[off + i + 2]) >> 26) & 0x3F] ^
                    LUT_A1[((v4 ^ KEY[off + i + 2]) >> 18) & 0x3F] ^
                    LUT_A2[((v4 ^ KEY[off + i + 2]) >> 10) & 0x3F] ^
                    LUT_A3[((v4 ^ KEY[off + i + 2]) >> 2) & 0x3F];
        }

        return ((v3 & 0xFFFFFFFF) << 32) |
                    (v4 & 0xFFFFFFFF);
    }

    private static int operatorB(final int off, final int state) {
        int v3 = (state >> 32) & 0xFFFFFFFF;
        int v4 = state & 0xFFFFFFFF;

        for (int i = 0; i < 32; i += 4) {
            final int v20 = ror(v3 ^ KEY[off + 31 - i], 28);

            v4 ^= LUT_A0[((v3 ^ KEY[off + 30 - i]) >> 26) & 0x3F] ^
                    LUT_A1[((v3 ^ KEY[off + 30 - i]) >> 18) & 0x3F] ^
                    LUT_A2[((v3 ^ KEY[off + 30 - i]) >> 10) & 0x3F] ^
                    LUT_A3[((v3 ^ KEY[off + 30 - i]) >>  2) & 0x3F] ^
                    LUT_B0[(v20 >> 26) & 0x3F] ^
                    LUT_B1[(v20 >> 18) & 0x3F] ^
                    LUT_B2[(v20 >> 10) & 0x3F] ^
                    LUT_B3[(v20 >>  2) & 0x3F];

            final int v21 = ror(v4 ^ KEY[off + 29 - i], 28);

            v3 ^= LUT_A0[((v4 ^ KEY[off + 28 - i]) >> 26) & 0x3F] ^
                    LUT_A1[((v4 ^ KEY[off + 28 - i]) >> 18) & 0x3F] ^
                    LUT_A2[((v4 ^ KEY[off + 28 - i]) >> 10) & 0x3F] ^
                    LUT_A3[((v4 ^ KEY[off + 28 - i]) >>  2) & 0x3F] ^
                    LUT_B0[(v21 >> 26) & 0x3F] ^
                    LUT_B1[(v21 >> 18) & 0x3F] ^
                    LUT_B2[(v21 >> 10) & 0x3F] ^
                    LUT_B3[(v21 >>  2) & 0x3F];
        }

        return ((v3 & 0xFFFFFFFF) << 32) |
                    (v4 & 0xFFFFFFFF);
    }

    private static int ror(final int value, final int amount) {
        return ((value << (32 - amount)) & 0xFFFFFFFF) |
                    ((value >> amount) & 0xFFFFFFFF);
    }
}
