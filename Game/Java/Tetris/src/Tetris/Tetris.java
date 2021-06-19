package Tetris;

//import org.graalvm.compiler.graph.Graph;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * 俄罗斯方块的主类:
 * 前提：必须是一块面板Jpanel,可以嵌入窗口
 * 面板上自带一个画笔，有一个功能：自动绘制
 * 其实是调用了JPanel里的paint()方法
 *
 *
 * (1)加载静态资源
 */
public class Tetris extends JPanel {

    /*属性：正在下落的四格方块*/
    private Tetromino currentOne = Tetromino.randomOneInit();
    /*属性：将要下落的四格方块*/
    private Tetromino nextOne = Tetromino.randomOneInit();
    /*属性：墙,20行 10列的 表格  宽度为26*/
    private Cell[][] wall = new Cell[20][10];
    private static final int CELL_SIZE = 26;

    private long startTime = System.currentTimeMillis();
    private long cellStartTime;



    int[] scores_pool = {0, 1, 2, 5, 10};

    private int totalScore = 0;//总分
    private int totalLine = 0;//总行数

    public static final int PLAYING = 0;
    public static final int PAUSE = 1;
    public static final int GAMEOVER = 2;

    private int game_state;

    String[] showState = {"P[pause]", "C[continue]", "Enter[replay]"};

    public static BufferedImage T;//各种形状的方块
    public static BufferedImage I;
    public static BufferedImage O;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage S;
    public static BufferedImage Z;
    public static BufferedImage U= Tetris.Z;
    public static BufferedImage background;//游戏背景
    public static BufferedImage game_over;//游戏结束

