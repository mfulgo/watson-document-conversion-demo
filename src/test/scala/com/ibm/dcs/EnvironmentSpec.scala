/** Copyright 2015 IBM Corp. All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
  * in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the License
  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  * or implied. See the License for the specific language governing permissions and limitations under
  * the License.
  */
package com.ibm.dcs

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}

class EnvironmentSpec extends WordSpec with Matchers {

  "The Environment" should {
    "default to the first credentials in vcap_services" in {
      val conf = ConfigFactory.parseString(
        "vcap_services = { document-conversion: [{ credentials: { username: foo, password: bar } }] }")
      val env = new Environment(conf)
      env.getUsername should be("foo")
      env.getPassword should be("bar")
    }

    "use specified credentials in vcap_services" in {
      val conf = ConfigFactory.parseString(
        """vcap_services = { document-conversion: [
          |  { name: first, credentials: { username: foo1, password: bar1 } }
          |  { name: second, credentials: { username: foo2, password: bar2 } }
          |]}
          |dcs.vcap_credentials_name = second
        """.stripMargin)
      val env = new Environment(conf)
      env.getUsername should be("foo2")
      env.getPassword should be("bar2")
    }

    "allow manual specification of credentials" in {
      val confs = ConfigFactory.parseString(
        """vcap_services = { document-conversion: [
          |  { name: first, credentials: { username: badU1, password: badP1 } }
          |  { name: second, credentials: { username: badU2, password: badP2 } }
          |]}
          |dcs: {
          |  vcap_credentials_name = second
          |  username: foo
          |  password: bar
          |}
        """.stripMargin) :: ConfigFactory.parseString(
        """vcap_services = { document-conversion: [
          |  { name: first, credentials: { username: badU1, password: badP1 } }
          |  { name: second, credentials: { username: badU2, password: badP2 } }
          |]}
          |dcs: {
          |  vcap_credentials_name = missing
          |  username: foo
          |  password: bar
          |}
        """.stripMargin) :: Nil
      confs foreach { conf =>
        val env = new Environment(conf)
        env.getUsername should be("foo")
        env.getPassword should be("bar")
      }
    }

    "throw exceptions if no credentials are found" in {
      val conf = ConfigFactory.parseString(
        """vcap_services = { document-conversion: [
          |  { name: first, credentials: { username: badU1, password: badP1 } }
          |]}
          |dcs: {
          |  vcap_credentials_name = missing
          |}
        """.stripMargin)
      val env = new Environment(conf)
      an[IllegalStateException] should be thrownBy env.getUsername
      an[IllegalStateException] should be thrownBy env.getPassword
    }
  }
}
