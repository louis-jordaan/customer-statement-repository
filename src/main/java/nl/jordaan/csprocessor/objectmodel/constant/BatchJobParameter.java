package nl.jordaan.csprocessor.objectmodel.constant;

public enum BatchJobParameter {
    INPUT_FILE_NAME("inputFileName"), OUTPUT_FILE_NAME("outputFileName");

    private final String key;

    BatchJobParameter(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
