/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2.tests.regressions

import raw.creds.RDBMSTestCreds
import raw.compiler.rql2.tests.CompilerTestContext

trait RD3084Test extends CompilerTestContext with RDBMSTestCreds {

  rdbms(authorizedUser, "mysql-test", mysqlCreds)
  rdbms(authorizedUser, "postgres-test", pgsqlCreds)
  rdbms(authorizedUser, "oracle-test", oracleCreds)
  rdbms(authorizedUser, "mssql-test", sqlServerCreds)

  test("""MySQL.InferAndQuery("mysql-test", "select * from test_types")""") {
    _ should run
  }

  // The field char1 is not the second in the table.
  // this would not work before (fields have be in the order)
  test("""MySQL.Query("mysql-test", "select * from test_types",
    |   type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should evaluateTo("""[{integer1: 1, char1: "string"}]""")
  }

  // switching the order of the fields
  test("""MySQL.Query("mysql-test", "select * from test_types",
    |   type collection(record(char1: string, integer1: int)))""".stripMargin) {
    _ should evaluateTo("""[{integer1: 1, char1: "string"}]""")
  }

  test("""PostgreSQL.InferAndQuery("postgres-test", "select * from rdbmstest.test_types")""") {
    _ should run
  }

  test("""PostgreSQL.Query("postgres-test", "select * from rdbmstest.test_types",
    |           type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should run
  }

  test("""Oracle.InferAndQuery("oracle-test", "select * from rawtest.test_types")""") { it =>
    assume(language != "rql2-truffle")
    it should run
  }

  test("""Oracle.Query("oracle-test", "select * from rawtest.test_types",
    |       type collection(record(integer1: int, char1: string)))""".stripMargin) { it =>
    assume(language != "rql2-truffle")
    it should run
  }

  test("""SQLServer.InferAndQuery("mssql-test", "select * from rdbmstest.test_types")""") {
    _ should run
  }

  test("""SQLServer.Query("mssql-test", "select * from rdbmstest.test_types",
    |       type collection(record(integer1: int, char1: string)))""".stripMargin) {
    _ should run
  }

}
