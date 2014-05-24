package ua.knu

import ua.knu.balaban._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.chart.{XYChart, NumberAxis, LineChart}
import ua.knu.balaban.BalabanSolver
import scala.collection.mutable.ArrayBuffer
import java.io.PrintWriter


object Main extends JFXApp {
  val xAxis = new NumberAxis
  val yAxis = new NumberAxis
  val lineChart = LineChart(xAxis, yAxis)
  lineChart.legendVisible = false
  lineChart.maxWidth = 600
  lineChart.maxHeight = 600

  def mySegments():ArrayBuffer[Segment] = {
    val res=new ArrayBuffer[Segment]()
    val str="{(55, 42), (62,9)}, {(19.0, 24.0), (43.0, 48.0)}, {(1.0, 31.0), (73.0, 49.0)}, {(28.0, 35.0), (47.0, 4.0)}, {(25.0, 72.0), (27.0, 73.0)}"
    for (segStr<-str.split("},"))
      res+=Segment.segmentFromStr(segStr)
    res
  }

  def mySegments2():ArrayBuffer[Segment] = {
    val res=new ArrayBuffer[Segment]()
    res.+=(new Segment(new Point(55,42),new Point(62,9)));
    res.+=(new Segment(new Point(70,92),new Point(75,42)));
    res.+=(new Segment(new Point(2,27),new Point(82,83)));
    res.+=(new Segment(new Point(37,13),new Point(60,22)));
    res.+=(new Segment(new Point(55, 38), new Point(73, 70)));
    res
  }

  val out=new PrintWriter(System.out,true)

  val randomSegments = RandomDataGenerator.generateSegments(50, 0, Int.MaxValue)
//val randomSegments = mySegments2()
  val solver = new BalabanSolver(randomSegments)
  val startTime = System.currentTimeMillis()
  if (randomSegments.length<50  )
    out.println(randomSegments)
  println("0ms")
  val intPairs = solver.intersectingPairs()
  println((System.currentTimeMillis() - startTime)+"ms")
  val intersectingPoints = intPairs.map(p => Segment.intersectSegments(p._1, p._2).get)
  if (randomSegments.length < 1000) {
    out.println(intersectingPoints)
    val segmentsSeries = DataTransformer.segmentsListToXYChartSeries(randomSegments)
    segmentsSeries.foreach(lineChart.getData.add(_))

    val pointsSeries = DataTransformer.pointsListToXYChartSeries(intersectingPoints)
    pointsSeries.foreach(lineChart.getData.add(_))

    stage = new PrimaryStage {
      title = "Line Chart Sample"
      scene = new Scene(800, 600) {
        root = lineChart
      }
    }
  }
}
