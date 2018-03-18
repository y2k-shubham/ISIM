/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParticleSimulator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Arnav
 */
class Body {

    public Color col;
    private String id;
    private double m;

    private double x;
    private double y;

    private double dx;
    private double dy;

    private double vx;
    private double vy;

    private boolean hasCollided;

    public Body(String id, double m) {
        this.id = id;
        this.m = m;
        this.hasCollided = false;
        this.dx = 0;
        this.dy = 0;
        Random r = new Random();
        this.col = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public Body(String id, double m, double x, double y, double vx, double vy) {
        this(id, m);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public String getId() {
        return id;
    }

    public double getM() {
        return m;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setM(double m) {
        this.m = m;
    }

    public void setCollided(boolean hasCollided) {
        this.hasCollided = hasCollided;
    }

    public void setDisp(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void updPos(double xMin, double xMax, double yMin, double yMax, double Cr) {
//        System.out.println("\nxi = " + x + "\tyi = " + y);
//        System.out.println("dx = " + dx + "\tdy = " + dy);
        x += dx;
        y += dy;
//      System.out.println("xf = " + x + "\tyf = " + y);

        double xTemp = x;
        double yTemp = y;

        x = Math.max(xMin, x);
        x = Math.min(xMax, x);

        if (x == xMin || x == xMax) {
//            System.out.println("x = " + xTemp + "(xMin, xMax) = (" + xMin + ", " + xMax + ")");
//          System.out.println("xMin = " + xMin + "\txMax = " + xMax + "\tx = " + x);
//            System.out.println("body " + id + " collided with v-wall (x)");
            dx = 0.0;
            vx = (vx > 0 && x == xMax) ? (Cr * -vx) : (vx < 0 && x == xMin) ? (Cr * -vx) : vx;
        }

        y = Math.max(yMin, y);
        y = Math.min(yMax, y);

        if (y == yMin || y == yMax) {
//            System.out.println("y = " + yTemp + "(yMin, yMax) = (" + yMin + ", " + yMax + ")");
//          System.out.println("yMin = " + yMin + "\tyMax = " + yMax + "\ty = " + y);
//            System.out.println("body " + id + " collided with h-wall (y)");
            dy = 0.0;
            vy = (vy > 0 && y == yMax) ? (Cr * -vy) : (vy < 0 && y == yMin) ? (Cr * -vy) : vy;
        }
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setVel(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

}

public class CollisionSimulatorCore {

    public final ArrayList<Body> bodies;

    private double t;
    private double Cr;

    private double rThresh;
    private double rThreshSqr;

    public double xMin, xMax;
    public double yMin, yMax;

    public CollisionSimulatorCore() {
        this.bodies = new ArrayList<>();
    }

    public void setT(double t) {
        this.t = t;
    }

    public void setrThresh(double rThresh) {
        this.rThresh = rThresh;
        this.rThreshSqr = Math.pow(rThresh, 2.0);
    }

    public void setCr(double Cr) {
        this.Cr = Cr;
    }

    public void setXLim(double xMin, double xMax) {
        this.xMin = xMin;
        this.xMax = xMax;
    }

    public void setYLim(double yMin, double yMax) {
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void createBody(short id, double m, double x, double y, double vx, double vy) {
        bodies.add(new Body(Short.toString(id), m, x, y, vx, vy));
    }

    private void findNewVel(double m1, double m2, double uDiff, double numConst, double denConst, double[] v) {
        v[0] = (Cr * m2 * (-uDiff) + numConst) / denConst;
        v[1] = (Cr * m1 * uDiff + numConst) / denConst;
    }

    private void clearCollisionFlags() {
        // clear collided flags
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).setCollided(false);
        }
    }

    private void collideB2W() {
        for (Body b : bodies) {
            boolean colTop = false;
            boolean colLeft = false;

            // check collision with left wall
            if (Math.abs(b.getX() - xMin) <= rThresh) {
                b.setVx(-b.getVx() * Cr);
                b.setCollided(true);
                colLeft = true;
//                System.out.println("Collision b/w wall and body " + b.getId() + " detected");
            }

            // check collision with right wall
            if (!colLeft && Math.abs(b.getX() - xMax) <= rThresh) {
                b.setVx(-b.getVx() * Cr);
                b.setCollided(true);
//                System.out.println("Collision b/w wall and body " + b.getId() + " detected");
            }

            // check collision with top wall
            if (Math.abs(b.getY() - yMin) <= rThresh) {
                b.setVy(-b.getVy() * Cr);
                b.setCollided(true);
                colTop = true;
//                System.out.println("Collision b/w wall and body " + b.getId() + " detected");
            }

            // check collision with bottom wall
            if (!colTop && Math.abs(b.getY() - yMax) <= rThresh) {
                b.setVy(-b.getVy() * Cr);
                b.setCollided(true);
//                System.out.println("Collision b/w wall and body " + b.getId() + " detected");
            }
        }
    }

    private void collideB2B() {
        // check collisions between all pairs of bodies and update velocities of collided bodies
        for (int i = 0; i < bodies.size(); i++) {
            Body b1 = bodies.get(i);

            if (!b1.hasCollided()) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    Body b2 = bodies.get(j);

                    // check collision
                    if (!b2.hasCollided() && Math.sqrt(Math.pow(b1.getX() - b2.getX(), 2.0) + Math.pow(b1.getY() - b2.getY(), 2.0)) <= rThresh) {
//                        System.out.println("\nCollision b/w bodies " + b1.getId() + " and " + b2.getId() + " detected");
//                        System.out.println("b1x = " + b1.getX() + "\tb2x = " + b2.getX() + "\tdx = " + (b1.getX() - b2.getX()) + "\tdx2 = " + Math.pow(b1.getX() - b2.getX(), 2.0));
//                        System.out.println("b1y = " + b1.getY() + "\tb2y = " + b2.getY() + "\tdy = " + (b1.getY() - b2.getY()) + "\tdy2 = " + Math.pow(b1.getY() - b2.getY(), 2.0));
//                        System.out.println("dx2 + dy2 = " + (Math.pow(b1.getX() - b2.getX(), 2.0) + Math.pow(b1.getY() - b2.getY(), 2.0)));
//                        System.out.println("sqrt(dx2 + dy2) = " + Math.sqrt((Math.pow(b1.getX() - b2.getX(), 2.0) + Math.pow(b1.getY() - b2.getY(), 2.0))));
//                        System.out.println("rThresh = " + rThresh);

                        // read parameters of bodies
                        double m1 = b1.getM();
                        double m2 = b2.getM();

                        double u1x = b1.getVx();
                        double u1y = b1.getVy();

                        double u2x = b2.getVx();
                        double u2y = b2.getVy();

                        double denConst = m1 + m2;

                        // set collided flags to prevent further checking
                        if (Cr > 0) {
                            // inelastic collision
                            b1.setCollided(true);
                            b2.setCollided(true);

                            double[] v = new double[2];

                            // find and update velocities
                            findNewVel(m1, m2, (u1x - u2x), (m1 * u1x + m2 * u2x), denConst, v);
                            b1.setVx(v[0]);
                            b2.setVx(v[1]);

                            findNewVel(m1, m2, (u1y - u2y), (m1 * u1y + m2 * u2y), denConst, v);
                            b1.setVy(v[0]);
                            b2.setVy(v[1]);
                        } else {
                            // perfectly inelastic collision
                            b1.setCollided(true);
                            bodies.remove(b2);

                            b1.setId(b1.getId() + "," + b2.getId());
                            b1.setM(m1 + m2);

                            b1.setPos((b1.getX() + b2.getX()) / 2.0, (b1.getY() + b2.getY()) / 2.0);

                            b1.setVx((m1 * u1x + m2 * u2x) / denConst);
                            b1.setVy((m1 * u1y + m2 * u2y) / denConst);
                        }
                    }
                }
            }
        }
    }

    private void moveBodies() {
        for (Body b : bodies) {
            b.setDisp(b.getVx() * t, b.getVy() * t);
            b.updPos(xMin, xMax, yMin, yMax, Cr);
        }
    }

    public void simulateStep() {
//        collideB2W();
        collideB2B();
        moveBodies();
        clearCollisionFlags();
    }

    public void showBodies() {
        System.out.println("New states of bodies are:-");
        for (Body b : bodies) {
            System.out.println("\nBody " + b.getId());
            System.out.println("Mass\t\t:\t" + b.getM());
            System.out.println("(vx, vy)\t:\t(" + b.getVx() + "\t" + b.getVy() + ")");
            System.out.println("(dx, dy)\t:\t(" + b.getDx() + "\t" + b.getDy() + ")");
            System.out.println("(x, y)\t\t:\t(" + b.getX() + "\t" + b.getY() + ")");
        }
    }

    public static void main(String[] args) {
        CollisionSimulatorCore csc = new CollisionSimulatorCore();
        Scanner in = new Scanner(System.in);

        System.out.println("Enter boundary coordinates:-");

        System.out.print("delta-T:\t");
        csc.setT(in.nextDouble());

        System.out.print("Cr\t:\t");
        csc.setCr(in.nextDouble());

        System.out.print("r-Thresh:\t");
        csc.setrThresh(in.nextDouble());

        System.out.print("\nxMin, xMax:\t");
        double xMin = in.nextDouble();
        double xMax = in.nextDouble();
        csc.setXLim(xMin, xMax);

        System.out.print("yMin, yMax:\t");
        double yMin = in.nextDouble();
        double yMax = in.nextDouble();
        csc.setYLim(yMin, yMax);

        System.out.print("\nEnter no of bodies:\t");
        short n = in.nextShort();

        for (short i = 0; i < n; i++) {
            System.out.println("\nBody " + i);

            System.out.print("Mass:\t");
            double m = in.nextDouble();

            System.out.print("x, y:\t");
            double x = in.nextDouble();
            double y = in.nextDouble();

            System.out.print("Vx, Vy:\t");
            double vx = in.nextDouble();
            double vy = in.nextDouble();

            csc.createBody(i, m, x, y, vx, vy);
        }

        in.nextLine();
        System.out.println("\n===============================");
        csc.showBodies();
        while (true) {
            System.out.println("\n===============================");
            System.out.println("Hit Enter to simulate a step");

            in.nextLine();

            csc.simulateStep();
            csc.showBodies();
        }
    }

}

/*
Input 1
0.001
1.0
0.05

-0  1
-10 10

2

5
0   0
10  0

10
1   0
-5  0

===========================

Input 2
0.001
1.0
0.05

-0  1
-10 10

2

4
0   0
10  0

12
1   0
-5  0

===========================

Input 3
0.001
0
0.05

-0  1
-10 10

2

4
0   0
10  0

12
1   0
-5  0

===========================

Input 4

0.001
1
0.05

-100 100
-100 100

2

2
0 0
900 1200

1
14 0
-500 1200

 */
