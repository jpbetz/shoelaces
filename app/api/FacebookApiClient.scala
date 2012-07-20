package api

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

class FacebookApiClient(oauthService:OAuthService, accessToken: Token) {
  
  def getUserData(): User = {
    val gson = new Gson()
    val requestURL = "https://graph.facebook.com/me?fields=name,link,username,picture,email"
    val response = get(requestURL)
    if(response.getCode() == 200) {
      val userData = response.getBody()
      gson.fromJson(userData,classOf[User])
    } else {
      throw new IllegalStateException(response.getBody());
    }
  }
  
  def get(requestURL: String): Response = {
    Logger.info("Facebook - GET " + requestURL)
    val req = new OAuthRequest(Verb.GET, requestURL)
    oauthService.signRequest(accessToken, req)
    req.send()
  }
}

