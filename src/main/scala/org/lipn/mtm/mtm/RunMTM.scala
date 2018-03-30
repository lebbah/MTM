package org.lipn.mtm.mtm

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.DenseVector
import org.lipn.mtm.global.AbstractModel
import org.lipn.mtm.global.AbstractPrototype
import org.lipn.mtm.global.AbstractTrainer
import org.lipn.mtm.utils.NamedVector
import org.lipn.mtm.utils.DataGenerator
import org.lipn.mtm.utils.Output


object RunSom{

    def main(args:Array[String]) {
      run(
          sparkMaster = args(0),
          intputFile = args(1),
          outputDir = args(2),
          execName = args(3),
          nbRow = args(4).toInt,
          nbCol = args(5).toInt,
          tmin = args(6).toDouble,
          tmax = args(7).toDouble,
          convergeDist = args(8).toDouble,
          maxIter = args(9).toInt,
          sep = args(10),
          initMap = args(11).toInt, //0: initialisation aleatoire
          initMapFile = args(12)
      )
    }

  def run(
    sparkMaster: String,
    intputFile: String,
    outputDir: String,
    execName: String = "RunMTM",
    nbRow: Int = 10, 
    nbCol: Int = 10, 
    tmin: Double = 0.9, 
    tmax: Double = 8,
    convergeDist: Double = -0.001,
    maxIter: Int = 50,
    sep : String = ";",
    initMap: Int = 0,
    initMapFile : String = "",
    nbRealVars : Int = 10
    ) = {
    exec(
      intputFile,
      outputDir,
      nbRow,
      nbCol,
      tmin,
      tmax,
      convergeDist,
      maxIter,
      sep,
      initMap,
      initMapFile,
      nbRealVars
    )(
      {
        val sparkConf = new SparkConf().setAppName(execName)
        sparkConf.setMaster(sparkMaster)
        new SparkContext(sparkConf)
      },
      true
    )
  }

  def exec(
    intputFile: String,
    outputDir: String,
    nbRow: Int = 10,
    nbCol: Int = 10,
    tmin: Double = 0.9,
    tmax: Double = 8,
    convergeDist: Double = -0.001,
		maxIter: Int = 50,
		sep : String = ";",
		initMap: Int = 0,
		initMapFile : String = "",
		    nbRealVars : Int = 10
    )(sc:SparkContext, stop:Boolean=false) = {

     val somOptions = Map(
        "clustering.som.nbrow" -> nbRow.toString, 
        "clustering.som.nbcol" -> nbCol.toString,
        "clustering.som.tmin" -> tmin.toString,
        "clustering.som.tmax" -> tmax.toString,
        "clustering.som.initMap" -> initMap.toString,
        "clustering.som.initMapFile" -> initMapFile.toString,   
        "clustering.som.separator" -> sep.toString,
        "clustering.som.nbRealVars" -> nbRealVars.toString
        )

	    val trainingDataset = sc.textFile(intputFile).map(x => new DenseVector(x.split(sep).map(_.toDouble))).cache() 

    println(s"nbRow: ${trainingDataset.count()}")

	 val som = new SomTrainerA
    val startLearningTime = System.currentTimeMillis()
    val model = som.training(trainingDataset, somOptions, maxIter, convergeDist)
    val somDuration = (System.currentTimeMillis() - startLearningTime) / 1000D
    
    val time = Output.write(outputDir, trainingDataset, model, nbRow, nbCol)
    (model, time)
	}
}
