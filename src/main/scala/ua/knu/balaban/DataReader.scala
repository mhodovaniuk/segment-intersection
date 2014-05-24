package ua.knu.balaban

import scala.collection.immutable.IndexedSeq

object DataReader {

  private def getSegment(line: String):Segment = {
    val a=line.split(" ")
    new Segment(new Point(a(0).toDouble,a(1).toDouble),
      new Point(a(2).toDouble,a(3).toDouble))
  }

  def readSegments(fileName:String):IndexedSeq[Segment]={
    val fileLines=io.Source.fromFile(fileName).getLines()
    (for(line<-fileLines)
      yield getSegment(line)).toIndexedSeq
  }
}
