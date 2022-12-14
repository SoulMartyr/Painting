package UI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferStrategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import Shape.*;


/**
 * 主界面
 *
 * @author Liu
 * @date 2022/11/01
 */
public class MainUI extends JFrame {
    /**
     * 菜单栏
     */
    private JMenuBar _menubar;
    /**
     * 文件栏、操作栏
     */
    private JMenu _menuFile, _menuOperate;
    /**
     * 打开、保存、撤回、重做、设置背景颜色、清屏
     */
    private JMenuItem _menuOpen, _menuSave, _menuCancel, _menuRedo, _menuBgColor, _menuClear;
    /**
     * 菜单项功能数组
     */
    private Vector<JMenuItem> _menuItemVec;

    /**
     * 工具栏
     */
    private JToolBar _toolBar;
    /**
     * 按钮：直线、二次曲线、三角行、直角三角行、矩形、圆角矩形、圆形、多边形、画笔、橡皮擦、绘制颜色选择、绘制颜色浏览、填充颜色选择、线宽选择、填充颜色浏览
     */
    private JButton _btnLine, _btnQuad, _btnTriangle, _btnRightTriangle, _btnRectangle, _btnRoundedRectangle,
            _btnCircle, _btnPolygon, _btnBrush, _btnEraser, _btnDrawColorChooser, _drawColorViewer, _btnFillColorChooser, _btnLineWidth, _fillColorViewer;
    /**
     * 是否填充绘制
     */
    private JCheckBox _btnFill;
    /**
     * 工具栏按钮数组
     */
    private Vector<JButton> _toolBtnVec;

    /**
     * 容器
     */
    private Container _container;
    /**
     * 画布布局
     */
    private JPanel _canvasPanel;
    /**
     * 画布
     */
    private Canvas _canvas;

    /**
     * 功能
     */
    private Function _func;
    /**
     * 顶点数组
     */
    private Vector<Vertex> _vertices;
    /**
     * 双缓存策略
     */
    private BufferStrategy _strategy;
    /**
     * 图形基类
     */
    private Shape2D _shape2D;
    /**
     * 已绘制图形数组
     */
    private Vector<Shape2D> _shape2DVec;
    private BufferedImage _bgImage;
    /**
     * 线宽
     */
    private int _lineWidth;
    /**
     * 是否填充绘制
     */
    private boolean _isFill;
    /**
     * 绘制颜色、背景颜色、填充颜色
     */
    private Color _cvColor, _bgColor, _fillColor;
    /**
     * 上一个被按下的按钮、用于按钮间按下样式设置
     */
    private JButton _preBtn;
    /**
     * 已绘制图形数组的索引、用于撤销与重做
     */
    private int _vecIndex;
    /**
     * 是否按下shift键
     */
    private boolean isShifted;


    /**
     * 初始化窗口、控件与监听器
     */
    public MainUI() {
        super("Painting");
        InitSizeAndPosAndIcon();
        InitMenuBar();
        InitToolBar();
        InitCanvas();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        InitMenuListener();
        InitBtnListener();
        InitCanvasListener();
    }

