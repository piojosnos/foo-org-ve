package org.cyrano.pacman.base;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.cyrano.util.PointInt;

public class PathCalculator {

  private double/**/[][] dist;
  private char/*  */[][] data;

  private int w;
  private int h;

  private boolean allowDiagonals;

  // --------------------------------------------------------------------------------

  public PathCalculator(char[][] data, int w, int h, boolean allowDiagonals) {
    this.data = data;

    this.w = w;
    this.h = h;

    this.allowDiagonals = allowDiagonals;
  }

  // --------------------------------------------------------------------------------

  public List<PointInt> calculate(PointInt src, PointInt tgt) {
    dist = new double[w][h];

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        dist[x][y] = Double.MAX_VALUE;
      }
    }

    dist[src.x][src.y] = 0;

    List<PointInt> nextPoints = new LinkedList<PointInt>();
    nextPoints.add(src);

    while (!nextPoints.isEmpty()) {
      PointInt cp = nextPoints.remove(0);

      for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
          if (!allowDiagonals && dx != 0 && dy != 0) {
            continue;
          }

          if (dx == 0 && dy == 0) {
            continue;
          }

          if (cp.x + dx < 0 || cp.x + dx >= w || //
              cp.y + dy < 0 || cp.y + dy >= h) {
            continue;
          }

          if (!isEmptySpace(cp.x + dx, cp.y + dy)) {
            continue;
          }

          double dst = //
          dist[cp.x][cp.y] + Math.sqrt(dx * dx + dy * dy);

          if (dst < dist[cp.x + dx][cp.y + dy]) {
            dist[cp.x + dx][cp.y + dy] = dst;

            PointInt np = new PointInt();
            np.x = cp.x + dx;
            np.y = cp.y + dy;

            nextPoints.add(np);
          }
        }
      }
    }

    PointInt cp = tgt;

    List<PointInt> ret = new LinkedList<PointInt>();

    while (cp.x != src.x || cp.y != src.y) {
      ret.add(0, cp);

      PointInt mp = null;

      for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
          if (!allowDiagonals && dx != 0 && dy != 0) {
            continue;
          }

          if (dx == 0 && dy == 0) {
            continue;
          }

          if (cp.x + dx < 0 || cp.x + dx >= w || //
              cp.y + dy < 0 || cp.y + dy >= h) {
            continue;
          }

          if (!isEmptySpace(cp.x + dx, cp.y + dy)) {
            continue;
          }

          if (mp == null) {
            mp = new PointInt();
            mp.x = cp.x + dx;
            mp.y = cp.y + dy;
          } else {
            if (dist[cp.x + dx][cp.y + dy] < dist[mp.x][mp.y]) {
              mp.x = cp.x + dx;
              mp.y = cp.y + dy;
            }
          }
        }
      }

      cp = mp;
    }

    return ret;
  }

  // --------------------------------------------------------------------------------

  public boolean isEmptySpace(int x, int y) {
    return data[x][y] != 'X';
  }

  // --------------------------------------------------------------------------------

  public void dump(PrintStream ps) {
    NumberFormat nf = NumberFormat.getInstance();

    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    nf.setMaximumIntegerDigits(4);
    nf.setMinimumIntegerDigits(4);

    nf.setGroupingUsed(false);

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (dist[x][y] != Double.MAX_VALUE) {
          ps.print(nf.format(dist[x][y]));
        } else {
          ps.print("XXXX.XX");
        }

        ps.print(" | ");
      }

      ps.println();
    }
  }

  // --------------------------------------------------------------------------------

  public static void main(String[] args) throws Exception {
    LevelLoad textMap = new LevelLoad();
    textMap.load(PathCalculator.class.getResource("map.txt").getPath());
    //textMap.dump(System.err);

    char[][] data = textMap.getData();

    PathCalculator pathCalculator = new PathCalculator( //
        data, textMap.getW(), textMap.getH(), false);

    PointInt src = new PointInt();
    src.x = 1;
    src.y = 1;

    PointInt tgt = new PointInt();
    tgt.x = 9;
    tgt.y = 11;

    List<PointInt> path = pathCalculator.calculate(src, tgt);
    pathCalculator.dump(System.err);

    System.err.println("--------------------------------------------------------------------------------");

    for (PointInt intPoint : path) {
      System.err.println(intPoint.x + ";" + intPoint.y);
      data[intPoint.x][intPoint.y] = '#';
    }

    System.err.println("--------------------------------------------------------------------------------");

    //textMap.dump(System.err);
  }
}