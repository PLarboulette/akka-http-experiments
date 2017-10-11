package actors

import akka.actor.{Actor, ActorLogging}
import models.Task

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}
import akka.pattern.pipe

case class GetAll (limit : Option[Int] = None)
case class AddTask (name : String, content : String, peopleId : Int)
case class UpdateTask (id : Int, content : String)
case class DeleteTask (id : String)

object TaskActor {

  def getAll(limit : Option[Int]  = None): Future[List[Task]] = {
    println(limit)
    Future.successful(List(Task(1,"Name1", "Content1", 2)))
  }

  def addTask (name : String, content : String, peopleId : Int) (implicit ec : ExecutionContext) : Future[Try[Task]] = {
    println(s"$name // $content // $peopleId")
    Future.successful(Failure(new Exception("TODO")))
  }

  def updateTask (id : Int, content : String) (implicit ex :ExecutionContext) : Future[Try[Task]] = {
    println(s"$id // $content")
    Future.successful(Failure(new Exception("TODO")))
  }

  def deleteTask(id : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    Future.successful(false)
  }
}

class TaskActor extends Actor with ActorLogging {

  import TaskActor._
  import context._

  override def receive = {
    case GetAll(limit) =>
      getAll(limit) pipeTo sender
    case AddTask(name, content, peopleId) =>
      addTask(name, content, peopleId) pipeTo sender
    case UpdateTask(id, content) =>
      updateTask(id, content) pipeTo sender
    case DeleteTask(id)=>
      deleteTask(id) pipeTo sender
  }
}
