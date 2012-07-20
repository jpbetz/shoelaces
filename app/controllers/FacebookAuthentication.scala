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
import api.FacebookApiClient
import play.api.cache.Cache
import play.api.Play.current


object FacebookAuthentication extends Controller {
  
  def loggedout = Action { implicit request =>
    getToken(session).map(token => Cache.set(token.getToken(), None))
    
    Ok(views.html.loggedout.render()).withSession()
  }
  
  def login = Action { implicit request =>
    Redirect(routes.FacebookAuthentication.auth())
  }

  
  /****************
   * Facebook OAuth 
   ****************/
  val EMPTY_TOKEN: Token = null 
  
  case class AuthenticatedRequest[A](
    user: User,
    facebookApi: FacebookApiClient, 
    request: Request[A]
  ) extends WrappedRequest(request)
  
  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) { request =>
      getToken(request.session).map { token =>
        var facebookApi = new FacebookApiClient(getOauthService, token)
        try {
          val user: User = Cache.getOrElse[User](token.getToken()) {
            facebookApi.getUserData()
          }
          f(AuthenticatedRequest(user, facebookApi, request))
        } catch {
          case e: IllegalStateException => Status(406)("Sign in failed")
        }
      }.getOrElse(Redirect(routes.FacebookAuthentication.loggedout()))      
    }
  }
  
  // Overloaded method to use the default body parser
  import play.api.mvc.BodyParsers._
  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent]  = {
    Authenticated(parse.anyContent)(f)
  }
  
  def auth = Action {
    val serv = getOauthService
    val authURL = serv.getAuthorizationUrl(EMPTY_TOKEN)
    Redirect(authURL)
  }
  
  def getToken(session: Session) : Option[Token] = {
    val accToken = session.get("acc_token")
    val accSecret = session.get("acc_secret")
    accToken match {
      case Some(k:String) => {
        val oauthService = getOauthService
        val accessToken = new Token(k,accSecret.get)
        Some(accessToken)
      }
      case _ => None
    }
  }
  
  def callback(code: String) = Action { implicit request =>
        val service = getOauthService
        val verifier = new Verifier(code)
        val accessToken = service.getAccessToken(EMPTY_TOKEN,verifier)
        Redirect(routes.Application.index()).withSession(
          "acc_token" -> accessToken.getToken(),
          "acc_secret" -> accessToken.getSecret())
  }
  
  def getOauthService:OAuthService = {
    val key = Play.current.configuration.getString("facebook.apiKey").getOrElse {
      throw new Exception("facebook.apiKey not set in config")
    }
    val secret = Play.current.configuration.getString("facebook.apiSecret").getOrElse {
      throw new Exception("facebook.apiSecret not set in config")
    }
    val callback = Play.current.configuration.getString("facebook.callback").getOrElse {
      throw new Exception("facebook.callback url not set in config")
    }
    new ServiceBuilder()
        .provider(classOf[FacebookApi])
        .apiKey(key)
        .apiSecret(secret)
        .callback(callback).build()
  }
}

