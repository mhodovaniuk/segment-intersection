package ua.knu.balaban

import javafx.scene.chart.XYChart
import scalafx.collections.ObservableBuffer

object DataTransformer {

    def segmentsListToXYChartSeries(segments:Seq[Segment]):Seq[XYChart.Series[Number,Number]]={
        for (s<-segments)
            yield new XYChart.Series[Number,Number]("",
                    ObservableBuffer(Seq(s.from,s.to)).
                        map(p=>new XYChart.Data[Number,Number](p.x,p.y)))
    }

    def pointsListToXYChartSeries(points:Seq[Point]):Seq[XYChart.Series[Number,Number]]={

        for (p<-points)
            yield new XYChart.Series[Number,Number]("",
                ObservableBuffer(new XYChart.Data[Number,Number](p.x,p.y)))
    }
}
