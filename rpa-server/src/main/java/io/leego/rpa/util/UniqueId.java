package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A globally unique identifier.
 *
 * @author Leego Yih
 */
public final class UniqueId implements Comparable<UniqueId>, Serializable {
    @Serial
    private static final long serialVersionUID = 4977846860435782934L;
    private static final int THREE_BYTES = 0x00FF_FFFF;
    private static final int WORKER_ID;
    private static final short SHARD_ID;
    private static final AtomicInteger ADDER;
    private final int timestamp;
    private final int value;
    private final int workerId;
    private final short shardId;
    private byte[] buffer;
    private String hex;

    static {
        SecureRandom random = new SecureRandom();
        WORKER_ID = random.nextInt(1 << 24);
        SHARD_ID = (short) random.nextInt(1 << 16);
        ADDER = new AtomicInteger(random.nextInt());
    }

    /**
     * Returns a new {@code UniqueId}.
     *
     * @return the {@code UniqueId}.
     */
    public static UniqueId next() {
        return new UniqueId();
    }

    /**
     * Returns a new {@code UniqueId} from the 24-byte hexadecimal string representation.
     *
     * @param hexString a potential {@code UniqueId} as a {@code String}.
     * @return the {@code UniqueId}.
     */
    public static UniqueId parse(String hexString) {
        return new UniqueId(hexString);
    }

    /**
     * Returns a new {@code UniqueId} from the given byte array, and the length must be equal to 12.
     *
     * @param bytes the byte array.
     * @return the {@code UniqueId}.
     */
    public static UniqueId parse(final byte[] bytes) {
        return new UniqueId(bytes);
    }

    /**
     * Checks if a string could be a {@code UniqueId}.
     *
     * @param hexString a potential {@code UniqueId} as a String.
     * @return whether the string could be a {@code UniqueId}.
     * @throws IllegalArgumentException if {@code hexString} is null.
     */
    public static boolean isValid(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        }
        int len = hexString.length();
        if (len != 24) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = hexString.charAt(i);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Constructs a new {@code UniqueId}.
     */
    public UniqueId() {
        this((int) (System.currentTimeMillis() / 1000));
    }

    /**
     * Constructs a new {@code UniqueId} with the specified unix timestamp.
     *
     * @param timestamp the specified unix timestamp.
     */
    public UniqueId(int timestamp) {
        this.timestamp = timestamp;
        this.value = ADDER.getAndIncrement() & THREE_BYTES;
        this.workerId = WORKER_ID;
        this.shardId = SHARD_ID;
    }

    /**
     * Constructs a new {@code UniqueId} with the specified unix timestamp and value.
     *
     * @param timestamp the specified unix timestamp.
     * @param value     the specified value.
     * @throws IllegalArgumentException if the value is illegal.
     */
    public UniqueId(int timestamp, int value) {
        if (value > THREE_BYTES || value < 0) {
            throw new IllegalArgumentException("The value must be between 0 and 16777215.");
        }
        this.timestamp = timestamp;
        this.value = value;
        this.workerId = WORKER_ID;
        this.shardId = SHARD_ID;
    }

    /**
     * Constructs a new {@code UniqueId} with the specified unix timestamp, value, worker id and shard id.
     *
     * @param timestamp the specified unix timestamp.
     * @param value     the specified value.
     * @param workerId  the worker id.
     * @param shardId   the shard id.
     * @throws IllegalArgumentException if the value or worker id is illegal.
     */
    public UniqueId(int timestamp, int value, int workerId, short shardId) {
        if (value > THREE_BYTES || value < 0) {
            throw new IllegalArgumentException("The value must be between 0 and 16777215.");
        }
        if (workerId > THREE_BYTES || workerId < 0) {
            throw new IllegalArgumentException("The worker identifier must be between 0 and 16777215.");
        }
        this.timestamp = timestamp;
        this.value = value;
        this.workerId = workerId;
        this.shardId = shardId;
    }

