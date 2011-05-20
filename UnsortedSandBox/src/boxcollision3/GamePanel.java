package boxcollision3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.cyrano.util.Hwh;

import boxcollision3.Box.CollisionInfo;

/**
 * @author Demián Gutierrez
 */
public class GamePanel extends JPanel implements Runnable {

  private boolean running = true;

  private List<Box> drawBoxList = new ArrayList<Box>();
  private List<Box> doneBoxList = new ArrayList<Box>();

  // --------------------------------------------------------------------------------

  public GamePanel() {

        drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX0.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX1.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX2.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX3.txt"));
    //  drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX4.txt"));
//    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX5.txt"));
//    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataX6.txt"));

    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataY0.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataY1.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataY2.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataY3.txt"));
    //    drawBoxList = BoxReader.fsRead(getClass().getResourceAsStream("dataY4.txt"));

    // ----------------------------------------

    Thread thread = new Thread(this);
    thread.start();
  }

  // --------------------------------------------------------------------------------

  @Override
  public void run() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long t1 = System.currentTimeMillis();

    int sleep = 20;
    int count = 0;

    while (running) {
      try {
        Thread.sleep(sleep);

        repaint();

        count++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      long t2 = System.currentTimeMillis();

      double dt = (t2 - t1) / 1000.0;

      // ----------------------------------------
      // Update Timer
      // ----------------------------------------

      // timer.handleDelta(dt);

      // ----------------------------------------
      // Update Sprites
      // ----------------------------------------

      updateAll(dt);
      repaint();

      t1 = t2;
    }
  }

  // --------------------------------------------------------------------------------

  private void updateAll(double dt) {
    synchronized (drawBoxList) {
      while (!drawBoxList.isEmpty()) {
        Box curBox = drawBoxList.remove(0);
        CollisionInfo col = null;

        double minTtc = Double.MAX_VALUE;

        for (Box tgtBox : drawBoxList) {
          CollisionInfo ci = new CollisionInfo();
          ci.box1 = curBox;
          ci.box2 = tgtBox;
          Box.timeToCollide(ci);
          double curTtc = ci.time;

          if (curTtc < minTtc) {
            minTtc = curTtc;
            col = ci;
          }
        }

        doneBoxList.add(curBox);

        System.err.println(dt + ";" + minTtc);
        if (minTtc < dt) {
          running = false;

          System.err.println("****************************************");
          System.err.println("Box1: " + (int) col.box1.cx + ";" + (int) col.box1.cy + ";" + //
              col.box1Side + ";" + col.box1.color + ";" + col.box1.id);
          System.err.println("Box2: " + (int) col.box2.cx + ";" + (int) col.box2.cy + ";" + //
              col.box2Side + ";" + col.box2.color + ";" + col.box2.id);

          dt = minTtc;
        }
      }

      for (Box box : doneBoxList) {
        box.updatePos(dt);
      }

      List<Box> aux = doneBoxList;
      doneBoxList = drawBoxList;
      drawBoxList = aux;
      //    running = false;
      System.err.println("bye updateAll");
    }
  }

  // --------------------------------------------------------------------------------
  // JPanel
  // --------------------------------------------------------------------------------

  @Override
  public void paint(Graphics g) {
    update(g);
  }

  // --------------------------------------------------------------------------------

  @Override
  public void update(Graphics g) {
    synchronized (drawBoxList) {
      Graphics2D g2d = (Graphics2D) g;

      g2d.setBackground(Color.WHITE);
      g2d.clearRect(0, 0, Hwh.getW(this), Hwh.getH(this));

      for (Box box : drawBoxList) {
        g2d.setColor(box.color);
        g2d.drawRect((int) box.cx, (int) box.cy, (int) box.bw, (int) box.bh);
      }
    }
  }
}