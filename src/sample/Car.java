package sample;

import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Car {
    public int gear = 1;
    public double speed = 0;
    public double throttle = 50;
    private final FuzzyRuleSet fuzzyRules;

    private final double[] ratio = {210, 70, 50, 35, 28, 21};

    public void gearTest() {
        double[] speeds = {10, 30, 50, 70, 90, 120};
        for(int i = 0; i < 6; i++) {
            System.out.println((i+1) + ", " + speeds[i] + " km/h, " + speeds[i]*ratio[i] + " rpm");
        }
    }

    public Car(FuzzyRuleSet fuzzyRules){
        this.fuzzyRules = fuzzyRules;
    }

    public void setGear(double gear){
        if(!(this.gear - 0.7 < gear && gear < this.gear + 0.7))
            this.gear = Math.toIntExact(Math.round(gear));
    }

    public void update(){
        speed += calculateAcceleration();

        fuzzyRules.setVariable("speed", this.speed);
        fuzzyRules.setVariable("throttle", this.throttle);
        fuzzyRules.setVariable("rpm", this.calculateRpm());

        //logika sterownika
        fuzzyRules.evaluate();

        this.setGear(
            fuzzyRules.getVariable("gear")
                    .defuzzify()
        );
    }

    public void showDashInfo(){
        System.out.println("Speed:\t" + this.speed);
        System.out.println("Rpm:\t" + calculateRpm());
        System.out.println("Gear:\t" + this.gear);
        System.out.println("===============");
    }

    public double calculateRpm(){
        return speed * ratio[this.gear-1];
    }

    private double calculateAcceleration() {
        double rpm = calculateRpm();
        if(rpm < 6000){
            double accel = rpm/12000 - speed*speed/180000;
            return (throttle/100) * Math.max(accel, 0.01);
        }
        else {
            double accel = 0.5 - ((rpm-6000)/12000)*((rpm-6000)/12000) - speed*speed/180000;
            return (throttle/100) * Math.max(accel, 0);
        }
    }
}
