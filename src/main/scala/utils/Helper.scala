package utils

import akka.NotUsed
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContext
import scala.util.Random

object Helper {

  def generateNumbers (implicit ec : ExecutionContext): Source[Int, NotUsed] = {
    Source.fromIterator(() =>
      Iterator.continually(Random.nextInt()))
  }
}
