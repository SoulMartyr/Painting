package Shape;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Vector;

/**
 * 直角三角形类
 *
 * @author Liu
 * @date 2022/11/01
 */
public class RightTriangle extends Shape2D {
    public RightTriangle(Vector<Vertex> vertices) {
        super(vertices);
    }

    public RightTriangle(Vector<Vertex> vertices, int verticesNum, int lineWidth, Color drawColor, boolean isFill, Color fillColor) {
        super(vertices, verticesNum, lineWidth, drawColor, isFill, fillColor);
    }

    @Override
    public GeneralPath generatePath() {
        GeneralPath path = new GeneralPath();
        if (_verticesNum == 2) {
            path = new Line(_vertices, _verticesNum).generatePath();
        }
        if (_verticesNum == 3) {
            calThirdVerTex();
            path.moveTo(_vertices.get(0).getX(), _vertices.get(0).getY());
            path.lineTo(_vertices.get(1).getX(), _vertices.get(1).getY());
            path.lineTo(_vertices.get(2).getX(), _vertices.get(2).getY());
            path.closePath();
        }
        return path;
    }

    @Override
    public Shape2D cloneShape2D() {
        return new RightTriangle(_vertices, _verticesNum, _lineWidth, _drawColor, _isFill, _fillColor);
    }

    /**
     * 计算第三个顶点
     * <p>第一个顶点与第三个顶点分别与第二个顶点的连线恒垂直
     */
    private void calThirdVerTex() {
        int x1 = _vertices.get(0).getX(), y1 = _vertices.get(0).getY();
        int x2 = _vertices.get(1).getX(), y2 = _vertices.get(1).getY();
        int x3 = _vertices.get(2).getX();
        int y3 = (int) ((x3 - x2) * (x1 - x2) / (y2 - y1) + y2);
        _vertices.get(2).setXY(x3, y3);
    }
}
