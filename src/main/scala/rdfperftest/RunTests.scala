package rdfperftest

import better.files._
import org.log4s._
import java.util.concurrent.atomic.AtomicInteger

object RunTests extends App {

  private[this] val logger = getLogger

  if (args.toList.head == "--testperengine") {
    logger.info("running the same test for each engine")

    val rdf4jStart = System.currentTimeMillis
    val rdf4jCount = new AtomicInteger(0)
    val rdf4jStatementCollector = RDFPerformanceTest.getStatementCollector(RDF4J, rdf4jCount)
    for (i <- 0 to 10)
      RDFPerformanceTest.parseFileOnce(RDF4J, rdf4jStatementCollector)
    logger.info(s"RDF4J parsed ${rdf4jCount.get} statements in ${System.currentTimeMillis - rdf4jStart}ms")

    val openrdfStart = System.currentTimeMillis
    val openrdfCount = new AtomicInteger(0)
    val openrdfStatementCollector = RDFPerformanceTest.getStatementCollector(OpenRDF, openrdfCount)
    for (i <- 0 to 10)
      RDFPerformanceTest.parseFileOnce(OpenRDF, openrdfStatementCollector)
    logger.info(s"OpenRDF parsed ${openrdfCount.get} statements in ${System.currentTimeMillis - openrdfStart}ms")

  } else if (args.length != 4) {
    System.err.println("usage: RunTests minThreads maxThreads warmupMillis testLengthMillis")
    System.err.println("   or: RunTests --runonce")
    System.err.println("sbt usage: sbt \"run minThreads maxThreads warmupMillis testLengthMillis\"")
    System.err.println("       or: sbt \"run --runonce\"")
  } else {

    val minThreads = args(0).toInt
    val maxThreads = args(1).toInt
    val warmupMillis = args(2).toInt
    val testLengthMillis = args(3).toInt

    logger.info(s"Running with minThreads=$minThreads maxThreads=$maxThreads warmupMillis=$warmupMillis testLengthMillis=$testLengthMillis")

    val file = File(s"test_results_${System.currentTimeMillis}.txt")
    file.appendLine("threads,rdf4j_statements_per_ms,openrdf_statements_per_ms")
    logger.info(s"Writing test results to ${file.name}")

    for (threads <- minThreads to maxThreads) {
      val rdf4jStatementsPerMs = RDFPerformanceTest.run(RDF4J, threads, warmupMillis, testLengthMillis)
      val openrdfStatementsPerMs = RDFPerformanceTest.run(OpenRDF, threads, warmupMillis, testLengthMillis)

      file.appendLine(s"$threads,$rdf4jStatementsPerMs,$openrdfStatementsPerMs")

      logger.info(s"Writing result to ${file.name} $threads threads: RDF4J $rdf4jStatementsPerMs ms, OpenRDF $openrdfStatementsPerMs ms")
    }

    logger.info(s"Finished tests, results written to ${file.name}")

  }
}