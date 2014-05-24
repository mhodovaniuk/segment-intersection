import scala.collection.mutable.ArrayBuffer
import ua.knu.balaban.Segment

class SimpleSolver(segments: Seq[Segment]) {
  def intersectingPairs(): Seq[(Segment, Segment)] = {
    val pairs=new ArrayBuffer[(Segment,Segment)]()
    for (i<-0 until segments.length)
      for (j<-0 until segments.length)
        if (i!=j && j>i && Segment.isIntersect(segments(i),segments(j)))
          pairs += ((segments(i),segments(j)))
    pairs
  }
}
