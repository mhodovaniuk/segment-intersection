package ua.knu.gui;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;
import ua.knu.balaban.*;

import javax.swing.plaf.FileChooserUI;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class PrimaryController implements Initializable {
    public LineChart<Number,Number> lineChart;
    public ProgressBar progressBar;
    public Button startBtn;
    public TextField fileNameTextField;
    public Button openFileBtn;
    public TextField randomSegmentsCount;
    public Service<ArrayList<XYChart.Series<Number, Number>>> service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChart.setLegendVisible(false);

    }

    public void onStartClick(ActionEvent actionEvent) {
        if (startBtn.getText().equals("Stop")){

            service.cancel();
            lineChart.getData().clear();
            startBtn.setText("Start");
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            return;
        }
        Task<ArrayList<XYChart.Series<Number,Number>>> task = Utils.onStartClick(actionEvent,startBtn.getText(),fileNameTextField.getText(),randomSegmentsCount.getText());
        service = new Service<ArrayList<XYChart.Series<Number, Number>>>() {
            @Override
            protected Task<ArrayList<XYChart.Series<Number, Number>>> createTask() {
                return task;
            }
        };
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                lineChart.getData().clear();
                lineChart.getData().addAll((ArrayList)event.getSource().getValue());
                startBtn.setText("Start");
                progressBar.progressProperty().unbind();
                progressBar.setProgress(0);
            }
        });
        progressBar.progressProperty().bind(task.progressProperty());
        service.start();

        startBtn.setText("Stop");
        fileNameTextField.setText("");
        randomSegmentsCount.setText("");

//        if (startBtn.getText().equals("Start")){
//            if (fileNameTextField.getText().trim().length()==0
//                    && randomSegmentsCount.getText().length()==0)
//                return;
//            startBtn.setText("Stop");
//            lineChart.getData().clear();
//            progressBar.setProgress(0);
//            Seq<Segment> segments;
//            if (fileNameTextField.getText().trim().length()==0)
//                segments= DataReader.readSegments(fileNameTextField.getText());
//            else
//                segments= RandomDataGenerator.generateSegments(Integer.parseInt(randomSegmentsCount.getText()), 0, Integer.MAX_VALUE);
//            progressBar.setProgress(0.05);
//            BalabanSolver solver = new BalabanSolver(segments);
//            Seq<Tuple2<Segment,Segment>> intPairs = solver.intersectingPairs();
//            progressBar.setProgress(0.3);
//            Seq<Point> intersectingPoints = new ArrayBuffer<Point>();
//            Iterator<Tuple2<Segment, Segment>> iterator = intPairs.iterator();
//            while (iterator.hasNext()) {
//                Tuple2<Segment,Segment> t = iterator.next();
//                intersectingPoints.ad(Segment.intersectSegments(t._1(), t._2()).get());
//            }
//
//            progressBar.setProgress(0.35);
//            Seq<XYChart.Series<Number,Number>> segmentsSeries = DataTransformer.segmentsListToXYChartSeries(segments)
//            progressBar.setProgress(0.4);
//            double curProgress = 0.4;
//            double step = 1./(segmentsSeries.length() + intersectingPoints.size()) / 0.6;
//            Iterator<XYChart.Series<Number,Number>> it = segmentsSeries.iterator();
//            while (it.hasNext()) {
//                lineChart.getData().add(it.next());
//            }
//
//            Seq<XYChart.Series<Number,Number>> pointsSeries = DataTransformer.pointsListToXYChartSeries(intersectingPoints)
//            it = pointsSeries.iterator();
//            while (it.hasNext()) {
//                lineChart.getData().add(it.next());
//            }
//
//            progressBar.setProgress(0);
//            startBtn.setText("Start");
//            fileNameTextField.setText("");
//            randomSegmentsCount.setText("");
//        } else {
//            lineChart.getData().clear();
//            progressBar.setProgress(0);
//            startBtn.setText("Start");
//            fileNameTextField.setText("");
//            randomSegmentsCount.setText("");
//        }
    }

    public void onOpenFileClick(ActionEvent actionEvent) {
        FileChooser fc=new FileChooser();
        fc.setTitle("Select file with data");
        File file=fc.showOpenDialog(null);
        fileNameTextField.setText(file.getAbsolutePath());
    }
}
