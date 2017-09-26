package rdfperftest

import better.files._
import org.log4s._

object RunTests extends App {

  private[this] val logger = getLogger

  val file = File(s"test_results_${System.currentTimeMillis}.txt")
  file.appendLine("threads,rdf4j_statements_per_ms,sesame_statements_per_ms")
  logger.info(s"Writing test results to ${file.name}")

  val warmupMillis = 10000
  val testLengthMillis = 30000

  for (threads <- 1 to 30) {
    val rdf4jStatementsPerMs = RDFPerformanceTest.run(RDF4J, threads, warmupMillis, testLengthMillis)
    val sesameStatementsPerMs = RDFPerformanceTest.run(Sesame, threads, warmupMillis, testLengthMillis)

    file.appendLine(s"$threads,$rdf4jStatementsPerMs,$sesameStatementsPerMs")

    logger.info(s"Writing result to ${file.name} $threads threads: RDF4J $rdf4jStatementsPerMs ms, Sesame $sesameStatementsPerMs ms")
  }

}