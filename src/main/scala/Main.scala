import actors.{AddTask, GetAll, TaskActor, UpdateTask}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import models.{People, Task}
import spray.json.DefaultJsonProtocol._
import utils.Helper

import scala.concurrent.duration._
import scala.io.StdIn

object Main extends App {

  implicit val system = ActorSystem("akka-http-experiments")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  implicit val peopleFormat = jsonFormat4(People)
  implicit val teamFormat = jsonFormat2(Team)
  implicit val taskFormat = jsonFormat4(Task)
  implicit val addTaskFormat = jsonFormat3(AddTask)

  val taskActor = system.actorOf(Props[TaskActor], "taskActor")

  val route =
    path("random") {
      get {
        complete(
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, Helper.generateNumbers.map { i => ByteString(s"$i\n")})
        )
      }
    } ~ path("tasks") {
      get {
        parameter("limit".as[Int].?) { (limit) =>
          implicit val timeout: Timeout = 5.seconds
          val tasks = (taskActor ? GetAll(limit)).mapTo[List[Task]]
          complete(tasks)
        }
      }
    } ~ path("tasks") {
      post {
        entity(as[AddTask]) {
          task =>
            taskActor ! AddTask(task.name, task.content, task.peopleId)
            complete(s"Task added")
        }
      }
    } ~ pathPrefix("tasks" / IntNumber) { id =>
      put {
        parameter("content".as[String]) { (content) =>
          taskActor ! UpdateTask(id, content)
          complete((StatusCodes.Accepted, "Content updated"))
        }
      }
    }

  val server = "localhost"
  val port = 8080
  val bindingFuture = Http().bindAndHandle(route, server, port)

  println(s"Server listening on $server:$port")

  StdIn.readLine()

  bindingFuture.flatMap(
    _.unbind()
  ).onComplete(
    _ => system.terminate()
  )
}