    /**
     * Constructs a new {@code UniqueId} from a 24-byte hexadecimal string representation.
     *
     * @param hexString the string to convert.
     * @throws IllegalArgumentException if the string is not a valid hex string representation of an {@code UniqueId}.
     */
    public UniqueId(String hexString) {
        this(Hex.decode(hexString));
    }

    /**
     * Constructs a new {@code UniqueId} from the given byte array.
     *
     * @param bytes the byte array.
     * @throws IllegalArgumentException if the byte array is null or not of length 12.
     */
    public UniqueId(final byte[] bytes) {
        if (bytes == null || bytes.length != 12) {
            throw new IllegalArgumentException("The byte array length must be equal to 12");
        }
        timestamp = (bytes[0] << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | ((bytes[3] & 0xFF));
        workerId = ((bytes[4] & 0xFF) << 16)
                | ((bytes[5] & 0xFF) << 8)
                | ((bytes[6] & 0xFF));
        shardId = (short) (((bytes[7] & 0xFF) << 8)
                | ((bytes[8] & 0xFF)));
        value = ((bytes[9] & 0xFF) << 16)
                | ((bytes[10] & 0xFF) << 8)
                | ((bytes[11] & 0xFF));
    }

    /**
     * Returns a byte array.
     *
     * @return a byte array.
     */
    public byte[] toByteArray() {
        return this.buffer != null
                ? this.buffer
                : (this.buffer = new byte[]{
                (byte) (timestamp >> 24),
                (byte) (timestamp >> 16),
                (byte) (timestamp >> 8),
                (byte) (timestamp),
                (byte) (workerId >> 16),
                (byte) (workerId >> 8),
                (byte) (workerId),
                (byte) (shardId >> 8),
                (byte) (shardId),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)});
    }

    /**
     * Returns a 24-byte hexadecimal string representation of the {@code UniqueId}.
     *
     * @return a 24-byte hexadecimal string representation of the {@code UniqueId}.
     */
    public String toHexString() {
        return hex != null ? hex : (hex = Hex.encode(toByteArray()));
    }

    /**
     * Returns the unix timestamp.
     *
     * @return the unix timestamp.
     */
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UniqueId uniqueId = (UniqueId) o;
        return timestamp == uniqueId.timestamp
                && value == uniqueId.value
                && workerId == uniqueId.workerId
                && shardId == uniqueId.shardId;
    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + value;
        result = 31 * result + workerId;
        result = 31 * result + shardId;
        return result;
    }

    @Override
    public int compareTo(UniqueId o) {
        if (o == null) {
            throw new NullPointerException();
        }
        byte[] cur = toByteArray();
        byte[] target = o.toByteArray();
        for (int i = 0; i < 12; i++) {
            if (cur[i] != target[i]) {
                return (cur[i] & 0xFF) < (target[i] & 0xFF) ? -1 : 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return toHexString();
    }

    static class Hex {
        /** Table for byte to hex string translation. */
        static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        /** Table for HEX to DEC byte translation. */
        static final int[] DEC = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};

        static String encode(byte[] bytes) {
            if (bytes == null) {
                return null;
            }
            int i = 0;
            char[] chars = new char[bytes.length << 1];
            for (byte b : bytes) {
                chars[i++] = HEX[(b & 0xf0) >> 4];
                chars[i++] = HEX[b & 0x0f];
            }
            return new String(chars);
        }

        static byte[] decode(String s) {
            if (s == null) {
                return null;
            }
            if ((s.length() & 1) == 1) {
                // Odd number of characters
                throw new IllegalArgumentException("Odd digits");
            }
            char[] chars = s.toCharArray();
            byte[] bytes = new byte[s.length() >> 1];
            for (int i = 0; i < bytes.length; i++) {
                int upper = DEC[chars[2 * i] - 48];
                int lower = DEC[chars[2 * i + 1] - 48];
                if (upper < 0 || lower < 0) {
                    // Non-hex character
                    throw new IllegalArgumentException("Non hex");
                }
                bytes[i] = (byte) ((upper << 4) + lower);
            }
            return bytes;
        }
    }
}

