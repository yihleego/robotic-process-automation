package io.leego.rpa.util;

/**
 * @author Leego Yih
 */
public interface Code {

    int getType();

    int getTag();

    int getIndex();

    default int getTypeWidth() {
        return 3;
    }

    default int getTagWidth() {
        return 3;
    }

    default int getIndexWidth() {
        return 3;
    }

    default int toCode() {
        if (getType() < 0) {
            throw new IllegalArgumentException("The type cannot be negative");
        }
        if (getTag() < 0) {
            throw new IllegalArgumentException("The tag cannot be negative");
        }
        if (getIndex() < 0) {
            throw new IllegalArgumentException("The index cannot be negative");
        }
        if (getTagWidth() < 0) {
            throw new IllegalArgumentException("The tag width cannot be negative");
        }
        if (getIndexWidth() < 0) {
            throw new IllegalArgumentException("The index width cannot be negative");
        }
        int maxTag = (int) Math.pow(10, getTagWidth());
        int maxIndex = (int) Math.pow(10, getIndexWidth());
        if (getTag() >= maxTag) {
            throw new IllegalArgumentException("The tag cannot greater than or equal to " + maxTag);
        }
        if (getIndex() >= maxIndex) {
            throw new IllegalArgumentException("The index cannot greater than or equal to " + maxIndex);
        }
        return getType() * maxTag * maxIndex + getTag() * maxIndex + getIndex();
    }
}
