/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Missile;

import java.text.DecimalFormat;
import java.util.Scanner;

/**
 *
 * @author y2k
 */
public class MissileProblem {

    int step;
    double m;
    double v1, v2;
    double t;

    double dM;
    double dV1, dV2;
    final double dT;

    final double m0;
    final double Ft;
    final double g;
    final double b;
    final double FtBYb;

    public MissileProblem(double m0, double b, double Ft, double dT) {
        this.step = 0;
        this.m = m0;
        this.v1 = this.v2 = 0.0;
        this.t = 0.0;

        this.m0 = m0;
        this.b = b;
        this.Ft = Ft;
        this.FtBYb = Ft / b;

        this.g = 9.8;
        this.dT = dT;
    }

    private void updStep() {
        step++;
    }

    private void updT() {
        t = dT * step;
    }

    private void updM() {
        dM = b * t;
        m = m0 - dM;
    }

    private void updV() {
        dV1 = (Ft - m * g) / m * dT;
        v1 += dV1;

        double tmp = v2;
        v2 = FtBYb * Math.log(m0 / m) - g * t;
        dV2 = v2 - tmp;
    }

    public void simulate() {
        updStep();
        updT();
        updV();
        updM();
    }

    private void output() {
//        DecimalFormat d = new DecimalFormat("#.###");
//        System.out.println(step + "\t" + dM + "\t" + m + "\t" + t + "\t" + dV1 + "\t" + v1 + "\t" + dV2 + "\t" + v2);
        System.out.println(step + "\t" + String.format("%12.8f", dM) + "\t" + String.format("%12.8f", m) + "\t\t" + String.format("%5.3f", t) + "\t" + String.format("%10.8f", dV1) + "\t" + String.format("%10.8f", v1) + "\t" + String.format("%10.8f", dV2) + "\t" + String.format("%10.8f", v2));
//        System.out.println(step++ + "\t" + String.format("%.5g", dM) + "\t" + String.format("%.5g", m) + "\t" + String.format("%.2g", t) + "\t" + String.format("%.3g", v));
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.print("m0 (initial mass)\t\t:\t");
        double m0 = s.nextDouble();

        System.out.print("b (rate of change of mass)\t:\t");
        double b = s.nextDouble();

        System.out.print("Ft (upthrust)\t\t\t:\t");
        double Ft = s.nextDouble();

        System.out.print("dT (time quantum)\t\t:\t");
        double dT = s.nextDouble();

        MissileProblem mp = new MissileProblem(m0, b, Ft, dT);

        System.out.println("\n\nLEGENDS");
        System.out.println(" #\t=\tStep number");
        System.out.println("dM\t=\tTotal reduction in mass since beginning of simulation\t(t = 0)");
        System.out.println(" M\t=\tCurrent mass\t\t\t\t\t\t(calculated as m = m0 - bt)");
        System.out.println(" T\t=\tTime elapsed since beginning of simulation\t\t(t = 0)");
        System.out.println("dV1\t=\tChange in velocity from previous step\t\t\t(approximated)");
        System.out.println(" V1\t=\tCurrent velocity\t\t\t\t\t(approximated)");
        System.out.println("dV2\t=\tChange in velocity from previous step\t\t\t(precise)");
        System.out.println(" V2\t=\tCurrent velocity\t\t\t\t\t(precise)");
        
        System.out.println("\n\nHit enter to simulate next 100 steps");
        System.out.println("#\t\tdM\t\tM\t\tT\t\tdV1\t\tV1\t\tdV2\t\tV2");
            
        mp.output();
        s.nextLine();

        while (true) {
            System.out.println("\nHit enter to simulate next 100 steps");
            s.nextLine();     
            System.out.println("#\t\tdM\t\tM\t\tT\t\tdV1\t\tV1\t\tdV2\t\tV2");
            for (int i = 1; i <= 100; i++) {
                mp.simulate();
                mp.output();
            }
        }
    }

}

/*
5000
40
200000
0.001
 */
