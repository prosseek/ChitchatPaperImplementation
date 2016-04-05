package simulation.grapevineType

import grapevineType.{GrapevineType, StringType, BottomType, FloatType}
import simulation.theoryFalsePositives.Float

/**
 * Created by smcho on 9/5/14.
 */
class FloatSimulation (config:Map[String, Int]) extends Simulation(config) {
  def getFloat(ba:Array[Byte]) : Option[GrapevineType] = {
    val bits = new FloatType
    if (bits.fromByteArray(ba) == BottomType.NoError) {
      Some(bits)
    }
    else None
  }

  def getRandomFloat(byteWidth:Int = FloatType.getSize) = {
    getFloat(Simulation.getRandomByteArray(byteWidth))
  }

  /*
    GET THEORETICAL FP TIME/DATE
  */
  def getTheory(bytes:Int = FloatType.getSize) :Map[String, Double] = {
    Map[String, Double](
      "theory_fp" -> Float.fp(bytes),
      "theory_fp_pair" -> Float.fp_pair(bytes)
    )
  }
  /*
    GENERATE TABLES - RUN TESTS
  */
  def run(width:Int, size:Int, near:Int, lat:Boolean) = {
    var bytes = math.max(width, FloatType.getSize)
    var bottom_computation = 0
    var fp = 0
    var bottom_relation_pair = 0
    var fp_pair = 0
    var bottom_relation_near = 0
    var fp_near = 0

    (1 to size).foreach { i =>
      val la = getRandomFloat(bytes)
      if (la.isEmpty)
        bottom_computation += 1 // get bottom_computation
      else {
        fp += 1
        val s = new StringSimulation(config)
        val ra = s.getRandomString(StringType.getMiniumLength)
        if (ra.isDefined) {
          fp_pair += 1
        }
      }
    }

    Map[String, Double](
      "fp" -> fp/size.toDouble,
      "fp_pair" -> fp_pair/size.toDouble
    ) ++ getTheory(bytes)
  }

  override def simulate(width:Int) :Map[String,Double] = {
    val iteration = config("iteration")
    // -1 means I'm not using it
    runAndAverage(iteration = iteration, f = () => run(width = width, size = config("size"), near = -1, lat = true))
  }
}

// Just test code to print out
object FloatSimulation extends App {
  var m = Map[String,Int]("size" -> 100000, "iteration" -> 3, "verbose" -> 0)
  var ls = new AgeSimulation(m)
  val f = (i :Int) => ls.simulate(width = i)

  (1 to 1).foreach { i =>
    println(s"SIMULATION FOR Float TYPE ${i}")
    println(s"${Util.map2string(f(i))}")
  }

  //  m = Map[String,Int]("size" -> 100, "near" -> 10, "iteration" -> 10, "verbose" ->0)

  //  val res = ls.simulateOverWidth(1,10)
  //  println(res.mkString("\n\n"))
}