package Shape;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.GeneralPath;
import java.util.Vector;

/**
 * 圆角矩形类
 *
 * @author Liu
 * @date 2022/11/01
 */
public class RoundedRectangle extends Rectangle {
    public RoundedRectangle(Vector<Vertex> vertices) {
        super(vertices);
    }

    public RoundedRectangle(Vector<Vertex> vertices, int verticesNum, int lineWidth, Color drawColor, boolean isFill, Color fillColor) {
        super(vertices, verticesNum, lineWidth, drawColor, isFill, fillColor);
    }

    @Override
    public GeneralPath generatePath() {
        GeneralPath path = new GeneralPath();
        if (_verticesNum == 2) {
            path = new Line(_vertices, _verticesNum).generatePath();
        } else if (_verticesNum == 4) {
            path = new Rectangle(_vertices, _verticesNum).generatePath();
        } else if (_verticesNum == 5) {
            calThirdVerTex();
            calForthVerTex();

            RoundRectangle2D roundRectangle = new RoundRectangle2D.Double();
            //计算圆角半径
            double r = Vertex.calDistance(_vertices.get(2), _vertices.get(4));

            Vertex[] vertices = sortVertices();

            double w = Vertex.calDistance(vertices[1], vertices[2]);
            double h = Vertex.calDistance(vertices[0], vertices[1]);
            //构建全等的水平圆角矩形
            roundRectangle.setRoundRect(vertices[0].getX(), vertices[0].getY(), w, h, r, r);
            path = new GeneralPath(roundRectangle);
            //旋转水平的圆角矩形
            double theta = Math.atan((double) (vertices[3].getY() - vertices[0].getY()) / (vertices[3].getX() - vertices[0].getX()));
            path.transform(AffineTransform.getRotateInstance(theta, vertices[0].getX(), vertices[0].getY()));
        }
        return path;
    }

    @Override
    public Shape2D cloneShape2D() {
        return new RoundedRectangle(_vertices, _verticesNum, _lineWidth, _drawColor, _isFill, _fillColor);
    }


    /**
     * 顶点排序
     * <p>将左上角设为第一个顶点，逆时针排序
     * <p>依据左上角顶点的X、Y坐标恒在矩形中心找到其目前的索引<br>
     *    再判断出下一个顶点的顺逆时针关系矫正坐标索引
     * @return {@link Vertex[]}
     */
    private Vertex[] sortVertices() {
        int CenterX = (_vertices.get(0).getX() + _vertices.get(2).getX()) / 2;
        int CenterY = (_vertices.get(0).getY() + _vertices.get(2).getY()) / 2;
        int LUIdx = 0;
        for (int i = 0; i < 4; i++) {
            if (_vertices.get(i).getX() < CenterX && _vertices.get(i).getY() < CenterY) {
                LUIdx = i;
                break;
            }
        }
        int nextIdx = (LUIdx + 1) % 4;
        int det = (_vertices.get(LUIdx).getX() - CenterX) * (_vertices.get(nextIdx).getY() - CenterY) - (_vertices.get(nextIdx).getX() - CenterX) * (_vertices.get(LUIdx).getY() - CenterY);
        Vertex[] vertices = new Vertex[5];
        if (det < 0) {
            for (int i = 0; i < 4; i++) {
                vertices[i] = new Vertex(_vertices.get((LUIdx + i) % 4));
            }
        } else {
            for (int i = 0; i < 4; i++) {
                vertices[i] = new Vertex(_vertices.get((LUIdx + 4 - i) % 4));
            }
        }
        return vertices;
    }
}
