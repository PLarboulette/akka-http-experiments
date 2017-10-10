import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import models.{People, Team}
import spray.json.DefaultJsonProtocol._
import utils.Helper

import scala.io.StdIn

object Main extends App {

  implicit val system = ActorSystem("akka-http-experiments")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  implicit val peopleFormat = jsonFormat4(People)
  implicit val teamFormat = jsonFormat2(Team)


  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~ path("peoples") {
      get {
        onSuccess(Helper.getAllPeoples()) {
          case peoples : List[People] => complete(peoples)
          case _ => complete(StatusCodes.NotFound)
        }
      }
    } ~ pathPrefix("peoples" / IntNumber) { peopleId =>
      onSuccess(Helper.getPeopleById(peopleId)) {
        case Some(people) => complete(people)
        case None => complete(StatusCodes.NotFound)
      }
    } ~ path("teams") {
      get {
        onSuccess(Helper.getAllTeams()) {
          case teams : List[Team] => complete(teams)
          case _ => complete(StatusCodes.NotFound)
        }
      }
    } ~ pathPrefix("teams" / IntNumber) {
      teamId =>
        onSuccess(Helper.getTeamById(teamId)) {
          case Some(team) => complete(team)
          case None => complete(StatusCodes.NotFound)
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
