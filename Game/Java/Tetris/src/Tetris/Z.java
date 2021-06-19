package Tetris;

public class Z extends Tetromino {
    /**
     * 提供构造器，进行初始化
     * Z型的四格方块的位置
     * */
    public Z() {
        cells[0]=new Cell(1,4,Tetris.Z);
        cells[1]=new Cell(0,3,Tetris.Z);
        cells[2]=new Cell(0,4,Tetris.Z);
        cells[3]=new Cell(1,5,Tetris.Z);
        cells[4]=new Cell(1,4,Tetris.Z);
    }
}