    static {
        try {
            //getResource(String url) url:加载图片的路径 相对位置是同包下
            T = ImageIO.read(Tetris.class.getResource("images/T.png"));
            O = ImageIO.read(Tetris.class.getResource("images/O.png"));
            I = ImageIO.read(Tetris.class.getResource("images/I.png"));
            J = ImageIO.read(Tetris.class.getResource("images/J.png"));
            L = ImageIO.read(Tetris.class.getResource("images/L.png"));
            S = ImageIO.read(Tetris.class.getResource("images/S.png"));
            Z = ImageIO.read(Tetris.class.getResource("images/Z.png"));
            background = ImageIO.read(Tetris.class.getResource("images/tetris.png"));
            game_over = ImageIO.read(Tetris.class.getResource("images/game-over.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        // 绘制背景,在区域1
        //g：画笔 g.drawImage(image,x,y,null) image:绘制的图片 x:开始绘制的横坐标 y:开始绘制的纵坐标
        g.drawImage(background, 0, 0, null);
        // 平移坐标轴
        g.translate(15, 15);
        // 绘制墙
        paintWall(g);
        // 绘制正在下落的四格方块,在区域5
        paintCurrentOne(g);
        // 绘制下一个将要下落的四格方块,在区域2
        paintNextOne(g);
        paintScore(g);//绘制游戏分数和列数,分数在区域3,列数在区域4
        paintState(g);//绘制游戏状态,在区域6
    }

    private void paintState(Graphics g) {//在右侧绘制游戏状态
        if (game_state == GAMEOVER) {//游戏结束
            g.drawImage(game_over, 0, 0, null);
            g.drawString(showState[GAMEOVER], 285, 265);
        }
        if (game_state == PLAYING) {//正在游戏
            g.drawString(showState[PLAYING], 285, 265);
        }
        if (game_state == PAUSE) {//暂停游戏
            g.drawString(showState[PAUSE], 285, 265);
        }

    }

    public void paintScore(Graphics g) {//在右侧位置绘制游戏分数
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 26));
        g.drawString("SCORES:" + totalScore, 285, 165);
        g.drawString("LINES:" + totalLine, 285, 215);
    }

    /**
     * 绘制下一个将要下落的四格方块 绘制到面板的右上角的相应区域
     */
    public void paintNextOne(Graphics g) {
        // 获取nextOne对象的四个元素
        if(nextOne.cells!=null) {
            Cell[] cells = nextOne.cells;
            for (Cell c : cells) {
                // 获取每一个元素的行号和列号
                int row = c.getRow();
                int col = c.getCol();
                // 横坐标和纵坐标
                int x = col * CELL_SIZE + 260;
                int y = row * CELL_SIZE + 26;
                g.drawImage(c.getImage(), x, y, null);
            }
        }
        else
        {

            Cell cell=nextOne.cell;
            int row = cell.getRow();
            int col = cell.getCol();
            // 横坐标和纵坐标
            int x = 6 * CELL_SIZE + 220;
            int y = row * CELL_SIZE + 35;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    /**
     * 绘制正在下落的四格方块 取出数组的元素 绘制元素的图片， 横坐标x: 纵坐标y:
     */
    public void paintCurrentOne(Graphics g) {
        if(currentOne.cells!=null)
        {
            Cell[] cells = currentOne.cells;
            for (Cell c : cells) {
                int x = c.getCol() * CELL_SIZE;
                int y = c.getRow() * CELL_SIZE;
                g.drawImage(c.getImage(), x, y, null);
            }
        }
        else {
            Cell cell=currentOne.cell;
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);

        }

    }


    /**
     * 墙是20行，10列的表格 是一个二维数组， 应该使用双层循环 绘制正方形。
     */
    public void paintWall(Graphics a) {
        // 外层循环控制行数
        for (int i = 0; i < 20; i++) {
            // 内层循环控制列数
            for (int j = 0; j < 10; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                if (cell == null) {
                    a.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    a.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    public boolean isGameOver() {
        if(nextOne.cells!=null)
        {
            Cell[] cells = nextOne.cells;
            for (Cell c : cells) {
                int row = c.getRow();
                int col = c.getCol();
                if (wall[row][col] != null) {//若方块已经达到第20行,则游戏结束
                    return true;
                }
            }
        }
        if(currentOne.cells==null)
        {
            Cell cell=currentOne.cell;
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row+1][col] != null) {//若方块已经达到第20行,则游戏结束
                return true;
            }
        }
        return false;
    }

    public boolean isFullLine(int row) {

        Cell[] line = wall[row];
        for (Cell c : line) {
            if (c == null) {//遍历到为空的方块即返回false,表明这一行没有满.
                return false;
            }
        }
        return true;
    }


    public void destroyLine() {
        int lines = 0;
        //获取当前正在下落的形状方块

        /**
         * 请在下方补全代码
         */
        List<Integer> list = new ArrayList<>();
        for (int x = 0; x < 20; x++) {
            int flag = 0;
            for (int i = 0; i < 10; i++) {
//                System.out.print(wall[x][i]+" ");
                if (wall[x][i] == null) {
                    flag = 1;
                    break;
                }

            }

            if (flag == 1) {
                continue;
            } else {
                list.add(x);
                startTime = System.currentTimeMillis();
            }
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < 10; j++) {
                wall[list.get(i)][j] = null;
            }
            lines++;
            for (int m = list.get(i); m > 0; m--) {
                for (int n = 0; n < 10; n++) {
                    wall[m][n] = wall[m - 1][n];
                }

            }

        }


        //获取分数
        totalScore += scores_pool[lines];
        totalLine += lines;

    }

    public boolean canDrop() {
        if(currentOne.cells!=null) {
            Cell[] cells = currentOne.cells;//当前方块数组
            for (Cell c : cells) {
                int row = c.getRow();
                int col = c.getCol();
                if (row == 19) {//落到底了
                    return false;
                }
                if (wall[row + 1][col] != null) {//某一元素下面不为空
                    return false;
                }
            }
        }
        else
        {
            Cell cell=currentOne.cell;
            int row = cell.getRow();
            int col = cell.getCol();
            if (row == 19) {//落到底了
                return false;
            }
            if (wall[row + 1][col] != null) {//某一元素下面不为空
                return false;
            }
        }
        return true;
    }


    public void landToWall() {
        if(currentOne.cells!=null)
        {
            Cell[] cells = currentOne.cells;
            for (Cell c : cells) {
                // 获取最终的行号和列号
                int row = c.getRow();
                int col = c.getCol();
                wall[row][col] = c;
            }
        }
        else {
            Cell c=currentOne.cell;
            int row = c.getRow();
            int col = c.getCol();
            wall[row][col] = c;
        }

    }

    public boolean outOfBounds() {//越界异常
        if(currentOne.cells!=null)
        {
            Cell[] cells = currentOne.cells;
            for (Cell c : cells) {
                int col = c.getCol();
                int row = c.getRow();
                if (col < 0 || col > 9 || row > 19 || row < 0) {//不能越过wall[][]
                    return true;
                }
            }
        }
        else {
            Cell c=currentOne.cell;
            int col = c.getCol();
            int row = c.getRow();
            if (col < 0 || col > 9 || row > 19 || row < 0) {//不能越过wall[][]
                return true;
            }
        }

        return false;
    }

    public boolean coincide() {//两个方块重合
        if(currentOne.cells!=null)
        {
            Cell[] cells = currentOne.cells;
            for (Cell c : cells) {
                int row = c.getRow();
                int col = c.getCol();
                if (wall[row][col] != null) {
                    return true;
                }
            }
        }
        else
        {
            Cell c=currentOne.cell;
            int row = c.getRow();
            int col = c.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }

        return false;
    }

    protected void moveLeftAction() {
        currentOne.moveLeft();
        if (outOfBounds() || coincide()) {//如果左移出了边界,执行右移的方法防止游戏错误
            currentOne.moveRight();
        }
    }

    protected void moveRightAction() {
        currentOne.moveRight();
        if (outOfBounds() || coincide()) {//如果右移出了边界,执行左移的方法防止错误.
            currentOne.moveLeft();
        }

    }

    public void softDropAction() {
        if (canDrop()) {
            currentOne.softDrop();
        } else {
            landToWall();
            destroyLine();
            currentOne = nextOne;//把这一个方块"变成"下一个方块
            if(currentOne.cells==null)
            {
                cellStartTime=System.currentTimeMillis();
            }
            nextOne = Tetromino.randomOne();//再随机生成一个"下一个方块"

        }
    }

    public void handDropAction() {
        for (; ; ) {
            if (canDrop()) {
                currentOne.softDrop();
            } else {
                break;
            }
        }
        landToWall();
        destroyLine();
        if (!isGameOver()) {
            currentOne = nextOne;
            if(currentOne.cells==null)
            {
                cellStartTime=System.currentTimeMillis();
            }
            nextOne = Tetromino.randomOne();
        } else {
            game_state = GAMEOVER;
        }
    }

    public void rotateRightAction() {
        currentOne.rotateRight();
        if (outOfBounds() || coincide()) {//转过头了怎么办?这就是rotateLeft()方法的用处了
            currentOne.rotateLeft();
        }
    }

    public void randomProduct() {
        int num;
        if(currentOne.cells!=null)
        {
            num=0;
        }
        else
        {
            num=1;
        }
        for (int i = num; i < 19; i++) {
            for (int j = 0; j < 10; j++) {
                wall[i][j] = wall[i + 1][j];
            }
        }

        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            arr[i] = (Math.random()) > 0.5 ? 1 : 0;
//            System.out.println(arr[i]);
        }
        for (int i = 0; i < 10; i++) {
            if (arr[i] == 0) {
                wall[19][i] = null;
            } else {
                int x = (int) (Math.random() * 7);
                BufferedImage img = null;
                switch (x) {
                    case 0: {
                        img = T;
                        break;
                    }
                    case 1: {
                        img = I;
                        break;
                    }
                    case 2: {
                        img = O;
                        break;
                    }
                    case 3: {
                        img = J;
                        break;
                    }
                    case 4: {
                        img = L;
                        break;
                    }
                    case 5: {
                        img = S;
                        break;
                    }
                    case 6: {
                        img = Z;
                        break;
                    }
                }
                Cell cell = new Cell(19, i, img);
                wall[19][i] = cell;
            }
        }

    }



    public void ShotAddCell()
    {

        Cell cell=currentOne.cell;
        int y=cell.getCol();
        int i=1;

        for(i=1;i<19;i++)
        {
            if(wall[1][y]!=null)
            {
                break;
            }
            if(wall[i][y]==null&&wall[i+1][y]!=null)
            {
                wall[i][y]=cell;
                System.out.println(wall[i][y]);
                break;
            }


        }

        if(i==19)
        {
            wall[i][y]=cell;
        }

        destroyLine();
        if (isGameOver()) {
            game_state=GAMEOVER;
        }

    }
    public void showWall()
    {
        for(int m=0;m<20;m++)
        {
            System.out.print(m+": ");
            for(int n=0;n<10;n++)
            {
                if(wall[m][n]==null)
                {
                    System.out.print("null ");
                }
                else
                {
                    System.out.print(wall[m][n].getCol()+" ");
                }
            }
            System.out.println(" ");
        }
    }
    public void shotDeleteCell()
    {
        Cell cell=currentOne.cell;
        int y=cell.getCol();
        int i=1;

        for(i=1;i<20;i++)
        {
            if(wall[i][y]!=null)
            {
                wall[i][y]=null;
                break;
            }
        }
    }
    public void start() {//封装了游戏逻辑

        game_state = PLAYING;

//        startTime=System.currentTimeMillis();
        KeyListener l = new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_P) {//VK_P即表示键盘P键
                    if (game_state == PLAYING) {//状态为PLAYING才能暂停
                        game_state = PAUSE;
                    }
                }

                if (code == KeyEvent.VK_C) {
                    if (game_state == PAUSE) {
                        game_state = PLAYING;
                    }
                }

                if (code == KeyEvent.VK_S) {
                    startTime=System.currentTimeMillis();
                    game_state = PLAYING;
                    wall = new Cell[20][10];//画一个新的"墙"
                    currentOne = Tetromino.randomOne();
                    nextOne = Tetromino.randomOne();
                    totalScore = 0;//分数置为0
                    totalLine = 0;//列数置为0
                }

                switch (code) {
                    case KeyEvent.VK_DOWN://按下缓慢下降
                    {
                        if (currentOne.cells!=null) {
                            softDropAction();
                        } else  {
//                            发射子弹
                            shotDeleteCell();
                        }
                        break;
                    }

                    case KeyEvent.VK_LEFT://按左左移
                        moveLeftAction();
                        break;
                    case KeyEvent.VK_RIGHT://按右右移
                        moveRightAction();
                        break;
                    case KeyEvent.VK_UP://按上变形
                    {
                        if (currentOne.cells!=null) {
                            rotateRightAction();
                        } else
                            {
                                ShotAddCell();
                        }
                        break;
                    }
                    case KeyEvent.VK_SPACE://按空格直接到底
                    {
                        if (game_state == PLAYING&&currentOne.cells!=null) {
                            handDropAction();
                        }
                        break;
                    }
                }
                repaint();//每操作一次都要重新绘制方块
            }
        };//内部类

