package rdfperftest

import org.log4s._
import java.util.concurrent.atomic.AtomicInteger

abstract class CountingStatementCollector(count: AtomicInteger) {
  private[this] val logger = getLogger

  def getCount = count.get

  def startRDF(): Unit = logger.debug("startRDF")
  def endRDF(): Unit = logger.debug(s"endRDF, count = ${count}")
  def handleComment(comment: String): Unit = logger.debug(s"handleComment ${comment}")
  def handleNamespace(prefix: String, uri: String): Unit = logger.debug(s"handleNamespace ${prefix} ${uri}")
}

class RDF4JCountingStatementCollector(count: AtomicInteger) extends CountingStatementCollector(count) with org.eclipse.rdf4j.rio.RDFHandler {
  def handleStatement(statement: org.eclipse.rdf4j.model.Statement): Unit = {
    count.incrementAndGet
  }
}

class OpenRDFCountingStatementCollector(count: AtomicInteger) extends CountingStatementCollector(count) with org.openrdf.rio.RDFHandler {
  def handleStatement(statement: org.openrdf.model.Statement): Unit = {
    count.incrementAndGet
  }
}
