package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Main extends Application {
    public static int frameDelay = 20;

    @Override
    public void start(Stage primaryStage) {
        String fileName = "fuzzy_transmission.fcl";
        FIS fis = FIS.load(fileName,false);

        FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
//        fuzzyRuleSet.chart();
        double[] speeds = {50, 50, 90, 90};
        double[] throttles = {25, 100, 60, 60};
        double[] tilts = {0, 0, -10, 10};
        for(int i = 0; i < speeds.length; i++){
            fuzzyRuleSet.setVariable("speed", speeds[i]);
            fuzzyRuleSet.setVariable("throttle", throttles[i]);
            fuzzyRuleSet.setVariable("tilt", tilts[i]);
            fuzzyRuleSet.evaluate();
            fuzzyRuleSet
                    .getVariable("gear")
                    .chartDefuzzifier(true)
                    .setTitle(
                            "Speed: " +
                            speeds[i] +
                            " Throttle: " +
                            throttles[i] +
                            " Tilt: " +
                            tilts[i]
                    );
        }

        Car c = new Car(fuzzyRuleSet);

        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(20));
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);

        primaryStage.setResizable(true);
        primaryStage.setMinHeight(350);
        primaryStage.setMinWidth(900);
        Scene scene = new Scene(mainGrid);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Fuzzy automatic trasmission simulation");
        primaryStage.show();

        Canvas canvas = new Canvas(1000, 900);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mainGrid.add(canvas, 0, 1);

        HBox controlBox = new HBox();
        controlBox.setSpacing(50);
        mainGrid.add(controlBox, 0, 0);

        // Throttle control
        Label throttleLabel = new Label("Throttle:");
        controlBox.getChildren().add(throttleLabel);
        Slider throttleSlider = new Slider();
        throttleSlider.setMin(0);
        throttleSlider.setMax(100);
        throttleSlider.setMajorTickUnit(10);
        throttleSlider.setMinorTickCount(10);
        throttleSlider.setShowTickLabels(true);
        throttleSlider.setValue(c.getThrottle());
        throttleSlider.setVisible(true);
        throttleSlider.valueProperty().addListener(
                (o, oldValue, newValue) -> c.setThrottle(newValue.floatValue())
        );
        controlBox.getChildren().add(throttleSlider);

        // Tilt control
        Label tiltLabel = new Label("Tilt:");
        controlBox.getChildren().add(tiltLabel);
        Slider tiltSlider = new Slider();
        tiltSlider.setMin(-15);
        tiltSlider.setMax(15);
        tiltSlider.setMajorTickUnit(5);
        tiltSlider.setMinorTickCount(5);
        tiltSlider.setShowTickLabels(true);
        tiltSlider.setValue(c.getTilt());
        tiltSlider.setVisible(true);
        tiltSlider.valueProperty().addListener(
                (o, oldValue, newValue) -> c.setTilt(newValue.floatValue())
        );
        controlBox.getChildren().add(tiltSlider);

        Button brakeButton = new Button("Brake");
        controlBox.getChildren().add(brakeButton);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) * 0.2;
        primaryStage.setX(x);
        primaryStage.setY(y);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameDelay), event -> {
            c.setBrake(brakeButton.pressedProperty().get());
            c.update();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, 850, 250);
            gc.setFill(Color.BLACK);
            for(int i = 0; i < 10; i++) {
                gc.strokeText(
                        String.valueOf(i*1000),
                        200 + 180*Math.cos(Math.PI + Math.PI*i/9),
                        200 + 180*Math.sin(Math.PI + Math.PI*i/9)
                );
            }
            gc.strokeLine(
                    200,
                    200,
                    200 + 160*Math.cos(Math.PI + Math.PI*c.calculateRpm()/9000),
                    200 + 160*Math.sin(Math.PI + Math.PI*c.calculateRpm()/9000)
            );
            gc.strokeText(
                    String.valueOf(c.gear),
                    400,
                    50
            );
            for(int i = 0; i < 16; i++) {
                gc.strokeText(
                        String.valueOf(i*20),
                        600 + 180*Math.cos(Math.PI + Math.PI*i/15),
                        200 + 180*Math.sin(Math.PI + Math.PI*i/15)
                );
            }
            gc.strokeLine(
                    600,
                    200,
                    600 + 160*Math.cos(Math.PI + Math.PI*c.speed/300),
                    200 + 160*Math.sin(Math.PI + Math.PI*c.speed/300)
            );
        }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        primaryStage.show();
        timeline.play();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
