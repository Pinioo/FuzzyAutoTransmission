package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MyMain {

    public static void main(String[] args) {
        String fileName = "fuzzy_transmission.fcl";
        FIS fis = FIS.load(fileName, false);

        //wyswietl wykresy funkcji fuzyfikacji i defuzyfikacji
        FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
        fuzzyRuleSet.chart();

        List<GearTestSuit> testSuits = new LinkedList<>();

        testSuits.add(new GearTestSuit(0, 100, 0));
        testSuits.add(new GearTestSuit(20, 100, 0));
        testSuits.add(new GearTestSuit(50, 50, 0));
        testSuits.add(new GearTestSuit(100, 0, 0));
        testSuits.add(new GearTestSuit(140, 100, 6000));
        testSuits.add(new GearTestSuit(150, 50, 7000));

        Car c = new Car(fuzzyRuleSet);
        //zadaj wartosci wejsciowe
        for (GearTestSuit test : testSuits) {
            System.out.println("Speed:\t\t\t" + test.speed);
            System.out.println("Throttle:\t\t" + test.throttle);
            System.out.println("Rpm:\t\t\t" + test.rpm);

            c.speed = test.speed;
            fuzzyRuleSet.setVariable("speed", test.speed);
            fuzzyRuleSet.setVariable("throttle", test.throttle);
            fuzzyRuleSet.setVariable("rpm", test.rpm);

            //logika sterownika
            fuzzyRuleSet.evaluate();

            //graficzna prezentacja wyjscia
            fuzzyRuleSet.getVariable("gear").chartDefuzzifier(true);
            System.out.println("Gear:\t\t\t" + fuzzyRuleSet.getVariable("gear").defuzzify());
            System.out.println("===============");
        }
//        Car c = new Car(fuzzyRuleSet);
//        while(true){
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            c.update();
//            c.showDashInfo();
//        }
    }
}