        this.addKeyListener(l);
        this.requestFocus();

        while (true) {
            int during=(int)(System.currentTimeMillis()-startTime)/1000;
            int lasttime=(int)(System.currentTimeMillis()-cellStartTime)/1000;
            if (during >= 10&&game_state==PLAYING) {
                this.startTime = System.currentTimeMillis();
                during = 0;
                randomProduct();
            }
            if(lasttime>=5&&currentOne.cells==null)
            {
                currentOne=nextOne;
                if(currentOne.cells==null)
                {
                    cellStartTime=System.currentTimeMillis();
                }
                nextOne=Tetromino.randomOne();
            }
/**
 * 当程序运行到此，会进入睡眠状态， 睡眠时间为800毫秒，单位为毫秒 800毫秒后，会自动执行后续代码
 */
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (game_state == PLAYING&&currentOne.cells!=null) {
                if (canDrop()) {
                    currentOne.softDrop();
                } else {
                    landToWall();
                    destroyLine();
                    // 将下一个下落的四格方块赋值给正在下落的变量
//
                    if (!isGameOver()) {
                        if (game_state == PLAYING) {
                            currentOne = nextOne;
                            if(currentOne.cells==null)
                            {
                                cellStartTime=System.currentTimeMillis();
                            }
                            nextOne = Tetromino.randomOne();
                        }
                    } else {
                        game_state = GAMEOVER;

                    }
                }

                repaint();
                /*
                 * 下落之后，要重新进行绘制，才会看到下落后的 位置 repaint方法 也是JPanel类中提供的 此方法中调用了paint方法
                 */
            }

        }
    }


    public static void main(String[] args) {


        // 1:创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");
        // 创建游戏界面，即面板
        Tetris panel = new Tetris();
        // 将面板嵌入窗口
        frame.add(panel);
        // 2:设置为可见
        frame.setVisible(true);
        // 3:设置窗口的尺寸
        frame.setSize(535, 580);
        // 4:设置窗口居中
        frame.setLocationRelativeTo(null);
        // 5:设置窗口关闭，即程序终止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 游戏的主要逻辑封装在start方法中
        panel.start();
    }

}
