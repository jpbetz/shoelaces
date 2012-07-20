package controllers

import java.lang._
import play.api._
import play.api.mvc._
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FacebookApi
import org.scribe.builder.api.Api
import org.scribe.model.Verifier
import org.scribe.model.Token
import org.scribe.model.Response
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import com.google.gson.Gson
import json.User
import controllers.FacebookAuthentication._

object Application extends Controller {
  
  def index = Authenticated { implicit request =>
    Ok(views.html.index.render(request.user))
  }
  
  // use Authenticated action for pages where the user must be logged in
  // when using Authenticated the request also contains the fields user and facebookApi
}

