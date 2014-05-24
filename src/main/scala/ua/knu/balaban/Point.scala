package ua.knu.balaban

import ua.knu.balaban.Coordinate.Coordinate


class Point(val x: Double, val y: Double) extends Ordered[Point] {
  val EPS=0.001
  def onSegment(s: Segment): Boolean = {
    insideStrip(s.from.x, s.to.x) && insideVerticalStrip(s.from.y, s.to.y)
  }

  def insideSegmentByCoord(s: Segment, c: Coordinate): Boolean = {
    c match {
      case Coordinate.X =>
        (s.from.x-EPS <= x && x <= s.to.x+EPS) || (s.to.x-EPS <= x && x <= s.from.x+EPS)
      case Coordinate.Y =>
        (s.from.y-EPS <= y && y <= s.to.y+EPS) || (s.to.y-EPS <= y && y <= s.from.y+EPS)
    }
  }

  def insideSegment(s: Segment): Boolean = {
    insideSegmentByCoord(s, Coordinate.X) && insideSegmentByCoord(s, Coordinate.Y)
  }

  def insideStrip(a: Double, b: Double) = {
    a-EPS <= x && x <= b+EPS
  }

  def insideVerticalStrip(a: Double, b: Double) = {
    (a-EPS <= y && y <= b+EPS) || (a+EPS >= y && y >= b-EPS)
  }


  override def hashCode(): Int = x.toInt.hashCode() * 13 + y.toInt.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case p: Point => compare(p) == 0
      case _ => false
    }
  }

  override def compare(that: Point): Int = {
    if (x < that.x || (Utils.compare(x , that.x)==0 && Utils.compare(y, that.y)<0))
      -1
    else
    if (Utils.compare(x , that.x)==0 && Utils.compare(y, that.y)==0)
      0
    else 1
  }

  override def toString: String = "(" + x + ", " + y + ")"
}

object Coordinate extends Enumeration {
  type Coordinate = Value
  val X, Y = Value
}