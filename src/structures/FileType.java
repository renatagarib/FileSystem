package structures;

public enum FileType{
    UNKNOWN ((byte) 0b00000000),
    REGULAR ((byte)0b00000001),
    DIRECTORY ((byte)0b00000010),
    CHARACTER_DEVICE((byte)0b00000011),
    BLOCK_DEVICE ((byte)0b00000100),
    FIFO ((byte)0b00000101),
    SOCKET ((byte)0b00000110),
    SYMBOLIC_LINK ((byte)0b00000111);

    private final byte id;

    FileType (byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

}
