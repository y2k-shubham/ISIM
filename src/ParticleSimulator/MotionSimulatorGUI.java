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
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;

/**
 *
 * @author Arnav
 */
public class MotionSimulatorGUI {

    private class DrawPanel extends JPanel {

        MotionSimulatorGUI msg;
        double xMul, xOff;
        double yMul, yOff;
        int RESX, RESY;
        int DIMX, DIMY;
        int DIMX_Hlf, DIMY_Hlf;

        public DrawPanel(MotionSimulatorGUI msg) {
            this.msg = msg;
        }

        void initVals() {
            RESX = getWidth();
            RESY = getHeight();
//            xMul = (RESX / 2) / (msg.msc.xHi - msg.msc.xLo);
//            yMul = (RESY / 2) / (msg.msc.yHi - msg.msc.yLo);
            xOff = RESX / 2;
            yOff = RESY / 2;
            xMul = (RESX) / (msg.msc.xHi - msg.msc.xLo);
            yMul = (RESY) / (msg.msc.yHi - msg.msc.yLo);
            DIMX = 20;
            DIMY = 20;
            DIMX_Hlf = DIMX / 2;
            DIMY_Hlf = DIMY / 2;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.

            ArrayList<Particle> pList = msg.msc.particles;
            for (Particle p : pList) {
                g.setColor(p.col);

                int x = (int) (p.getX() * xMul + xOff) - DIMX_Hlf;
                int y = (int) (p.getY() * yMul + yOff) - DIMY_Hlf;
                g.fillOval(x, y, DIMX, DIMY);

                if (p.isXBound()) {
                    int xLim;

                    xLim = (int) ((p.getxLo() * xMul) + xOff);
                    if (xLim > Integer.MIN_VALUE) {
//                        System.out.println("p" + p.getId() + " drawing x-Lines (" + xLim + ", 0) (" + xLim + ", " + RESY + ")");
                        g.drawLine(xLim, 0, xLim, RESY);
                    }

                    xLim = (int) ((p.getxHi() * xMul) + xOff);
                    if (xLim < Integer.MAX_VALUE) {
//                        System.out.println("p" + p.getId() + " drawing x-Lines (" + xLim + ", 0) (" + xLim + ", " + RESY + ")");
                        g.drawLine(xLim, 0, xLim, RESY);
                    }
                }
                if (p.isYBound()) {
                    int yLim;

                    yLim = (int) ((p.getyLo() * xMul) + yOff);
                    if (yLim > Integer.MIN_VALUE) {
//                        System.out.println("p" + p.getId() + " drawing y-Lines (0, " + yLim + ") (" + RESX + ", " + yLim + ")");
                        g.drawLine(0, yLim, RESX, yLim);
                    }

                    yLim = (int) ((p.getyLo() * yMul) + yOff);
                    if (yLim < Integer.MAX_VALUE) {
//                        System.out.println("p" + p.getId() + " drawing y-Lines (0, " + yLim + ") (" + RESX + ", " + yLim + ")");
                        g.drawLine(0, yLim, RESX, yLim);
                    }
                }

                if (p.getQ() != 0.0) {
                    g.setColor(Color.white);
                    g.drawLine(x, y + DIMY_Hlf, x + DIMX_Hlf * 2, y + DIMY_Hlf);
                    if (p.getQ() > 0) {
                        g.drawLine(x + DIMX_Hlf, y, x + DIMX_Hlf, y + DIMY_Hlf * 2);
                    }
                }
            }
        }

    }

    JFrame jfFrame;
    DrawPanel jpDraw;

    ParticleSimulator.MotionSimulatorCore msc;
    volatile boolean run;

    public MotionSimulatorGUI() {
        jfFrame = new JFrame();
        jpDraw = new DrawPanel(this);
        msc = new MotionSimulatorCore();
        run = false;
    }

    void initFrame() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight() - 50;
        JPanel jp = new JPanel(new BorderLayout());

        jp.add(jpDraw, BorderLayout.CENTER);

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

    void simulate() {
        jpDraw.repaint();
        run = true;

        while (run) {
            msc.simulateStep();
            jpDraw.repaint();
//            msc.showParts();

            try {
                Thread.sleep(7);
            } catch (InterruptedException e) {
                System.out.println("Cautght Exception:\t" + e);
            }
        }
    }

    public static void main(String[] args) {
        MotionSimulatorGUI msg = new MotionSimulatorGUI();

        msg.msc.input();
//        msg.msc.inputRandom();
        msg.initPanel();
        msg.initFrame();
        msg.showFrame();
        msg.jpDraw.initVals();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        msg.simulate();
    }

}

/*

0.001
3

100000000
-1
-1
0
0
-8.215838363
F
F


100000000
1
0
0
0
0
F
F


100000000
-1
1
0
0
8.215838363
F
F

-3.2
3.2
-1.8
1.8

 */
