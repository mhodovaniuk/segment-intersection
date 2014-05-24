package ua.knu.balaban

import javafx.event.ActionEvent
import javafx.scene.control.{TextField, Button}
import javafx.scene.chart.{XYChart, LineChart}
import javafx.scene.control.ProgressBar
import javafx.stage.FileChooser
import javafx.concurrent.Task
import java.util

/**
 * Created by mykhailo on 08.05.14.
 */
object Utils {
  val EPS = 0.001

  def compare(a: Double, b: Double): Int = {
    if (math.abs(a - b) < EPS)
      0
    else if (a < b)
      -1
    else 1
  }

  def onStartClick(event: ActionEvent, startBtnText: String, fileNameTextField: String, randomSegmentsCount: String): Task[util.ArrayList[XYChart.Series[Number,Number]]] = {
    new Task[util.ArrayList[XYChart.Series[Number,Number]]]() {
      override def call(): util.ArrayList[XYChart.Series[Number,Number]] = {
          def isEmpty(str: String) = str.trim.length == 0
          if (isEmpty(fileNameTextField) && isEmpty(randomSegmentsCount))
            return new util.ArrayList[XYChart.Series[Number,Number]]()
          updateProgress(0,1)
          val segments: Seq[Segment] = if (!isEmpty(fileNameTextField))
            DataReader.readSegments(fileNameTextField)
          else
            RandomDataGenerator.generateSegments(Integer.parseInt(randomSegmentsCount), 0, Int.MaxValue)
          val res=new util.ArrayList[XYChart.Series[Number,Number]]()
          updateProgress(0.05,1)
          val solver = new BalabanSolver(segments)
          val intPairs = solver.intersectingPairs()
          updateProgress(0.3,1)
          val intersectingPoints = intPairs.map(p => Segment.intersectSegments(p._1, p._2).get)
          updateProgress(0.35,1)
          val segmentsSeries = DataTransformer.segmentsListToXYChartSeries(segments)
          updateProgress(0.4,1)
          var curProgress = 0.4
          val step = 1./(segmentsSeries.length + intersectingPoints.length) / 0.6
          segmentsSeries.foreach(o => {
            res.add(o)
            updateProgress(curProgress,1)
            curProgress += step
          })
          val pointsSeries = DataTransformer.pointsListToXYChartSeries(intersectingPoints)
          pointsSeries.foreach(o => {
            res.add(o)
            updateProgress(curProgress,1)
            curProgress += step
          })
          updateProgress(0,1)
           res

      }
    }
  }

  def onOpenFileClick(event: ActionEvent, fileNameTextField: TextField) {
    val fc = new FileChooser()
    fc.setTitle("Select file with data")
    val file = fc.showOpenDialog(null)
    fileNameTextField.setText(file.getAbsolutePath)
  }
}
