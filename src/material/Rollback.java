package material;

public class Rollback {
    
    private Piece src;
    private Piece capture;
    private Square captureSquare;
    private Square srcSquare;

    public Rollback(Piece src, Piece capture, Square srcSquare, Square captureSquare) {
        this.src = src;
        this.capture = capture;
        this.srcSquare = srcSquare;
        this.captureSquare = captureSquare;
    }

    public Rollback(Piece src, Square srcSquare) {
        this(src, null, srcSquare, null);
    }

    public Piece getCapture() {
        return capture;
    }

    public Square getCaptureDest() {
        return captureSquare;
    }

    public Piece getSrc() {
        return src;
    }

    public Square getSrcDest() {
        return srcSquare;
    }

}
