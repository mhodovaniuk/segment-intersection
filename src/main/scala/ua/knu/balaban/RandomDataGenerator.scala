package ua.knu.balaban

import scala.util.Random
import scala.collection.immutable.IndexedSeq

object RandomDataGenerator {
    val random=new Random
    def generateSegments(n:Int,min:Int,max:Int):IndexedSeq[Segment]={
        for (i<-1 to n) yield getRandomSegment(min,max)
    }
    private def getRandomSegment(min:Int,max:Int):Segment={
        new Segment(getRandomPoint(min,max),getRandomPoint(min,max))
    }
    private def getRandomPoint(min:Int,max:Int):Point={
        new Point(getRandomInt(min,max),getRandomInt(min,max))
    }
    private def getRandomInt(min:Int,max:Int):Int={
        math.abs(random.nextInt())%max+min
    }
}
