package rdfperftest

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicBoolean

import org.log4s._

class TestStats(rdfEngine: RDFEngine, threads: Int, count: AtomicInteger) {

  private[this] val logger = getLogger

  val startMillis = System.currentTimeMillis
  def elapsedMillis = System.currentTimeMillis - startMillis
  var lastMessageMillis = System.currentTimeMillis

  private val warmedUp = new AtomicBoolean(false)
  def isWarmedUp = warmedUp.get

  private var warmupCount = 0L
  private var warmupTime = 0L

  private val s = if (threads == 1) "" else "s"

  def setWarmedUp = {
    warmupCount = count.get
    warmupTime = System.currentTimeMillis - startMillis
    warmedUp.set(true)
    logger.info(s"$rdfEngine with $threads thread$s: Warmup finished after reading $warmupCount statements")
  }

  def warmedUpCount = count.get - warmupCount

  def rate: Int = {
    if (warmupTime == 0 || warmedUpCount == 0) 0
    else {
      val totalElapsed = elapsedMillis - warmupTime
      (warmedUpCount.toFloat / totalElapsed.toFloat).toInt
    }
  }

  def logUpdate = {
    logger.info(s"$rdfEngine with $threads thread$s ${elapsedMillis}ms elapsed, $warmedUpCount statements read at $rate statements/ms")
    lastMessageMillis = System.currentTimeMillis
  }

}