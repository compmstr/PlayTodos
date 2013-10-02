package util

import play.api.mvc.{Session}

object Utils {
	def strToInt(s: String): Option[Int] = {
		try{
			Some(s.toInt)
		}catch{
			case e: Throwable => None
		}
	}

	def sessionUserId(session: Session): Int = {
		strToInt(session.get("userId").getOrElse("-1")).getOrElse(-1)
	}
}
	
