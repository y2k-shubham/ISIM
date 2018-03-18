/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParticleSimulator;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Arnav
 */
class Particle {

    // final instrinsic properties
    private final short id;
    private final double m;
    private final double q;
    public Color col;
    boolean collided;

    // final x-y limits, if any
    private final boolean xBound;
    private double xLo, xHi;
    private final boolean yBound;
    private double yLo, yHi;

    // position
    private double x;
    private double y;

    // displacement
    private double sx;
    private double sy;

    // velocity
    private double vx;
    private double vy;

    // acceleration
    private double ax;
    private double ay;

    private MotionSimulatorCore msc;

    public Particle(MotionSimulatorCore msc, short id, double m, double q, boolean xBound, boolean yBound) {
        this.msc = msc;
        this.id = id;
        this.m = m;
        this.q = q;
        this.xBound = xBound;
        this.yBound = yBound;
        xLo = Double.MIN_VALUE;
        xHi = Double.MAX_VALUE;
        yLo = Double.MIN_VALUE;
        yHi = Double.MAX_VALUE;
        collided = false;
        Random r = new Random();
        //this.col = new Color(((int) (r.nextDouble() * 170)), ((int) (r.nextDouble() * 170)), ((int) (r.nextDouble() * 170)));
        this.col = (q > 0) ? (new Color(1.0f, 0.0f, 0.0f)) : ((q < 0) ? (new Color(0.0f, 0.0f, 1.0f)) : (new Color(0.0f, 1.0f, 0.0f)));
    }