    /**
     * 依据屏幕大小初始化窗口大小<br>
     * 窗口位于屏幕中心且长宽均为屏幕长宽的0.75
     * 初始化图标
     */
    private void InitSizeAndPosAndIcon() {
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolKit.getScreenSize();
        double width = screenDimension.getWidth();
        double height = screenDimension.getHeight();
        setSize((int) width * 3 / 4, (int) height * 3 / 4);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(MainUI.class.getResource("/Icon/Painting.png")).getImage());
    }

    /**
     * 初始化菜单栏
     */
    private void InitMenuBar() {
        _menuFile = new JMenu("File");
        _menuOperate = new JMenu("Operate");
        _menuOpen = new JMenuItem("Open");
        _menuSave = new JMenuItem("Save");
        _menuCancel = new JMenuItem("Cancel");
        _menuRedo = new JMenuItem("Redo");
        _menuBgColor = new JMenuItem("Background Color");
        _menuClear = new JMenuItem("All Clear");

        _menuFile.add(_menuOpen);
        _menuFile.add(_menuSave);
        _menuOperate.add(_menuCancel);
        _menuOperate.add(_menuRedo);
        _menuOperate.add(_menuBgColor);
        _menuOperate.add(_menuClear);

        _menuItemVec = new Vector<>();
        _menuItemVec.add(_menuOpen);
        _menuItemVec.add(_menuSave);
        _menuItemVec.add(_menuCancel);
        _menuItemVec.add(_menuRedo);
        _menuItemVec.add(_menuBgColor);
        _menuItemVec.add(_menuClear);

        _menubar = new JMenuBar();
        _menubar.add(_menuFile);
        _menubar.add(_menuOperate);

        setJMenuBar(_menubar);
    }

    /**
     * 初始化工具栏
     */
    private void InitToolBar() {
        String[] _toolBtnStr = {"Brush", "Line", "Quad", "Triangle", "RightTriangle", "Rectangle", "RoundedRectangle",
                "Circle", "Polygon", "Eraser", "LineWidth", "DrawColorChooser", "DrawColorViewer", "FillColorChooser", "FillColorViewer"};

        _toolBtnVec = new Vector<>();
        //绘图类
        _btnBrush = new JButton();
        _toolBtnVec.add(_btnBrush);
        _btnBrush.setToolTipText("Draw free.");
        _btnLine = new JButton();
        _toolBtnVec.add(_btnLine);
        _btnLine.setToolTipText("Draw Line.");
        _btnQuad = new JButton();
        _toolBtnVec.add(_btnQuad);
        _btnQuad.setToolTipText("Draw Quadratic Curve.");
        _btnTriangle = new JButton();
        _toolBtnVec.add(_btnTriangle);
        _btnTriangle.setToolTipText("Draw Triangle.");
        _btnRightTriangle = new JButton();
        _toolBtnVec.add(_btnRightTriangle);
        _btnRightTriangle.setToolTipText("Draw RightTriangle.");
        _btnRectangle = new JButton();
        _toolBtnVec.add(_btnRectangle);
        _btnRectangle.setToolTipText("Draw Rectangle.");
        _btnRoundedRectangle = new JButton();
        _toolBtnVec.add(_btnRoundedRectangle);
        _btnRoundedRectangle.setToolTipText("Draw RoundedRectangle.");
        _btnCircle = new JButton();
        _toolBtnVec.add(_btnCircle);
        _btnCircle.setToolTipText("Draw Circle.");
        _btnPolygon = new JButton();
        _toolBtnVec.add(_btnPolygon);
        _btnCircle.setToolTipText("Draw Polygon.Right click to set vertices number.");
        //操作类
        _btnEraser = new JButton();
        _toolBtnVec.add(_btnEraser);
        _btnEraser.setToolTipText("Eraser.");
        _btnLineWidth = new JButton();
        _toolBtnVec.add(_btnLineWidth);
        _btnLineWidth.setToolTipText("Set Line Width.");
        //颜色类
        _btnDrawColorChooser = new JButton();
        _toolBtnVec.add(_btnDrawColorChooser);
        _btnDrawColorChooser.setToolTipText("Set color of draw.");
        _drawColorViewer = new JButton();
        _toolBtnVec.add(_drawColorViewer);
        _btnFillColorChooser = new JButton();
        _toolBtnVec.add(_btnFillColorChooser);
        _fillColorViewer = new JButton();
        _toolBtnVec.add(_fillColorViewer);
        _btnFillColorChooser.setToolTipText("Set color of fill.");

        _btnFill = new JCheckBox("Fill");
        _btnFill.setSize(32, 32);
        _btnFill.setFont(new Font("宋体", 0, 16));

        for (int i = 0; i < _toolBtnVec.size(); i++) {
            JButton btn = _toolBtnVec.get(i);
            String functionName = Function.values()[i].toString();
            if (functionName.equals("DrawColorViewer") || functionName.equals("FillColorViewer")) {
                btn.setText("     ");
                btn.setFont(new Font("宋体", 1, 30));
                btn.setOpaque(true);
                btn.setEnabled(false);
                btn.setBackground(Color.BLACK);
            } else {
                btn.setText(functionName);
                btn.setFont(new Font("宋体", 1, 0));
                btn.setIcon(new ImageIcon(MainUI.class.getResource("/Icon/" + _toolBtnStr[i] + ".png")));
            }
            btn.setFocusPainted(false);
        }


        _toolBar = new JToolBar("ToolBar");

        for (int i = 0; i < _toolBtnVec.size(); i++) {
            JButton btn = _toolBtnVec.get(i);
            String functionName = Function.values()[i].toString();
            switch (functionName) {
                case "Brush" -> {
                    _toolBar.add(new JLabel("Draw"));
                    _toolBar.addSeparator();
                }
                case "Eraser" -> {
                    _toolBar.addSeparator();
                    _toolBar.add(new JLabel("Tool"));
                    _toolBar.addSeparator();
                }
                case "DrawColorChooser" -> {
                    _toolBar.addSeparator();
                    _toolBar.add(new JLabel("Current Draw Color"));
                    _toolBar.addSeparator();
                }
                case "FillColorChooser" -> {
                    _toolBar.addSeparator();
                    _toolBar.add(_btnFill);
                    _toolBar.addSeparator();
                    _toolBar.add(new JLabel("Current Fill Color"));
                    _toolBar.addSeparator();
                }
            }
            _toolBar.add(btn);
        }

        _container = getContentPane();
        _container.setLayout(new BorderLayout());
        _container.add(_toolBar, BorderLayout.NORTH);

    }


    /**
     * 初始化画布
     * <p>同时初始化绘制时的参数
     */
    private void InitCanvas() {
        _canvasPanel = new JPanel();
        _canvasPanel.setLayout(new GridLayout());
        _canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                g.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());
                Graphics2D g2D = (Graphics2D) g;
                for (int i = 0; i < _vecIndex; i++) {
                    Shape2D shape2D = _shape2DVec.get(i);
                    PaintShape2D(g2D, shape2D);
                }
            }
        };
        _canvas.setBackground(Color.WHITE);
        _canvas.setSize(new Dimension(this.getWidth(), this.getHeight()));
        _canvasPanel.add(_canvas);
        _container.add(_canvasPanel);

        _shape2DVec = new Vector<>();
        _func = Function.Line;
        _preBtn = _btnLine;

        _bgImage = null;
        _lineWidth = 4;
        _cvColor = Color.BLACK;
        _bgColor = Color.WHITE;
        _isFill = false;
        _fillColor = Color.BLACK;

        _vecIndex = 0;
        isShifted = false;


    }

    /**
     * 初始化菜单功能监听器
     */
    private void InitMenuListener() {
        for (JMenuItem jMenuItem : _menuItemVec) {
            jMenuItem.addActionListener(new MenuItemListener());
        }
    }

    /**
     * 初始化工具栏按钮监听器
     * <p>按下每个按钮后实现不同绘制功能与参数的设置
     */
    private void InitBtnListener() {
        for (JButton btn : _toolBtnVec) {
            btn.addActionListener(new BtnListener());
        }
        _btnFill.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                _isFill = !_isFill;
            }
        });
    }

    /**
     * 初始化画布监听器
     * <p>初始化双缓存策略，并加入对鼠标与键盘的监听
     */
    private void InitCanvasListener() {
        _canvas.createBufferStrategy(2);
        _strategy = _canvas.getBufferStrategy();
        _canvas.addMouseListener(new CanvasMouseListener());
        _canvas.addMouseMotionListener(new CanvasMouseListener());
        _canvas.addKeyListener(new CanvasKeyListener());
        _btnLine.doClick();
        _btnLine.setEnabled(false);
    }

    /**
     * 绘制图形
     *
     * @param graphics2D graphics2d，调用其draw方法实现多态
     * @param shape2D    图形基类
     */
    private void PaintShape2D(Graphics2D graphics2D, Shape2D shape2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(shape2D.getDrawColor());
        graphics2D.setStroke(new BasicStroke(shape2D.getLineWidth()));
        GeneralPath path = shape2D.generatePath();
        graphics2D.draw(path);
        if (shape2D.isFill()) {
            graphics2D.setColor(shape2D.getFillColor());
            graphics2D.fill(path);
        }
    }

    /**
     * 菜单栏监听器
     *
     * @author Liu
     * @date 2022/11/02
     */
    private class MenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = e.getActionCommand();
            switch (str) {
                case "Open" -> {
                    JFileChooser fileChooser = new JFileChooser("D:\\");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Png Img (*.png)", "png"));
                    int result = fileChooser.showOpenDialog(MainUI.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        try {
                            _bgImage = ImageIO.read(file);//获取图片
                            Graphics g = _canvas.getGraphics();
                            g.drawImage(_bgImage, 0, 0, _canvas.getWidth(), _canvas.getHeight(), MainUI.this);
                            _shape2DVec.clear();
                        } catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, ioe.toString(), "error", JOptionPane.ERROR_MESSAGE);
                            ioe.printStackTrace();
                        }
                    }

                }
                case "Save" -> {
                    BufferedImage img = new BufferedImage(
                            _canvas.getWidth(), _canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2D = img.createGraphics();
                    g2D.setColor(_bgColor);
                    g2D.fillRect(0, 0, _canvas.getWidth(), _canvas.getHeight());
                    for (Shape2D shape2D : _shape2DVec) {
                        PaintShape2D(g2D, shape2D);
                    }

                    JFileChooser fileChooser = new JFileChooser("D:\\");
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Png Img (*.png)", "png"));
                    int result = fileChooser.showSaveDialog(MainUI.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        if (!file.getAbsolutePath().endsWith(".png"))
                            file = new File(file.getAbsolutePath() + ".png");
                        try {
                            ImageIO.write(img, "png", file);
                        } catch (IOException ioe) {
                            JOptionPane.showMessageDialog(null, ioe.toString(), "error", JOptionPane.ERROR_MESSAGE);
                            ioe.printStackTrace();
                        }
                    }
                }
                case "Cancel" -> {
                    try {
                        _vecIndex -= 1;
                        _canvas.paint(_canvas.getGraphics());
                    } catch (IndexOutOfBoundsException iobe) {
                        _vecIndex += 1;
                        iobe.printStackTrace();
                    }
                }
                case "Redo" -> {
                    try {
                        _vecIndex += 1;
                        _canvas.paint(_canvas.getGraphics());
                    } catch (IndexOutOfBoundsException iobe) {
                        _vecIndex -= 1;
                        iobe.printStackTrace();
                    }
                }
                case "Background Color" -> {
                    Color color = JColorChooser.showDialog(MainUI.this, "Select a color", _cvColor);

                    _bgColor = color;
                    _canvas.setBackground(color);

                    for (Shape2D shape2D : _shape2DVec) {
                        if (shape2D instanceof Eraser)
                            shape2D.setDrawColor(_bgColor);
                    }
                }
                case "All Clear" -> {
                    Graphics g = _canvas.getGraphics();
                    g.setColor(_bgColor);
                    g.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());
                    _shape2DVec.clear();
                    _vecIndex = 0;
                    _bgImage = null;
                }

            }
        }
    }

    /**
     * 工具栏监听器
     *
     * @author Liu
     * @date 2022/11/02
     */
    private class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = e.getActionCommand();
            switch (str) {
                case "Brush", "Line", "Quad", "Triangle", "RightTriangle", "Rectangle", "RoundedRectangle",
                        "Circle", "Polygon", "Eraser" -> {

                    _func = Function.valueOf(e.getActionCommand());
                    _vertices = new Vector<>(_func.getVerticesNum());
                    _shape2D = utils.ActionSwitch(_vertices, _func);

                    JButton lastBtn = (JButton) e.getSource();
                    lastBtn.setEnabled(false);
                    _preBtn.setEnabled(true);

                    _preBtn = lastBtn;
                }
                case "LineWidth" -> {
                    String strLineWidth = JOptionPane.showInputDialog(MainUI.this, "LineWidth",
                            "Input LineWidth", JOptionPane.QUESTION_MESSAGE);
                    try {
                        _lineWidth = Integer.parseInt(strLineWidth);
                        _shape2D.setLineWidth(_lineWidth);
                    } catch (NullPointerException | NumberFormatException ne) {
                        JOptionPane.showMessageDialog(null, ne.toString(), "error", JOptionPane.ERROR_MESSAGE);
                        ne.printStackTrace();
                    }
                }
                case "DrawColorChooser" -> {
                    Color color = JColorChooser.showDialog(null, "Select a color", _cvColor);

                    _cvColor = color;
                    _drawColorViewer.setBackground(color);
                    _shape2D.setDrawColor(_cvColor);
                }
                case "FillColorChooser" -> {
                    Color color = JColorChooser.showDialog(null, "Select a color", _cvColor);

                    _fillColor = color;
                    _fillColorViewer.setBackground(color);
                }
            }
        }
    }

    /**
     * 画布鼠标功能监听器
     *
     * @author Liu
     * @date 2022/11/02
     */
    private class CanvasMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 3 && _func == Function.Polygon) {
                String strVerticesNum = JOptionPane.showInputDialog(MainUI.this, "Vertices Num(default:5)",
                        "Input VerticesNum", JOptionPane.QUESTION_MESSAGE);
                try {
                    Function.Polygon.setVerticesNum(Integer.parseInt(strVerticesNum));
                    _shape2D = utils.ActionSwitch(_vertices, _func);
                } catch (NullPointerException | NumberFormatException ne) {
                    JOptionPane.showMessageDialog(null, ne.toString(), "error", JOptionPane.ERROR_MESSAGE);
                    ne.printStackTrace();
                }
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() != 1) return;
            super.mousePressed(e);
            try {
                int count = _shape2DVec.size() - _vecIndex;
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        _shape2DVec.remove(_shape2DVec.size() - 1);
                    }
                    _shape2D.clear();
                }

                _shape2D.setLineWidth(_lineWidth);
                _shape2D.setFill(_isFill);
                _shape2D.setFillColor(_fillColor);

                utils.PressedSwitch(_shape2D, _func, _shape2DVec, e.getX(), e.getY());

                if (_func == Function.Eraser)
                    _shape2D.setDrawColor(_bgColor);
                else
                    _shape2D.setDrawColor(_cvColor);

            } catch (NullPointerException npe) {
                JOptionPane.showMessageDialog(null, npe.toString(), "error", JOptionPane.ERROR_MESSAGE);
                npe.printStackTrace();
            }
        }


        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() != 1) return;
            super.mouseReleased(e);
            try {
                utils.ReleasedSwitch(_shape2D, _func, _shape2DVec, e.getX(), e.getY());
                _vecIndex = _shape2DVec.size();
            } catch (NullPointerException npe) {
                JOptionPane.showMessageDialog(null, npe.toString(), "error", JOptionPane.ERROR_MESSAGE);
                npe.printStackTrace();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            try {
                do {
                    do {
                        Graphics2D graphics = (Graphics2D) _strategy.getDrawGraphics();

                        graphics.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight());

                        if (_bgImage != null)
                            graphics.drawImage(_bgImage, 0, 0, _canvas.getWidth(), _canvas.getHeight(), MainUI.this);

                        for (Shape2D shape2D : _shape2DVec) {
                            PaintShape2D(graphics, shape2D);
                        }

                        utils.DraggedSwitch(_shape2D, _func, e.getX(), e.getY());
                        if (isShifted)
                            _shape2D.correctVertex();
                        PaintShape2D(graphics, _shape2D);

                        graphics.dispose();

                    } while (_strategy.contentsRestored());
                    _strategy.show();

                } while (_strategy.contentsLost());
            } catch (NullPointerException | ArrayIndexOutOfBoundsException nae) {
                JOptionPane.showMessageDialog(null, nae.toString(), "error", JOptionPane.ERROR_MESSAGE);
                nae.printStackTrace();
            }
        }
    }

    /**
     * 画布键盘功能监听器
     *
     * @author Liu
     * @date 2022/11/02
     */
    private class CanvasKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                isShifted = true;
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ie) {
                    JOptionPane.showMessageDialog(null, ie.toString(), "error", JOptionPane.ERROR_MESSAGE);
                    ie.printStackTrace();
                }
                isShifted = false;
            }
        }
    }


}
