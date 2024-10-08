/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.sql.compiler

import java.sql.{
  Blob,
  CallableStatement,
  Clob,
  Connection,
  DatabaseMetaData,
  NClob,
  PreparedStatement,
  SQLWarning,
  SQLXML,
  Savepoint,
  Statement,
  Struct
}
import java.util.Properties
import java.util.concurrent.Executor

class SqlConnection(connectionPool: SqlConnectionPool, conn: Connection) extends java.sql.Connection {

  override def close(): Unit = {
    if (isClosed) {
      // If the connection closed in the meantime (e.g. due to a crash), we cannot release it back to the pool.
      connectionPool.actuallyRemoveConnection(this)
    } else {
      // If the connection seems "sane", then we do not ACTUALLY close the connection.
      // Instead, we just release the borrow.
      connectionPool.releaseConnection(
        this,
        isAlive =
          false //  We are not sure if the connection is alive or not, e.g. it could be closed because it failed.
      )
    }
  }

  // This is called by the connection pool when we *actually* want to close the connection.
  def actuallyClose(): Unit = {
    conn.close()
  }

  override def createStatement(): Statement = conn.createStatement()

  override def prepareStatement(sql: String): PreparedStatement = conn.prepareStatement(sql)

  override def prepareCall(sql: String): CallableStatement = conn.prepareCall(sql)

  override def nativeSQL(sql: String): String = conn.nativeSQL(sql)

  override def setAutoCommit(autoCommit: Boolean): Unit = conn.setAutoCommit(autoCommit)

  override def getAutoCommit: Boolean = conn.getAutoCommit

  override def commit(): Unit = conn.commit()

  override def rollback(): Unit = conn.rollback()

  override def isClosed: Boolean = conn.isClosed

  override def getMetaData: DatabaseMetaData = conn.getMetaData

  override def setReadOnly(readOnly: Boolean): Unit = conn.setReadOnly(readOnly)

  override def isReadOnly: Boolean = conn.isReadOnly

  override def setCatalog(catalog: String): Unit = conn.setCatalog(catalog)

  override def getCatalog: String = conn.getCatalog

  override def setTransactionIsolation(level: Int): Unit = conn.setTransactionIsolation(level)

  override def getTransactionIsolation: Int = conn.getTransactionIsolation

  override def getWarnings: SQLWarning = conn.getWarnings

  override def clearWarnings(): Unit = conn.clearWarnings()

  override def createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement =
    conn.createStatement(resultSetType, resultSetConcurrency)

  override def prepareStatement(sql: String, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement =
    conn.prepareStatement(sql, resultSetType, resultSetConcurrency)

  override def prepareCall(sql: String, resultSetType: Int, resultSetConcurrency: Int): CallableStatement =
    conn.prepareCall(sql, resultSetType, resultSetConcurrency)

  override def getTypeMap: java.util.Map[String, Class[_]] = conn.getTypeMap

  override def setTypeMap(map: java.util.Map[String, Class[_]]): Unit = conn.setTypeMap(map)

  override def setHoldability(holdability: Int): Unit = conn.setHoldability(holdability)

  override def getHoldability: Int = conn.getHoldability

  override def setSavepoint(): Savepoint = conn.setSavepoint()

  override def setSavepoint(name: String): Savepoint = conn.setSavepoint(name)

  override def rollback(savepoint: Savepoint): Unit = conn.rollback(savepoint)

  override def releaseSavepoint(savepoint: Savepoint): Unit = conn.releaseSavepoint(savepoint)

  override def createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement =
    conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)

  override def prepareStatement(
      sql: String,
      resultSetType: Int,
      resultSetConcurrency: Int,
      resultSetHoldability: Int
  ): PreparedStatement = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)

  override def prepareCall(
      sql: String,
      resultSetType: Int,
      resultSetConcurrency: Int,
      resultSetHoldability: Int
  ): CallableStatement = conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)

  override def prepareStatement(sql: String, autoGeneratedKeys: Int): PreparedStatement =
    conn.prepareStatement(sql, autoGeneratedKeys)

  override def prepareStatement(sql: String, columnIndexes: Array[Int]): PreparedStatement =
    conn.prepareStatement(sql, columnIndexes)

  override def prepareStatement(sql: String, columnNames: Array[String]): PreparedStatement =
    conn.prepareStatement(sql, columnNames)

  override def createClob(): Clob = conn.createClob()

  override def createBlob(): Blob = conn.createBlob()

  override def createNClob(): NClob = conn.createNClob()

  override def createSQLXML(): SQLXML = conn.createSQLXML()

  override def isValid(timeout: Int): Boolean = conn.isValid(timeout)

  override def setClientInfo(name: String, value: String): Unit = conn.setClientInfo(name, value)

  override def setClientInfo(properties: Properties): Unit = conn.setClientInfo(properties)

  override def getClientInfo(name: String): String = conn.getClientInfo(name)

  override def getClientInfo: Properties = conn.getClientInfo

  override def createArrayOf(typeName: String, elements: Array[AnyRef]): java.sql.Array =
    conn.createArrayOf(typeName, elements)

  override def createStruct(typeName: String, attributes: Array[AnyRef]): Struct =
    conn.createStruct(typeName, attributes)

  override def setSchema(schema: String): Unit = conn.setSchema(schema)

  override def getSchema: String = conn.getSchema

  override def abort(executor: Executor): Unit = conn.abort(executor)

  override def setNetworkTimeout(executor: Executor, milliseconds: Int): Unit =
    conn.setNetworkTimeout(executor, milliseconds)

  override def getNetworkTimeout: Int = conn.getNetworkTimeout

  override def unwrap[T](iface: Class[T]): T = conn.unwrap(iface)

  override def isWrapperFor(iface: Class[_]): Boolean = conn.isWrapperFor(iface)
}
