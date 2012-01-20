package org.cyrano.jogl._16.curves_2;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.cyrano.jogl.base.BaseExample;
import org.cyrano.jogl.base.Camera;
import org.cyrano.jogl.base.Primitives;
import org.cyrano.jogl.util.TextureCache;

/**
 * @author Demián Gutierrez
 */
public class Main extends BaseExample {

  // --------------------------------------------------------------------------------

  private int xMousePick;
  private int yMousePick;

  private boolean pick;

  private Camera camera = new Camera();

  private static final int uSize = 2;
  private static final int vSize = 2;
  private static final int gridSize = 20;

  private double[] grid2x2 = { //
  /**/-2.0, -2.0, +0.0,//
      +2.0, -2.0, +0.0,//
      -2.0, +2.0, +0.0,//
      +2.0, +2.0, +0.0};

  // --------------------------------------------------------------------------------

  public void init(GLAutoDrawable drawable) {
    //    drawable.addMouseMotionListener(camera);
    //    drawable.addMouseListener/*  */(camera);
    drawable.addKeyListener/*    */(camera);

    drawable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent evt) {
        Main.this.mouseClicked(evt);
      }
    });

    drawable.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent evt) {
        motion(evt);
      }
    });

    GL gl = drawable.getGL();

    TextureCache.init("textures");

    gl.glDisable(GL.GL_CULL_FACE);
    gl.glEnable(GL.GL_DEPTH_TEST);

    gl.glEnable(GL.GL_MAP2_VERTEX_3);
    gl.glMapGrid2d(gridSize, 0.0, 1.0, gridSize, 0.0, 1.0);

  }

  // --------------------------------------------------------------------------------

  double[] projMatrix = new double[16];
  double[] modelMatrix = new double[16];

  public void reshape(GLAutoDrawable drawable, //
      int x, int y, int w, int h) {

    this.w = w;
    this.h = h;

    GL gl = drawable.getGL();
    gl.glViewport(0, 0, w, h);

    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projMatrix, 0);

    camera.updateCameraBox();
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelMatrix, 0);
  }

  // --------------------------------------------------------------------------------

  public void display(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | //
        /*   */GL.GL_DEPTH_BUFFER_BIT);

    gl.glLoadIdentity();

    camera.updateCameraBox();
    camera.updateCameraPos();

    // ----------------------------------------

    //    float[] points = {-40, 40, 0, -10, 200, 0, 10, -200, 0, 40, 40, 0};
    //    
    //    gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0, 1, 3, 4, points, 0);
    //    gl.glEnable(GL.GL_MAP1_VERTEX_3);
    //
    //    
    //    gl.glColor3f(1,  1, 0);
    //    
    //    gl.glBegin(GL.GL_LINE_STRIP);
    //    
    //    for (int k = 0; k <= 50; k++) {
    //      gl.glEvalCoord1f(k / 50f);
    //    }
    //    
    //    gl.glEnd();

    if (pick) {
      drawControlPoints(gl);

      System.err.println("picked ID is: " + getPickId(gl));

      selectedPoint = getPickId(gl) - 1;
      
      System.err.println("selected point: " + selectedPoint);

      pick = false;
    }

    // ----------------------------------------

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    Primitives.drawAxes(gl);
    evaluateGrid(gl);
    drawControlPoints(gl);

    // ----------------------------------------

    gl.glFlush();
  }

  private void evaluateGrid(GL gl) {
    gl.glColor3f(1.0f, 1.0f, 1.0f);
    gl.glMap2d(GL.GL_MAP2_VERTEX_3, 0, 1, 3, uSize, 0, 1, uSize * 3, vSize, grid2x2, 0);
    gl.glEvalMesh2(GL.GL_LINE, 0, gridSize, 0, gridSize);
  }

  private void drawControlPoints(GL gl) {
    int i;

    //gl.glColor3f(1.0f, 1.0f, 0.0f);
    gl.glPointSize(10.0f);

    gl.glBegin(GL.GL_POINTS);

    for (i = 0; i < uSize * vSize; i++) {
      glSetColorAndId(gl, 1.0f, 1.0f, 0.0f, (byte) (i + 1));
      gl.glVertex3dv(grid2x2, i * 3);
    }

    gl.glEnd();
  }

  private byte getPickId(GL gl) {
    int[] viewport = new int[4];

    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

    ByteBuffer bb = ByteBuffer.allocate(4);

    gl.glReadPixels( //
        /*          */xMousePick, //
        viewport[3] - yMousePick, //
        1, 1, GL.GL_RGBA, GL.GL_BYTE, bb);

    return bb.get(3);
  }

  // --------------------------------------------------------------------------------

  private void glSetColorAndId(GL gl, float r, float g, float b, byte id) {
    if (pick == true) {
      gl.glColor4b((byte) 0, (byte) 0, (byte) 0, id);
    } else {
      gl.glColor3f(r, g, b);
    }
  }

  // --------------------------------------------------------------------------------

  public void displayChanged(GLAutoDrawable drawable, //
      boolean modeChanged, boolean deviceChanged) {
    // Empty
  }

  // --------------------------------------------------------------------------------

  private void mouseClicked(MouseEvent evt) {
    xMousePick = evt.getX();
    yMousePick = evt.getY();

    System.err.println(xMousePick + ";" + yMousePick);

    pick = true;

    glCanvas.repaint();
  }

  int selectedPoint = -1;

  int w;
  int h;

  public void motion(MouseEvent evt) {

    double[] obj = new double[3];

    if (selectedPoint != -1) {
      int[] viewport = new int[]{0, 0, w, h};

      GLU glu = new GLU();

      glu.gluUnProject(evt.getX(), h - evt.getY(), 0.95, modelMatrix, 0, projMatrix, 0, viewport, 0, obj, 0);

      //      glu.gluUnProject(evt.getX(), h - evt.getY(), 0.95,
      //        modelMatrix, 0, projMatrix, 0, viewport,
      //        obj, 0);

      grid2x2[selectedPoint * 3 + 0] = obj[0];
      grid2x2[selectedPoint * 3 + 1] = obj[1];

      glCanvas.repaint();
    }
  }

  // --------------------------------------------------------------------------------

  public static void main(String[] args) {
    new Main().run();
  }
}