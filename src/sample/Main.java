package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String fileName = "fuzzy_transmission.fcl";
        FIS fis = FIS.load(fileName,false);

        //wyswietl wykresy funkcji fuzyfikacji i defuzyfikacji
        FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
        Car c = new Car(fuzzyRuleSet);
        c.gearTest();

        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(20));
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);

        primaryStage.setTitle("Snake Main Menu");
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1200);
        Scene scene = new Scene(mainGrid);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Hello World");
        primaryStage.show();
//        Label speedL = new Label("0");
//        Label rpmL = new Label("0");
//        Label gearL = new Label("0");

//        mainGrid.add(speedL, 0, 0);
//        mainGrid.add(rpmL, 0, 1);
//        mainGrid.add(gearL, 0, 2);
//
//        mainGrid.add(new Label("KM/H"), 1, 0);
//        mainGrid.add(new Label("RPM"), 1, 1);
//        mainGrid.add(new Label("Gear"), 1, 2);

        Canvas canvas = new Canvas(1200, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mainGrid.add(canvas, 0, 0);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            c.update();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, 800, 600);
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
//            speedL.setText(String.valueOf((int)c.speed));
//            rpmL.setText(String.valueOf((int)c.calculateRpm()));
//            gearL.setText(String.valueOf(c.gear));
        }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
