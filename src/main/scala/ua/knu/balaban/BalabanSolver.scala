package ua.knu.balaban

import scala.collection.mutable.ArrayBuffer

/**
 * Created by mykhailo on 02.05.14.
 */
class BalabanSolver(val segments: Seq[Segment]) {

  var ends = ArrayBuffer[(Double, Segment, String)]()
  var pairs=new ArrayBuffer[(Segment, Segment)]()

  private def lessInStrip(s1: Segment, s2: Segment, a: Double): Boolean = {
    s1.getYByX(a) < s2.getYByX(a)
  }

  def split(l: ArrayBuffer[Segment], b: Double, e: Double): (ArrayBuffer[Segment], ArrayBuffer[Segment]) = {
    val l1 = new ArrayBuffer[Segment]()
    val q = new ArrayBuffer[Segment]()
    for (s <- l) {
      if (!s.isSpanStrip(b, e))
        l1 += s
      else if (q.isEmpty)
        q += s
      else if (Segment.isIntersectInsideStrip(q.last, s, b, e))
        l1 += s
      else
        q += s
    }
    (q, l1)
  }

  def findIntersectionForStaircase(d: ArrayBuffer[Segment], i: Int, s: Segment, b: Double, e: Double): Int = {
    var count = 0
    var j = i
    while (j < d.length && Segment.isIntersectInsideStrip(d(j), s, b, e)) {
      pairs += ((d(j), s))
      j += 1
      count += 1
    }
    j = i - 1
    while (j >= 0 && Segment.isIntersectInsideStrip(d(j), s, b, e)) {
      pairs += ((d(j), s))
      j -= 1
      count += 1
    }
    count
  }

  def findIntersectionForSorted(d: ArrayBuffer[Segment], s: ArrayBuffer[Segment], b: Double, e: Double, x: Double): Int = {
    if (s.isEmpty)
      0
    else {
      var count: Int = 0
      val sIterator = s.iterator
      var currentSSegment = sIterator.next()
      var i=0
      while (i<d.length){
        val currentDSegment = d(i)
        while (currentDSegment.getYByX(x) >= currentSSegment.getYByX(x)) {
          count += findIntersectionForStaircase(d, i, currentSSegment, b, e)
          if (!sIterator.hasNext)
            return count
          currentSSegment = sIterator.next()
        }
        i+=1
      }
      do {
        count += findIntersectionForStaircase(d, d.length, currentSSegment, b, e)
        if (sIterator.hasNext)
          currentSSegment = sIterator.next()
      } while (sIterator.hasNext)
      count
    }
  }

//  def merge(s1: ArrayBuffer[Segment], s2: ArrayBuffer[Segment], x: Double): ArrayBuffer[Segment] = {
//    (s1 ++ s2).sortWith(lessInStrip(_, _, x))
//  }

    def merge(s1: ArrayBuffer[Segment], s2: ArrayBuffer[Segment], x: Double):ArrayBuffer[Segment] = {
      if (s1.isEmpty)
        s2
      else if (s2.isEmpty)
        s1
      else {
        val res=new ArrayBuffer[Segment]()
        var s1Index,s2Index = 0
        while (!(s1Index==s1.size && s2Index==s2.size)){
          if (s1Index==s1.size){
            res+=s2(s2Index)
            s2Index+=1
          } else if (s2Index==s2.size) {
            res+=s1(s1Index)
            s1Index+=1
          } else if (s1(s1Index).getYByX(x)<s2(s2Index).getYByX(x)){
            res+=s1(s1Index)
            s1Index+=1
          } else {
            res+=s2(s2Index)
            s2Index+=1
          }
        }
        res
      }
    }

  def searchInStrip(l: ArrayBuffer[Segment], b: Double, e: Double): ArrayBuffer[Segment] = {
    val (q, l1) = split(l, b, e)
    if (l1.isEmpty)
      q
    else {
      findIntersectionForSorted(q, l1, b, e, b)
      val r1 = searchInStrip(l1, b, e)
      merge(q, r1, e)
    }
  }

  def loc(staircase: ArrayBuffer[Segment], s: Segment, b: Double, e: Double): Int = {
    val x = math.max(s.from.x, b)
    var (start, finish) = (0, staircase.size)
    while (start != finish) {
      val center = (start + finish) / 2
      if (s.getYByX(x) < staircase(center).getYByX(x))
        finish = center
      else {
        start = center + 1
      }
    }
    start
  }

  def findIntersectionForUnsorted(d: ArrayBuffer[Segment], s: ArrayBuffer[Segment], b: Double, e: Double): Unit = {
    for (segment <- s) {
      val i = loc(d, segment, b, e)
      findIntersectionForStaircase(d, i, segment, b, e)
    }
  }

  def treeSearch(lv: ArrayBuffer[Segment], iv: ArrayBuffer[Segment], b: Int, e: Int): ArrayBuffer[Segment] = {
    val (bx, ex) = (ends(b)._1, ends(e)._1)
    if (e - b == 1) {
      searchInStrip(lv, bx, ex)
    } else {
      val ils, irs = new ArrayBuffer[Segment]()
      val (qv, lls) = split(lv, bx, ex)
      findIntersectionForSorted(qv, lls, bx, ex, bx)
      findIntersectionForUnsorted(qv, iv, bx, ex)
      val c = (b + e) / 2
      val cx = ends(c)._1
      for (s <- iv) {
        if (s.to.x < cx)
          ils += s
        if (s.from.x > cx)
          irs += s
      }
      val rls = treeSearch(lls, ils, b, c)
      val lrs = rls
      if (ends(c)._3 == "left") {
        val pos = loc(lrs, ends(c)._2, cx, ex)
        lrs.insert(pos, ends(c)._2)
      } else {
        val pos: Int = lrs.indexOf(ends(c)._2)
        if (pos != -1)
          lrs.remove(pos)
      }
      val rrs = treeSearch(lrs, irs, c, e)
      findIntersectionForSorted(qv, rrs, bx, ex, ex)
      merge(qv, rrs, ex)
    }
  }

  def intersectingPairs(): Seq[(Segment, Segment)] = {
    pairs.clear()
    for (i <- 0 until segments.length) {
      ends+=((segments(i).from.x, segments(i), "left"))
      ends+=((segments(i).to.x, segments(i), "right"))
    }
    ends = ends.sortWith(_._1 < _._1)

    val lr, ir = new ArrayBuffer[Segment]()
    lr += ends(0)._2
    ir ++= segments.filter((s) => {
      !(Segment.equals(ends(0)._2, s) || Segment.equals(ends.last._2, s))
    })
    treeSearch(lr, ir, 0, ends.length - 1)
    pairs
  }
}
