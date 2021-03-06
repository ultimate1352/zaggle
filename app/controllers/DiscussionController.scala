package controllers

import javax.inject._

import common.AuthorizedAction
import io.swagger.annotations.{Api, ApiOperation}
import models.competition.Competition
import models.discussion.Discussion
import play.api.libs.json._
import play.api.mvc._
import services.DiscussionService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  *
  * @author Lawrence
  * @since 2018. 4. 5.
  * @note
  * @version 0.2.0
  */
@Api(value = "Discussion")
@Singleton
class DiscussionController @Inject()(cc: ControllerComponents
                                     , authorizedAction: AuthorizedAction
                                     , discussionService: DiscussionService) extends AbstractController(cc) {

  @ApiOperation(value = "List of Discussion",
    notes = "Discussion 리스트 정보를 반환한다.",
    response = classOf[Discussion],
    responseContainer = "List")
  def list(competitionId: Option[Long]) = Action.async { implicit request =>
    val discussions = competitionId match {
      case Some(competitionId) =>
        discussionService.list(competitionId)
      case None =>
        discussionService.list
    }
    discussions.map { discussions =>
      Ok(Json.toJson(discussions))
    }
  }

  @ApiOperation(value = "Discussion",
    notes = "Discussion을 반환한다.",
    response = classOf[Discussion])
  def get(id: Long) = Action.async { implicit request =>
    discussionService.getById(id).map { discussion =>
      discussion match {
        case Some(discussion) =>
          Ok(Json.toJson(discussion))
        case _ =>
          NotFound
      }
    }
  }

  @ApiOperation(value = "Create Discussion",
    notes = "Discussion을 생성한다",
    response = classOf[Discussion])
  def create() = authorizedAction.async(parse.json) { request: Request[JsValue] =>
    val reqContextOpt = request.attrs.get(AuthorizedAction.JWT_KEY)
    request.body.asOpt[Discussion] match {
      case Some(discussion) =>
        discussionService.create(discussion.copy(userId = reqContextOpt.get.userId)).map { discussion =>
          Ok(Json.toJson(discussion))
        }
      case _ =>
        Future(BadRequest(""))
    }
  }

  @ApiOperation(value = "List of Competition",
    notes = "Competition 리스트 정보를 반환한다.",
    response = classOf[Competition],
    responseContainer = "List")
  def modity(id: Long) = Action.async(parse.json) { request: Request[JsValue] =>
   request.body.asOpt[Discussion] match {
      case Some(discussion) =>
        discussionService.update(discussion)
        Future(Ok(""))
      case _ =>
        Future(BadRequest(""))
    }
  }

  def delete(id: Long) = Action.async { implicit request =>
    discussionService.delete(id)
    Future(Ok(""))
  }

}