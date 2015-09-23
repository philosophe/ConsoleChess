package run;

import material.Move;
import material.Square;

public class MoveParser {

    private String move;

    public MoveParser(String move) throws IllegalArgumentException {
       this.move = move.replaceAll(" ", "").toLowerCase();
       int mlen = this.move.length();
       if (mlen != 4 && mlen != 2) throw new IllegalArgumentException(String.format("Unable to parse: %s", move));
    }

    public int getDestCol() throws IllegalArgumentException {
        return charToNum(move.charAt(2));
    }

    public int getDestRow() throws IllegalArgumentException {
        return charToNum(move.charAt(3));
    }

    public Square getDestSquare() throws IllegalArgumentException {
        return new Square(getDestRow(), getDestCol());
    }

    public int getStartCol() throws IllegalArgumentException {
        return charToNum(move.charAt(0));
    }

    public int getStartRow() throws IllegalArgumentException {
        return charToNum(move.charAt(1));
    }

    public Square getStartSquare() throws IllegalArgumentException {
        return new Square(getStartRow(), getStartCol());
    }

    public Move toMove() throws IllegalArgumentException {
        return new Move(getStartRow(), getStartCol(), getDestRow(), getDestCol());
    }

    private int charToNum(char a) throws IllegalArgumentException {
        switch (a) {
            case 'a': return 0;
            case '1': return 0;
            case 'b': return 1;
            case '2': return 1;
            case 'c': return 2;
            case '3': return 2;
            case 'd': return 3;
            case '4': return 3;
            case 'e': return 4;
            case '5': return 4;
            case 'f': return 5;
            case '6': return 5;
            case 'g': return 6;
            case '7': return 6;
            case 'h': return 7;
            case '8': return 7;
            default: throw new IllegalArgumentException(String.format("Could not convert char %s to an index", a));
        }
    }
} 
