package utils

import models.{People, Team}

import scala.concurrent.{ExecutionContext, Future}

object Helper {

  val listPeople = List(
    People(1, "Wayne", "Bruce", 30),
    People(2,"Kent", "Clark", 25),
    People(3,"Woman", "Wonder", 25)
  )

  val teams = List (
    Team(1, listPeople),
    Team(2, listPeople.filter(_.age > 26))
  )

  def getAllPeoples (limit :Option[Int] = None) (implicit ec : ExecutionContext) : Future[List[People]] = {
    Future.successful(listPeople)
  }

  def getPeopleById (peopleId : Int) (implicit ec : ExecutionContext) : Future[Option[People]] = {
    Future.successful(listPeople.find(_.id == peopleId))
  }

  def getTeamById(teamId : Int) (implicit ec : ExecutionContext) : Future[Option[Team]] = {
      Future.successful(teams.find(_.id == teamId))
  }

  def getAllTeams (limit : Option[Int] = None) (implicit ec : ExecutionContext) : Future[List[Team]] = {
    Future.successful(teams)
  }
}
