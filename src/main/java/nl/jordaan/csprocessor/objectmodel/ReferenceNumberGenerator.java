package nl.jordaan.csprocessor.objectmodel;

import java.util.UUID;

public class ReferenceNumberGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
