/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.apollo.web

import org.apache.activemq.apollo.util.ClassFinder
import resources.BrokerResource
import collection.immutable.TreeMap

trait WebModule {
  def priority:Int
  def web_resources: Map[String, ()=>AnyRef]
  def root_redirect:String
}

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object WebModule {

  val finder = new ClassFinder[WebModule]("META-INF/services/org.apache.activemq.apollo/web-module.index",classOf[WebModule])

  val (root_redirect, web_resources) = {

    // sort by priority.  Highest priority wins.
    val sorted = TreeMap(finder.singletons.map(x=> x.priority -> x): _*).values
    val web_resources = sorted.foldLeft(Map[String, ()=>AnyRef]()) { case (map, provider) =>
      map ++ provider.web_resources
    }
    (sorted.last.root_redirect, web_resources)
  }


}

object DefaultWebModule extends WebModule {

  def priority: Int = 100

  def create_broker_resource() = new BrokerResource
  override def web_resources = Map("broker" -> create_broker_resource _ )

  def root_redirect: String = "broker"

}