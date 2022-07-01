package exceptions;

public class VirtualFileNotFoundException extends Exception{
    public VirtualFileNotFoundException (String errorMessage) {
        super(errorMessage);
    }
}
