/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParticleSimulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author Arnav
 */
public class CollisionSimulatorGUI implements ActionListener {

    private class DrawPanel extends JPanel {

        CollisionSimulatorGUI msg;
        double xMul, xOff;
        double yMul, yOff;
        int DIMX, DIMY;
        int xLo, xHi, yLo, yHi;

        public DrawPanel(CollisionSimulatorGUI msg) {
            this.msg = msg;
        }

        public void initVals() {
            xMul = (getWidth() / 2) / (msg.xHi - msg.xLo);
            yMul = (getHeight() / 2) / (msg.yHi - msg.yLo);
            xOff = getWidth() / 2;
            yOff = getHeight() / 2;
            DIMX = DIMY = 20;
            
            xLo = (int) (msg.xLo * xMul + xOff - DIMX / 2);
            xHi = (int) (msg.xHi * xMul + xOff);
            yLo = (int) (msg.yLo * yMul + yOff - DIMY / 2);
            yHi = (int) (msg.yHi * yMul + yOff);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.

            g.setColor(Color.black);
            g.drawLine(xLo, yLo, xLo, yHi);
            g.drawLine(xLo, yLo, xHi, yLo);
            g.drawLine(xLo, yHi, xHi, yHi);
            g.drawLine(xHi, yLo, xHi, yHi);

            ArrayList<Body> bList = msg.csc.bodies;
            for (Body b : bList) {
                g.setColor(b.col);
                int x = (int) (b.getX() * xMul + xOff) - (DIMX / 2);
                int y = (int) (b.getY() * yMul + yOff) - (DIMY / 2);
                g.fillOval(x, y, DIMX / 2, DIMY / 2);
            }
        }

    }

    JFrame jfFrame;
    DrawPanel jpDraw;
    JSlider jsSpeed;
    JLabel jlSpeed;
    JButton jbPlayPause;

    CollisionSimulatorCore csc;
    double xLo, xHi, yLo, yHi;
    int sleepTime;
    volatile boolean run;

    public CollisionSimulatorGUI() {
        jfFrame = new JFrame();
        jpDraw = new DrawPanel(this);
        jbPlayPause = new JButton("Play");
        csc = new CollisionSimulatorCore();
        run = false;
    }

    void input() {
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

        System.out.println("\nEnter display bounds:-");

        System.out.print("(xLo, xHi):\t");
        xLo = in.nextDouble();
        xHi = in.nextDouble();

        System.out.print("(yLo, yHi):\t");
        yLo = in.nextDouble();
        yHi = in.nextDouble();
        
        System.out.print("\nEnter sleep duration:\t");
        sleepTime = in.nextInt();
    }

    void inputRandom() {
        Random r = new Random();

        System.out.println("Enter boundary coordinates:-");

        System.out.print("delta-T:\t");
        csc.setT(0.001);

        System.out.print("Cr\t:\t");
        csc.setCr(Math.min(r.nextDouble() + 0.4, 1.0));
//        csc.setCr(0.1);

        System.out.print("r-Thresh:\t");
        csc.setrThresh(0.2);

        System.out.print("\nxMin, xMax:\t");
        double xMin = -8;
        double xMax = 8;
        csc.setXLim(xMin, xMax);

        System.out.print("yMin, yMax:\t");
        double yMin = -4.5;
        double yMax = 4.5;
        csc.setYLim(yMin, yMax);

        System.out.print("\nEnter no of bodies:\t");
        short n = 40;

        for (short i = 0; i < n; i++) {
            System.out.println("\nBody " + i);

            System.out.print("Mass:\t");
            double m = r.nextInt(490) + 10;

            System.out.print("x, y:\t");
            double x = (r.nextDouble() * 16) - 8;
            double y = (r.nextDouble() * 9) - 4.5;

            System.out.print("Vx, Vy:\t");
            double vx = r.nextDouble() * 100;
            double vy = r.nextDouble() * 100;

            csc.createBody(i, m, x, y, vx, vy);
        }

        System.out.println("\nEnter display bounds:-");

        System.out.print("(xLo, xHi):\t");
        xLo = -8;
        xHi = 8;

        System.out.print("(yLo, yHi):\t");
        yLo = -4.5;
        yHi = 4.5;
        
        System.out.print("\nEnter sleep duration:\t");
        sleepTime = 2;
    }
    
    void initFrame() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight() - 50;
        JPanel jp = new JPanel(new BorderLayout());

        jp.add(jpDraw, BorderLayout.CENTER);
        jp.add(jbPlayPause, BorderLayout.SOUTH);

        jfFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfFrame.setResizable(true);
        jfFrame.getContentPane().add(jp);
        jfFrame.pack();
        jfFrame.setBounds(0, 0, width, height);
    }

    void initPanel() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight() - 50;
        jpDraw.setBounds(0, 0, width - 6, height - 6);
    }

    void showFrame() {
        jfFrame.setVisible(true);
    }

    void addListeners() {
        jbPlayPause.addActionListener(this);
    }

    void simulate() {
        jpDraw.repaint();
        run = true;

        while (run) {
            csc.simulateStep();
            jpDraw.repaint();
            //csc.showBodies();

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Cautght Exception:\t" + e);
            }
        }
    }

    public static void main(String[] args) {
        CollisionSimulatorGUI csg = new CollisionSimulatorGUI();

//        csg.input();
        csg.inputRandom();
        csg.initPanel();
        csg.initFrame();
        csg.addListeners();
        csg.showFrame();
        csg.jpDraw.initVals();
        
//        System.out.println("(xMin, xMax) = (" + csg.csc.xMin + ", " + csg.csc.xMax + ")");
//        System.out.println("(yMin, yMax) = (" + csg.csc.yMin + ", " + csg.csc.yMax + ")");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        csg.simulate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (jbPlayPause.getText().equals("Play")) {
            jbPlayPause.setText("Pause");
            simulate();
        } else {
            jbPlayPause.setText("Play");
            run = false;
        }
    }

}
