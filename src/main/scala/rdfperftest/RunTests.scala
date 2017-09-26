package rdfperftest

import better.files._
import org.log4s._

object RunTests extends App {

  private[this] val logger = getLogger

  if (args.length != 4) {
    System.err.println("usage: RunTests minThreads maxThreads warmupMillis testLengthMillis")
    System.err.println("sbt usage: sbt \"run minThreads maxThreads warmupMillis testLengthMillis\"")
  } else {

    val minThreads = args(0).toInt
    val maxThreads = args(1).toInt
    val warmupMillis = args(2).toInt
    val testLengthMillis = args(3).toInt
    
    logger.info(s"Running with minThreads=$minThreads maxThreads=$maxThreads warmupMillis=$warmupMillis testLengthMillis=$testLengthMillis") 

    val file = File(s"test_results_${System.currentTimeMillis}.txt")
    file.appendLine("threads,rdf4j_statements_per_ms,sesame_statements_per_ms")
    logger.info(s"Writing test results to ${file.name}")

    for (threads <- minThreads to maxThreads) {
      val rdf4jStatementsPerMs = RDFPerformanceTest.run(RDF4J, threads, warmupMillis, testLengthMillis)
      val sesameStatementsPerMs = RDFPerformanceTest.run(Sesame, threads, warmupMillis, testLengthMillis)

      file.appendLine(s"$threads,$rdf4jStatementsPerMs,$sesameStatementsPerMs")

      logger.info(s"Writing result to ${file.name} $threads threads: RDF4J $rdf4jStatementsPerMs ms, Sesame $sesameStatementsPerMs ms")
    }
    
    logger.info(s"Finished tests, results written to ${file.name}")

  }
}