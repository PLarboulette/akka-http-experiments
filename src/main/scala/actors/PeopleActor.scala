package actors

import akka.actor.{Actor, ActorLogging}
import models.People

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class GetAll(limit : Option[Int] = None)
case class AddPeople (lastName : String, firstName : String, age : Int)

object PeopleActor {

  val listPeople = List(
    People(1, "Wayne", "Bruce", 30),
    People(2, "Kent", "Clark", 25),
    People(3, "Allen", "Barry", 25)
  )

  def getAll(limit: Option[Int] = None)(implicit ec: ExecutionContext): Future[List[People]] = {
    Future.successful(listPeople)
  }

  def addPeople(lastName: String, firstName: String, age: Int)(implicit ec: ExecutionContext): Future[Try[People]] = {
    Future.successful(Failure(new Exception("TO DO")))
  }

  def updatePeople(id: Int, lastNameOpt: Option[String], firstNameOpt: Option[String], ageOpt: Option[Int]): Future[Try[People]] = {
    val before = listPeople.find(_.id == id)
    before
      .map(_
        .copy(
          lastName = lastNameOpt.getOrElse(before.get.lastName),
          firstName = firstNameOpt.getOrElse(before.get.firstName),
          age = ageOpt.getOrElse(before.get.age)
        )
      )
      .map(people => Future.successful(Success(people)))
      .getOrElse(Future.successful(Failure(new Exception("People is not defined"))))
  }
}

class PeopleActor extends Actor with ActorLogging {

  import PeopleActor._
  import context._

  override def receive = {
    case GetAll(limit) =>
      getAll(limit)
    case AddPeople(lastName, firstName, age) =>
      addPeople(lastName, firstName, age)
  }
}
