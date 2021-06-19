package Tetris;

public class U extends Tetromino {
    public U() {
        cells[0]=new Cell(0,3,Tetris.Z);
        cells[1]=new Cell(1,3,Tetris.Z);
        cells[2]=new Cell(1,4,Tetris.Z);
        cells[3]=new Cell(1,5,Tetris.Z);
        cells[4]=new Cell(0,5,Tetris.Z);
    }
}
