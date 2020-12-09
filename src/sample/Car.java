package sample;

import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Car {
    private final double updatesPerSec = 1000.0/Main.frameDelay;
    private boolean brake = false;
    public int gear = 1;
    public double speed = 5;
    public double throttle = 50;
    public double roadTilt = 0;
    private final FuzzyRuleSet fuzzyRules;

    private final double[] ratio = {190, 90, 50, 35, 28, 21};

    public Car(FuzzyRuleSet fuzzyRules){
        this.fuzzyRules = fuzzyRules;
    }

    public void setGear(double gear){
        if(!(this.gear - 0.55 < gear && gear < this.gear + 0.55))
            this.gear = Math.toIntExact(Math.round(gear));
    }

    public void update(){
        speed += calculateSpeedUpdate();
        speed = Math.max(0, speed);

        fuzzyRules.setVariable("speed", this.speed);
        fuzzyRules.setVariable("throttle", this.throttle);
        fuzzyRules.setVariable("tilt", this.roadTilt);

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

    public double calculateTiltResistance(){
        return 36.0/updatesPerSec * Math.sin(Math.toRadians(this.roadTilt));
    }

    public double calculateWindResistance(){
        return speed*speed/(updatesPerSec*3600);
    }

    public double calculateAcceleration() {
        if(brake) return -0.5;

        double accel;
        double rpm = calculateRpm();
        if(rpm < 6000)
            accel = rpm/12000 - calculateWindResistance();
        else
            accel = 0.5 - ((rpm-6000)/12000)*((rpm-6000)/12000) - calculateWindResistance();

        return (0.5 + throttle/200) * Math.max(accel, 0.2);
    }

    private double calculateSpeedUpdate(){
        return calculateAcceleration() - calculateTiltResistance();
    }

    public void setThrottle(double throttle) {
        if(throttle >= 0 && throttle <= 100)
            this.throttle = throttle;
    }

    public double getThrottle() {
        return this.throttle;
    }

    public void setBrake(boolean b){
        this.brake = b;
    }

    public void setTilt(double tilt) {
        if(tilt >= -15 && tilt <= 15)
            this.roadTilt = tilt;
    }

    public double getTilt() {
        return this.roadTilt;
    }
}
