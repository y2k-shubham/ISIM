/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Inventory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Arnav
 */
public class InventoryProblem {

    class Day {

        int qtyOrg;
        int qtyDel;
        int qtyDem;
        int qtyOrd;
        boolean outOrder;
    }

    int dueDay;
    int qtyDue;
    int qtyRem;
    int qtyDem[];

    float cost;

    final float costCar;
    final float costOrd;
    final float costLos;

    final int noDays;
    final int qtyDemLim;

    int qtyReOPnt;
    int qtyReOQty;

    Day dStat[];

    public InventoryProblem() {
        this.qtyDue = 0;
        this.costCar = 0.75f;
        this.costOrd = 75.0f;
        this.costLos = 18.0f;

        this.noDays = 180;
        this.qtyDemLim = 99;
        this.qtyRem = 115;
        this.qtyDem = new int[noDays];

        this.dStat = new Day[noDays];
        for (int i = 0; i < noDays; i++) {
            dStat[i] = new Day();
            dStat[i].outOrder = false;
            dStat[i].qtyDel = 0;
            dStat[i].qtyDem = 0;
            dStat[i].qtyOrg = 0;
            dStat[i].qtyOrd = 0;
        }
    }

    void setReoStrategy(int qtyReOPnt, int qtyReoQty) {
        this.cost = 0.0f;
        this.qtyReOPnt = qtyReOPnt;
        this.qtyReOQty = qtyReoQty;
    }

    void readDemands(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new FileReader(filename));
        for (int i = 0; i < noDays; i++) {
            qtyDem[i] = s.nextInt();
        }
        s.close();
    }

    void genDemands(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        Random r = new Random();

        for (int i = 0; i < noDays; i++) {
            fw.write(r.nextInt(qtyDemLim + 1) + "\n");
        }
        fw.close();
    }

    void simulate() {
        for (int i = 0; i < noDays; i++) {
            dStat[i].qtyOrg = qtyRem;

            // add ordered quantity to remaining quantity
            if (qtyDue > 0) {
                if (i == dueDay) {
                    dStat[i].qtyDel = qtyDue;
                    dStat[i].outOrder = false;

                    qtyRem += qtyDue;
                    qtyDue = 0;
                } else if (i + 1 < noDays) {
                    dStat[i + 1].outOrder = true;
                }
            }

            dStat[i].qtyDem = qtyDem[i];
            // find qtyShort or excess
            if (qtyRem >= qtyDem[i]) {
                qtyRem -= qtyDem[i];
                cost += qtyRem * costCar;
            } else {
                int qtyShort = qtyDem[i] - qtyRem;
                cost += costLos * qtyShort;
                qtyRem = 0;
            }

            // place order if needed
            if (qtyDue == 0 && qtyRem <= qtyReOPnt) {
                dStat[i].qtyOrd = qtyReOQty;
                if (i + 1 < noDays) {
                    dStat[i + 1].outOrder = true;
                }

                qtyDue = qtyReOQty;
                dueDay = i + 3;
                cost += costOrd;
            }
        }
    }

    void output() {
        int qtyDelTot = 0;
        int qtyOrdTot = 0;
        int qtyDemTot = 0;
        int qtyShortTot = 0;
        float costLosTot = 0.0f;
        float costOrdTot = 0.0f;
        float costInvTot = 0.0f;

        System.out.println("\nDay-by-day stats are:-");
        System.out.println("DNO\tQ_ORG\tQ_DEL\tQ_NEW\tQ_DEM\tSHORT\tLOS\tQ_FIN\tST_CST\tO_ORD\tQ_ORD\tOR_CST\tTO_COST");
        for (int i = 0; i < noDays; i++) {
            Day d = dStat[i];

            qtyDelTot += d.qtyDel;
            System.out.print(i + "\t" + d.qtyOrg + "\t" + d.qtyDel + "\t");

            int qtyNew = (d.qtyOrg + d.qtyDel);
            qtyDemTot += d.qtyDem;
            System.out.print(qtyNew + "\t" + d.qtyDem + "\t");

            int qtyShort = d.qtyDem - qtyNew;
            float costLos = 0.0f;
            if (qtyShort > 0) {
                System.out.print(qtyShort + "\t" + (costLos = (qtyShort * this.costLos)) + "\t");
                qtyShortTot += qtyShort;
                costLosTot += costLos;
            } else {
                System.out.print("0\t0\t");
            }

            int qtyFin = Math.max(0, qtyNew - d.qtyDem);
            float costInv = qtyFin * this.costCar;
            costInvTot += costInv;
            System.out.print(qtyFin + "\t" + costInv + "\t");

            if (d.outOrder) {
                System.out.print("T\t");
            } else {
                System.out.print("F\t");
            }

            float costOrd = 0.0f;
            if (d.qtyOrd > 0) {
                costOrd = this.costOrd;
                costOrdTot += this.costOrd;
                System.out.print(d.qtyOrd + "\t" + costOrd + "\t");
            } else {
                System.out.print("0\t0\t");
            }

            float costTot = costLos + costInv + costOrd;
//            this.cost += costTot;
            System.out.println(costTot);
        }

        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("\t\t" + qtyDelTot + "\t\t" + qtyDemTot + "\t" + qtyShortTot + "\t" + costLosTot + "\t\t" + costInvTot + "\t\t" + qtyOrdTot + "\t" + costOrdTot + "\t" + cost);
    }

    public static void main(String[] args) throws IOException {
        InventoryProblem ip = new InventoryProblem();
//        ip.genDemands("Inventory/dem_1.txt");
//        ip.genDemands("Inventory/dem_2.txt");
//        ip.genDemands("Inventory/dem_3.txt");
//        ip.genDemands("Inventory/dem_4.txt");
//        ip.genDemands("Inventory/dem_5.txt");

        ip.readDemands("Inventory/dem_1.txt");
        ip.setReoStrategy(125, 150);

        ip.simulate();
        ip.output();
    }

}
