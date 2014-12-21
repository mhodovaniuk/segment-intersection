package ua.knu.balaban

/*
 * y1=kx1+b
 * y2=kx2+b
 *
 * y2-y1=k(x2-x1) => k=(y2-y1)/(x2-x1)
 *
 * b=(y1+y2-k(x1+x2))/2
 *
 * y=kx+b
 */

class Segment(p1: Point, p2: Point) {
  val from = if (p1.compare(p2) < 0) p1 else p2
  val to = if (p1.compare(p2) < 0) p2 else p1

  var segmentType: SegmentType.SegmentType = SegmentType.GENERAL

  val k: Double = if (to.x == from.x) {
    segmentType = SegmentType.OY
    0
  } else (to.y - from.y) / (to.x - from.x)

  val b: Double = if (to.y == from.y) {
    segmentType = SegmentType.OX
    0
  }
  else if (segmentType == SegmentType.OY)
    0
  else (to.y + from.y - k * (from.x + to.x)) / 2

  def getXByY(y: Double) = {
    (y - b) / k
  }

  def getYByX(x: Double) = {
    x * k + b
  }

  def isSpanStrip(a: Double, b: Double) = {
    (from.x <= a && to.x >= b) || (from.x >= b && to.x <= a)
  }

  def isInnerStrip(a: Double, b: Double) = {
    a <= from.x && b >= to.x && a <= to.x && b >= from.x
  }

  def isCrossStrip(a: Double, b: Double) = {
    !(isSpanStrip(a, b) || isInnerStrip(a, b))
  }


  override def hashCode(): Int = {
    from.hashCode() * 7 + to.hashCode()
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case s: Segment => from.equals(s.from) && to.equals(s.to)
      case _ => false
    }
  }

  override def toString: String = "{" + from.toString + ", " + to.toString + "}"
}

object SegmentType extends Enumeration {
  type SegmentType = Value
  val OX, OY, GENERAL = Value
}

object Segment {
  def segmentFromStr(segStr: String): Segment = {

    val s=segStr.filter(c=> !(c==' ' || c=='}' || c=='{'))
    val ps=s.split("[)],")
    val p1=ps(0).filter(c=> !(c=='(' || c==')'))
    val p2=ps(1).filter(c=> !(c=='(' || c==')'))
    val pp1=p1.split(',')
    val pp2=p2.split(',')
    new Segment(new Point(pp1(0).toDouble,pp1(1).toDouble),new Point(pp2(0).toDouble,pp2(1).toDouble))
  }

  def equals(s1: Segment, s2: Segment): Boolean = {
    s1.from.compare(s2.from) == 0 && s1.to.compare(s2.to) == 0
  }

  def isIntersectInsideStrip(s1: Segment, s2: Segment, b: Double, e: Double): Boolean = {
    intersectSegments(s1, s2) match {
      case Some(p) => p.insideStrip(b, e)
      case None => false
    }
  }

  def isIntersect(s1: Segment, s2: Segment) = {
    intersectSegments(s1, s2) match {
      case Some(p) => true
      case None => false
    }
  }

  def intersectSegments(s1: Segment, s2: Segment): Option[Point] ={
    intersectLines(s1,s2) match {
      case Some(p) => if (p.onSegment(s1) && p.onSegment(s2)) Some(p) else None
      case _ => None
    }

  }

  def intersectLines(s1: Segment, s2: Segment): Option[Point] = {
    s1.segmentType match {
      case SegmentType.OX => {
        s2.segmentType match {
          case SegmentType.OX => None
          case SegmentType.OY => {
            val int = new Point(s2.from.x, s1.from.y)
            if (int.insideSegmentByCoord(s1, Coordinate.Y)
              && int.insideSegmentByCoord(s2, Coordinate.X))
              Some(int)
            else None
          }
          case SegmentType.GENERAL => {
            val int = new Point(s2.getXByY(s1.from.y), s1.from.y)
            if (int.insideSegment(s2) && int.insideSegmentByCoord(s1, Coordinate.X))
              Some(int)
            else None
          }
        }
      }
      case SegmentType.OY => {
        s2.segmentType match {
          case SegmentType.OX => intersectSegments(s2, s1)
          case SegmentType.OY => None
          case SegmentType.GENERAL => {
            val int = new Point(s1.from.x, s2.getYByX(s1.from.x))
            if (int.insideSegment(s2) && int.insideSegmentByCoord(s1, Coordinate.Y))
              Some(int)
            else None
          }
        }
      }
      case SegmentType.GENERAL => {
        s2.segmentType match {
          case SegmentType.OX => intersectSegments(s2, s1)
          case SegmentType.OY => intersectSegments(s2, s1)
          /*
           * y=k1*x+b1
           * y=k2*x+b2
           *
           * k1*x+b1=k2*x+b2
           * x=(b2-b1)/(k1-k2)
           */

          case SegmentType.GENERAL => {
            val x = (s2.b - s1.b) / (s1.k - s2.k)
            val y = x * s1.k + s1.b
            Some(new Point(x, y))
          }
        }
      }
    }
  }
}
