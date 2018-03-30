package org.lipn.som.test

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.lipn.som.som.RunSom

class RunSOMTest extends AssertionsForJUnit {

	@Test def veifySimpleFileExist {//fusionaxa_mini.csv
	  RunSom.run(
			  intputFile = "conf/test/resources/fusion1000.csv",
			  outputDir = "target/surefire-reports",
			  sparkMaster = "local[4]",
			  execName = "RunSomTest",
			  nbRow = 5,//10, 
			  nbCol = 5,//10, 
			  tmin = 8, 
			  tmax = 0.9,
			  convergeDist = -0.001,
			  maxIter = 30
    )
	}
}