package Tetris;

import java.awt.image.BufferedImage;

/*
 * 俄罗斯方块中的最小单位:方格(细胞)
 * 特征(属性):
 * 		row--行号
 * 		col--列号
 * 		image--对应的图片
 *  行为(方法)
 *  	left()
 *  	right()
 *  	drop()
 */
public class Cell {

    private int row;
    private int col;
    private BufferedImage image;

    public Cell() {}
    public Cell(int row, int col, BufferedImage image) {
        super();
        this.row = row;
        this.col = col;
        this.image = image;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void left() {
        col--;//向左移动,列数减1
    }
    public void right() {
        col++;//向右移动,列数加1
    }
    public void drop() {
        row++;//向下移动,行数+1
    }
}