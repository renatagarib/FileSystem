package structures;

public enum FileType{
    UNKNOWN ((byte) 0b00000000),
    REGULAR ((byte)0b00000001),
    DIRECTORY ((byte)0b00000010),
    CHARACTERDEVICE((byte)0b00000011),
    BLOCKDEVICE ((byte)0b00000100),
    FIFO ((byte)0b00000101),
    SOCKET ((byte)0b00000110),
    SYMBOLICLINK ((byte)0b00000111);

    private final byte id;

    FileType (byte id) {
        this.id = id;
    }

    private byte id() {
        return id;
    }

}
