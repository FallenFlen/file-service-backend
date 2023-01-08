package com.flz.downloadandupload.common.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ByteUtils {
    public static byte[] merge(List<byte[]> byteArrays) {
        int length = byteArrays.stream()
                .map((bytes) -> bytes.length)
                .reduce(0, Integer::sum);
        byte[] result = new byte[length];
        AtomicInteger currentPos = new AtomicInteger(0);
        byteArrays.forEach((bytes) -> {
            System.arraycopy(bytes, 0, result, currentPos.get(), bytes.length);
            currentPos.addAndGet(bytes.length);
        });
        return result;
    }
}
