package rdfperftest

import better.files._

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicBoolean

import java.io.ByteArrayInputStream

import org.log4s._

object RDFPerformanceTest {
  private[this] val logger = getLogger

  val file = "src/main/resources/wnba-2015-season.trig".toFile
  val fileBytes = file.byteArray
  logger.debug(s"Parsing RDF from ${file.pathAsString}")

  def run(rdfEngine: RDFEngine, threads: Int, warmUpMillis: Long, testLengthMillis: Long): Int = {
    logger.info(s"Starting test with $threads threads")

    val count = new AtomicInteger(0)
    val testStats = new TestStats(rdfEngine, threads, count)

    val running = new AtomicBoolean(true)
    val warmedUp = new AtomicBoolean(false)
    var warmupCount = 0L
    var warmupTime = 0L

    val startMillis = System.currentTimeMillis

    for (id <- 1 to threads) {
      val thread = new Thread {
        override def run {
          val statementCollector: CountingStatementCollector =
            if (rdfEngine == RDF4J) new RDF4JCountingStatementCollector(count)
            else if (rdfEngine == OpenRDF) new OpenRDFCountingStatementCollector(count)
            else ???

          var elapsedMillis = System.currentTimeMillis - startMillis

          while (running.get) {
            parseFileOnce(rdfEngine, statementCollector)

            logger.debug(s"[$id] Done parsing RDF in ${System.currentTimeMillis - startMillis}ms")

            elapsedMillis = System.currentTimeMillis - startMillis
          }
        }
      }
      thread.start
    }

    val thread = new Thread {
      override def run {

        def elapsedMillis = System.currentTimeMillis - startMillis

        while (elapsedMillis < testLengthMillis) {
          if (!testStats.isWarmedUp && (elapsedMillis > warmUpMillis)) {
            testStats.setWarmedUp
          }

          testStats.logUpdate
          Thread.sleep(5000)
        }
        running.set(false)
      }
    }
    thread.start

    while (running.get) Thread.sleep(1000)

    testStats.rate
  }

  def getStatementCollector(rdfEngine: RDFEngine, count: AtomicInteger) = {
    val statementCollector: CountingStatementCollector =
      if (rdfEngine == RDF4J) new RDF4JCountingStatementCollector(count)
      else if (rdfEngine == OpenRDF) new OpenRDFCountingStatementCollector(count)
      else ???
    statementCollector
  }

  def parseFileOnce(rdfEngine: RDFEngine, statementCollector: CountingStatementCollector) = {
    val in = new ByteArrayInputStream(RDFPerformanceTest.fileBytes)

    if (rdfEngine == RDF4J) {
      import org.eclipse.rdf4j.rio.RDFFormat
      import org.eclipse.rdf4j.rio.Rio
      val parser = Rio.createParser(RDFFormat.TRIG)
      parser.setRDFHandler(statementCollector.asInstanceOf[RDF4JCountingStatementCollector])
      parser.parse(in, "http://www.stellman-greene.com/pbprdf")

    } else if (rdfEngine == OpenRDF) {
      import org.openrdf.rio.RDFFormat
      import org.openrdf.rio.Rio
      val parser = Rio.createParser(RDFFormat.TRIG)
      parser.setRDFHandler(statementCollector.asInstanceOf[OpenRDFCountingStatementCollector])
      parser.parse(in, "http://www.stellman-greene.com/pbprdf")

    } else ???
  }
}