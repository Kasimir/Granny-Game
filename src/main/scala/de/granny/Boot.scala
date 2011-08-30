package de.granny

import com.mongodb._
import net.liftweb.mongodb._

object Boot {

  def boot = {
      val srvr = new ServerAddress("127.0.0.1", 27017)
      MongoDB.defineDb(DefaultMongoIdentifier, new Mongo(srvr), "granny")
      MongoDB.use(DefaultMongoIdentifier) { db =>
        val coll = db.getCollection("gamenodedocs")
        coll.drop()
        coll.ensureIndex("stones")
    }

  }
}