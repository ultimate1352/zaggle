package controllers

import javax.inject._
import play.api.mvc._

/**
  *
  * @author: Lawrence
  * @since: 2018. 1. 22.
  * @note:
  * @version: 0.1.0
  */
@Singleton
class BithumbController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  case class Student(age: Int, name: String)

  import play.api.libs.json._

  implicit val studentFormat = Json.format[Student]

  def index() = Action(parse.json) { implicit request =>
    request.body.asOpt[List[Student]] match {
      case Some(students) =>
        Ok(Json.toJson(students))
      case None =>
        BadRequest("")
    }
  }
}