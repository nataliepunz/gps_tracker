package at.jku.se.gps_tracker.app;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GpsTracker extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel("Devices");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Visits");

        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);

        XYChart.Series<String,Number> dataSeries1 = new Series<String, Number>();
        dataSeries1.setName("elevation");

        dataSeries1.getData().add(new Data<String, Number>("Desktop", 15));
        dataSeries1.getData().add(new Data<String, Number>("Phone"  , 65));
        dataSeries1.getData().add(new Data<String, Number>("5Tablet"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Desdfsktop", 15));
        dataSeries1.getData().add(new Data<String, Number>("T6ablet"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Dessdktop", 15));
        dataSeries1.getData().add(new Data<String, Number>("Ta9blet"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Tab596let"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Degagsktop", 15));
        dataSeries1.getData().add(new Data<String, Number>("Tablet45"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Tabl564et"  , 23));
        dataSeries1.getData().add(new Data<String, Number>("Derhesktop", 15));
        dataSeries1.getData().add(new Data<String, Number>("Tab213let"  , 23));

        

        barChart.getData().add(dataSeries1);
        barChart.setAnimated(false);
        barChart.setTitle("CHESSEBURGER PER DEVICE");
        barChart.setLegendSide(Side.TOP);
        barChart.setLegendVisible(true);

        VBox vbox = new VBox(barChart);

        Scene scene = new Scene(vbox, 400, 200);

        primaryStage.setScene(scene);
        primaryStage.setHeight(300);
        primaryStage.setWidth(1200);
        
        primaryStage.show();
	}
}