    public Particle(MotionSimulatorCore msc, short id, double m, double q, double x, double y, double vx, double vy, boolean xBound, boolean yBound) {
        this(msc, id, m, q, xBound, yBound);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public short getId() {
        return id;
    }

    public double getM() {
        return m;
    }

    public double getQ() {
        return q;
    }

    public boolean isXBound() {
        return xBound;
    }

    public boolean isYBound() {
        return yBound;
    }

    public double getxLo() {
        return xLo;
    }

    public double getxHi() {
        return xHi;
    }

    public double getyLo() {
        return yLo;
    }

    public double getyHi() {
        return yHi;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }

    public double getSx() {
        return sx;
    }

    public double getSy() {
        return sy;
    }

    public void setXBound(double xLo, double xHi) {
        if (xBound) {
            this.xLo = xLo;
            this.xHi = xHi;
        }
    }

    public void setYBound(double yLo, double yHi) {
        if (yBound) {
            this.yLo = yLo;
            this.yHi = yHi;
        }
    }

    public void updPos() {
        double newX = this.x + this.sx;
        double newY = this.y + this.sy;

        double tmpX = newX;
        newX = Math.max(msc.xLo, newX);
        newX = Math.min(msc.xHi, newX);
        if (newX == msc.xLo || newX == msc.xHi) {
            this.sx = 0;
            this.vx = -this.vx;
            collided = true;
            //this.ax = (2 * vx) / msc.t;
        }

        double tmpY = newY;
        newY = Math.max(msc.yLo, newY);
        newY = Math.min(msc.yHi, newY);
        if (newY == msc.yLo || newY == msc.yHi) {
            this.sy = 0;
            this.vy = -this.vy;
            collided = true;
            //this.ay = (2 * vy) / msc.t;
        }

        if (collided) {
            return;
        }

        if (xBound) {
            if (xLo <= newX && newX <= xHi) {
                this.x = newX;
            } else {
                this.sx = 0;
                this.vx = 0;
                this.ax = 0;
            }
        } else if (!xBound) {
            this.x += this.sx;
        }

        if (yBound) {
            if (yLo <= newY && newY <= yHi) {
                this.y = newY;
            } else {
                this.sy = 0;
                this.vy = 0;
                this.ay = 0;
            }
        } else if (!yBound) {
            this.y += this.sy;
        }
    }

    public void setVel(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void setDisp(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }

    public void setAcc(double ax, double ay) {
        this.ax = ax;
        this.ay = ay;
    }

}

public class MotionSimulatorCore {

    // constants
    private final static double KG = 6.67E-11f;
    private final static double KE = 9E9f;

    // time-duration
    double t;
    private final static int MAX_PARTICLES = 100;

    // list of particles
    public final ArrayList<Particle> particles;

    // matrix to hold distance between particles
    public final Point2D.Double[][] d;
    private final double[][] r;
    private final double[][] rSqr;

    // matrix to hold accelerations due to gravity
    public final Point2D.Double[][] fG;

    // matrix to hold accelerations due to electrostatic force
    public final Point2D.Double[][] fE;

    // for limiting view and rebounding particles when they hit the edge of window
    double xLo, xHi, yLo, yHi;

    // for performing collision between particles
    double rThresh;
    double rThresh_Sqr;
    double mDiff[][], mSum[][];

    public MotionSimulatorCore() {
        particles = new ArrayList<>();
        r = new double[MAX_PARTICLES][MAX_PARTICLES];
        rSqr = new double[MAX_PARTICLES][MAX_PARTICLES];
        d = new Point2D.Double[MAX_PARTICLES][MAX_PARTICLES];
        fG = new Point2D.Double[MAX_PARTICLES][MAX_PARTICLES];
        fE = new Point2D.Double[MAX_PARTICLES][MAX_PARTICLES];
        mDiff = new double[MAX_PARTICLES][MAX_PARTICLES];
        mSum = new double[MAX_PARTICLES][MAX_PARTICLES];
    }

    // creation and initialization
    public void input() {
        Scanner in = new Scanner(System.in);

        double t;
        int n;

        System.out.print("Enter time-quantum:\t");
        t = in.nextDouble();

        System.out.print("Enter no of particles:\t");
        n = in.nextInt();

        setT(t);
        initMat(fG, n);
        initMat(fE, n);
        initMat(d, n);

        System.out.println("Enter details of particles one-by-one:-\n");
        for (int i = 0; i < n; i++) {
            // other parameters
            short id;
            double m, q;
            double x, y;
            double vx, vy;
            // for bounds
            boolean isXBound = false, isYBound = false;
            char xb, yb;
            double xLo = 0, xHi = 0;
            double yLo = 0, yHi = 0;
            String tmp;

            System.out.println("\nParticle " + i);
            id = (short) i;

            System.out.print("Mass, Charge:\t");
            m = in.nextDouble();
            q = in.nextDouble();

            System.out.print("X, Y Coords:\t");
            x = in.nextDouble();
            y = in.nextDouble();

            System.out.print("X, Y Velocity:\t");
            vx = in.nextDouble();
            vy = in.nextDouble();
            in.nextLine();

            System.out.print("X-Bound?\t");
            xb = in.nextLine().charAt(0);

            if (xb == 'Y' || xb == 'y' || xb == 'T' || xb == 't') {
                System.out.print("X-Lo:\t\t");
                tmp = in.nextLine();
                if (!tmp.isEmpty()) {
                    xLo = Double.parseDouble(tmp);
                } else {
                    xLo = Double.MIN_VALUE;
                }

                System.out.print("X-Hi:\t\t");
                tmp = in.nextLine();
                if (!tmp.isEmpty()) {
                    xHi = Double.parseDouble(tmp);
                } else {
                    xHi = Double.MAX_VALUE;
                }

                isXBound = true;
            }

            System.out.print("Y-bound?:\t");
            yb = in.nextLine().charAt(0);
            if (yb == 'Y' || yb == 'y' || yb == 'T' || yb == 't') {
                System.out.print("Y-Lo:\t\t");
                tmp = in.nextLine();
                if (!tmp.isEmpty()) {
                    yLo = Double.parseDouble(tmp);
                } else {
                    yLo = Double.MIN_VALUE;
                }

                System.out.print("Y-Hi:\t\t");
                tmp = in.nextLine();
                if (!tmp.isEmpty()) {
                    yHi = Double.parseDouble(tmp);
                } else {
                    yHi = Double.MAX_VALUE;
                }

                isYBound = true;
            }

            createParticle(id, m, q, x, y, vx, vy, isXBound, xLo, xHi, isYBound, yLo, yHi);
        }

        System.out.println("\nEnter display bounds:-");

        System.out.print("(xLo, xHi):\t");
        xLo = in.nextDouble();
        xHi = in.nextDouble();

        System.out.print("(yLo, yHi):\t");
        yLo = in.nextDouble();
        yHi = in.nextDouble();

        System.out.print("\nEnter r-Thresh:\t");
        rThresh = in.nextDouble();
        rThresh_Sqr = Math.pow(rThresh, 2.0);

        initMat();
    }

    public void inputRandom() {
        Random r = new Random();

        double t;
        int n;

        System.out.print("Enter time-quantum:\t");
        t = 0.001;

        System.out.print("Enter no of particles:\t");
        n = 5;

        setT(t);
        initMat(fG, n);
        initMat(fE, n);
        initMat(d, n);

        System.out.println("Enter details of particles one-by-one:-\n");
        for (int i = 0; i < n; i++) {
            // other parameters
            short id;
            double m, q;
            double x, y;
            double vx, vy;
            // for bounds
            boolean isXBound = false, isYBound = false;
            char xb, yb;
            double xLo = 0, xHi = 0;
            double yLo = 0, yHi = 0;
            String tmp;

            System.out.println("\nParticle " + i);
            id = (short) i;

            System.out.print("Mass, Charge:\t");
            m = r.nextInt(3000);
            q = (r.nextDouble() * 2 - 1) / 10;

            System.out.print("X, Y Coords:\t");
            x = r.nextInt(80) - 40;
            y = r.nextInt(45) - 22.5;

            System.out.print("X, Y Velocity:\t");
            vx = r.nextInt(40) - 20;
            vy = r.nextInt(22) - 11.25;

            createParticle(id, m, q, x, y, vx, vy, isXBound, xLo, xHi, isYBound, yLo, yHi);
        }

        System.out.println("\nEnter display bounds:-");

        System.out.print("(xLo, xHi):\t");
        xLo = -41;
        xHi = 41;

        System.out.print("(yLo, yHi):\t");
        yLo = -23;
        yHi = 23;

        System.out.print("\nEnter r-Thresh:\t");
        rThresh = 0.1;
        rThresh_Sqr = Math.pow(rThresh, 2.0);

        initMat();
    }

    public void setT(double t) {
        this.t = t;
    }

    public void createParticle(short id, double m, double q, double x, double y, double vx, double vy, boolean isXBound, double xLo, double xHi, boolean isYBound, double yLo, double yHi) {
        Particle p;
        particles.add(p = new Particle(this, id, m, q, x, y, vx, vy, isXBound, isYBound));

        if (isXBound) {
            p.setXBound(xLo, xHi);
        }

        if (isYBound) {
            p.setYBound(yLo, yHi);
        }
    }

    public void initMat(Point2D.Double[][] mat, int dim) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                mat[i][j] = new Point2D.Double();
            }
        }
    }

    public void initMat() {
        for (int i = 0; i < particles.size(); i++) {
            for (int j = 0; j < particles.size(); j++) {
                mDiff[i][j] = particles.get(i).getM() - particles.get(j).getM();
                mSum[i][j] = particles.get(i).getM() + particles.get(j).getM();
            }
        }
    }

    // calculations
    private void clearMat(Point2D.Double[][] mat, int dim) {
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                mat[i][j].x = 0.0f;
                mat[i][j].y = 0.0f;
            }
        }
    }

    private void calcDist() {
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                // calculate difference of x and y coordinates
                double dx = particles.get(j).getX() - particles.get(i).getX();
                double dy = particles.get(j).getY() - particles.get(i).getY();

                // store difference of x and y coordinates for this particle
                d[i][j].x = dx;
                d[i][j].y = dy;

                // store difference of x and y coordinates for other particle
                d[j][i].x = -dx;
                d[j][i].y = -dy;

                // calculate euclidean distance
                double rSqr = (double) (Math.pow(dx, 2.0) + Math.pow(dy, 2.0));
                double r = (double) Math.sqrt(rSqr);

                // store euclidean distance
                this.rSqr[i][j] = this.rSqr[j][i] = rSqr;
                this.r[i][j] = this.r[j][i] = r;
            }
        }
    }

    private void calcGForce() {
        clearMat(fG, particles.size());

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                // calculate G-force
                double fg = KG * particles.get(i).getM() * particles.get(j).getM() / rSqr[i][j];

                // store G-force on this particle
                fG[i][j].x = fg * d[i][j].x / r[i][j];
                fG[i][j].y = fg * d[i][j].y / r[i][j];

                // store G-force on other particle
                fG[j][i].x = -fG[i][j].x;
                fG[j][i].y = -fG[i][j].y;
            }
        }
    }

    private void calcEForce() {
        clearMat(fE, particles.size());

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                // calculate E-force
                double fe = KE * particles.get(i).getQ() * particles.get(j).getQ() / rSqr[i][j];
                fe = -fe;

                // store E-force on this particle
                fE[i][j].x = fe * d[i][j].x / r[i][j];
                fE[i][j].y = fe * d[i][j].y / r[i][j];

                // store E-force on other particle
                fE[j][i].x = -fE[i][j].x;
                fE[j][i].y = -fE[i][j].y;
            }
        }
    }

    // movement and invocation of methods
    private void moveParts() {
        double halfTSqr = 0.5 * Math.pow(t, 2.0);

        for (int i = 0; i < particles.size(); i++) {
            // calculate net force on particle
            double fx = 0.0;
            double fy = 0.0;
            for (int j = 0; j < particles.size(); j++) {
                fx += fG[i][j].x + fE[i][j].x;
                fy += fG[i][j].y + fE[i][j].y;
            }

            // calculate and store net acceleration of particle
            double ax = fx / particles.get(i).getM();
            double ay = fy / particles.get(i).getM();
            particles.get(i).setAcc(ax, ay);

            // calculate and store displacement of particle
            double sx = particles.get(i).getVx() * t + halfTSqr * ax;
            double sy = particles.get(i).getVy() * t + halfTSqr * ay;
            particles.get(i).setDisp(sx, sy);

            // calculate and store velocity of particle
            double vx = particles.get(i).getVx() + ax * t;
            double vy = particles.get(i).getVy() + ay * t;
            particles.get(i).setVel(vx, vy);

            // calculate and store new position of particle
            particles.get(i).updPos();
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < particles.size(); i++) {
            Particle p1 = particles.get(i);

            if (!p1.collided) {
                for (int j = 0; j < particles.size(); j++) {

                    Particle p2 = particles.get(j);
                    if (!p2.collided && i != j) {
                        if (rSqr[i][j] <= rThresh_Sqr) {
                            p1.collided = p2.collided = true;

                            double m1 = p1.getM();
                            double m2 = p2.getM();

                            double u1x = p1.getVx();
                            double u1y = p1.getVy();

                            double u2x = p2.getVx();
                            double u2y = p2.getVy();

                            p1.setVel((u1x * mDiff[i][j] + 2 * m2 * u2x) / (mSum[i][j]), (u1y * mDiff[i][j] + 2 * m2 * u2y) / (mSum[i][j]));
                            p2.setVel((u2x * -mDiff[i][j] + 2 * m1 * u1x) / (mSum[i][j]), (u2y * -mDiff[i][j] + 2 * m1 * u1y) / (mSum[i][j]));
                        }
                    }
                }
            }
        }
    }

    private void clearCollisionFlags() {
        for (Particle p : particles) {
            p.collided = false;
        }
    }

    public void simulateStep() {
        calcDist();
        calcGForce();
        calcEForce();
        moveParts();
        checkCollisions();
        clearCollisionFlags();
    }

    // output
    public void showParts() {
        System.out.println("\nNew states of particles are:-");
        for (int i = 0; i < particles.size(); i++) {
            System.out.println("\nParticle " + particles.get(i).getId());
            System.out.println("(Mass, Charge)\t:\t(" + particles.get(i).getM() + ", " + particles.get(i).getQ() + ")");

            if (particles.get(i).isXBound()) {
                System.out.println("(xLo, xHi)\t:\t(" + particles.get(i).getxLo() + ", " + particles.get(i).getxHi() + ")");
            }
            if (particles.get(i).isYBound()) {
                System.out.println("(yLo, yHi)\t:\t(" + particles.get(i).getyLo() + ", " + particles.get(i).getyHi() + ")");
            }

            System.out.println("(Ax, Ay)\t:\t(" + particles.get(i).getAx() + ", " + particles.get(i).getAy() + ")");
            System.out.println("(Vx, Vy)\t:\t(" + particles.get(i).getVx() + ", " + particles.get(i).getVy() + ")");
            System.out.println("(Sx, Sy)\t:\t(" + particles.get(i).getSx() + ", " + particles.get(i).getSy() + ")");
            System.out.println("(x, y)\t\t:\t(" + particles.get(i).getX() + ", " + particles.get(i).getY() + ")");
        }
    }

    private void showDists(Point2D.Double[][] mat, String name) {
        System.out.println("\n" + name + " matrix is:-");
        for (int i = 0; i < particles.size(); i++) {
            for (int j = 0; j < particles.size(); j++) {
                System.out.print("(" + mat[i][j].x + ", " + mat[i][j].y + ")\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void showDists(double[][] mat, String name) {
        System.out.println("\n" + name + " matrix is:-");
        for (int i = 0; i < particles.size(); i++) {
            for (int j = 0; j < particles.size(); j++) {
                System.out.print(mat[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    // main method
    public static void main(String[] args) {
        MotionSimulatorCore sC = new MotionSimulatorCore();
        Scanner in = new Scanner(System.in);

        sC.input();

        in.nextLine();
        System.out.println("\n===============================");
//        sC.showDists(sC.d, "d[]");
//        sC.showDists(sC.r, "r[]");
//        sC.showDists(sC.rSqr, "rSqr[]");
//        sC.showDists(sC.fG, "fG[]");
//        sC.showDists(sC.fE, "fE[]");
        sC.showParts();
        while (true) {
            System.out.println("\n===============================");
            System.out.println("Hit Enter to simulate a step");

            in.nextLine();
            sC.simulateStep();

//            sC.showDists(sC.d, "d[]");
//            sC.showDists(sC.r, "r[]");
//            sC.showDists(sC.rSqr, "rSqr[]");
//            sC.showDists(sC.fG, "fG[]");
//            sC.showDists(sC.fE, "fE[]");
            sC.showParts();
        }
    }

}

/*
0.1 2

1000 0.001
2 5
0 0

500 -0.01
12 5
0 0

=================

0.001 3

100000000 -1
-1 0
0 -8.215838363
F
F

100000000 1
0 0
0 0
F
F

100000000 -1
1 0
0 8.215838363
F
F

================

0.001 3

10000 1
-10 0
0 0
T T
-10 -10
0 0

10000 1
5 0
0 0
F F

10000 1
10 0
0 0
T T
10 10
0 0

=================

0.001 4

10000 1
-3 0
0 0
T
-3

f


10000 1
0 3
0 0
F
T

3


10000 1
0 -3
0 0
F
T
-3


10000 1
3 0
0 0
F
F


===================

0.001 4

10000 1
-3 0
0 0
T
-3

f


10000 1
0 3
0 0
F
T

3


10000 1
0 -3
0 0
F
T
-3


10000 1
2.5 0
0 0
F
F


 */
