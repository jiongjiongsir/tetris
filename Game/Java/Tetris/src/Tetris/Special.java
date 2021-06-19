package Tetris;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Special extends Tetromino{
    public Special(int col, BufferedImage image)
    {
        cell=new Cell(0,col,image);
        cells=null;
    }

}
