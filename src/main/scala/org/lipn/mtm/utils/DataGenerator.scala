package org.lipn.mtm.utils
import org.apache.spark.mllib.linalg.DenseVector
import util.Random
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.Array


/**
 * Created with IntelliJ IDEA.
 * User: tug
 * Date: 27/03/13
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
object DataGen extends Serializable {

  class Center(val cls: Int, val rayon: Double, val elements: Array[Double]) extends Serializable {
    def this(cls: Int, dims: Int, a: Double, b: Double, rayon: Double) = this(cls, rayon, Array.fill(dims)(new Random(42).nextGaussian() * a + b))
  }


  def generate(sc: SparkContext,
                        numPoints: Int,
                        nbCls: Int,
                        d: Int,
                        numPartitions: Int = 2): RDD[NamedVector] =
  {
    // First, generate some centers
    val rand = new Random(42)
    val r = 1.0
    val centers = Array.fill(nbCls)(Array.fill(d)(rand.nextGaussian() * r))
    // Then generate points around each center
    sc.parallelize(0 until numPoints, numPartitions).map{ idx =>
      val cls = idx % nbCls
      val center = centers(cls)
      val rand2 = new Random(42 + idx)
      new NamedVector(Array.tabulate(d)(i => center(i) + rand2.nextGaussian()), cls)
    }
  }
}

object DataGenerator extends Serializable {
  private val rand = new Random

  private case class DModel(A: Double, B: Double) {
    def gen =  A * rand.nextDouble() + B
  }

  private case class PModel(cls: Int, dmodels: Array[DModel]) {
    def genVector = new DenseVector(dmodels.map(_.gen))
    def genNamedVector = new NamedVector(dmodels.map(_.gen), cls)
  }

  private def PModel2D(cls: Int, A: Double, B: Double, C: Double) = PModel(cls, Array(DModel(A, B), DModel(A, C)))

  private def PModelND(cls: Int, dims: Int, A: Double, B: Double) = PModel(cls, Array.fill(dims)(DModel(A, B)))

  class SModel(N: Int, pmodels: Array[PModel]) {
    private def nextVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genVector
    private def nextNamedVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genNamedVector
    def getVector = Array.tabulate(N)(nextVector)
    def getNamedVector = Array.tabulate(N)(nextNamedVector)
  }
  val CLS_1 = 1
  val CLS_2 = 2
  val CLS_3 = 3
  val CLS_4 = 4

  def genH2Dims(N: Int) = new SModel(N, Array(
    PModel2D(CLS_1, 1, 1, 1),
    PModel2D(CLS_1, 1, 1, 2),
    PModel2D(CLS_1, 1, 1, 3),
    PModel2D(CLS_1, 1, 2, 2),
    PModel2D(CLS_1, 1, 3, 1),
    PModel2D(CLS_1, 1, 3, 2),
    PModel2D(CLS_1, 1, 3, 3)
  ))

  def gen2Cls2Dims(N: Int) = new SModel(N, Array(
    PModel2D(CLS_1, 1, 1, 1),
    PModel2D(CLS_2, 2, 2, 2)
  ))

  def gen2ClsNDims(N: Int, dims: Int) = new SModel(N, Array(
    PModelND(CLS_1, dims, 1, 1),
    PModelND(CLS_2, dims, 2, 2)
  ))
}

