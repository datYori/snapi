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

package com.rawlabs.sql.compiler.antlr4

import com.rawlabs.compiler.Message
import com.rawlabs.sql.parser.generated.{PsqlLexer, PsqlParser}
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{Positions, StringSource}

import scala.collection.mutable

class SqlSyntaxAnalyzer(val positions: Positions) extends Parsers(positions) {

  def parse(s: String): ParseProgramResult = {
    val source = StringSource(s)
    val rawErrorListener = new SqlErrorListener()

    val striped = s.stripTrailing()
    val lexer = new PsqlLexer(CharStreams.fromString(striped))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new PsqlParser(new CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.prog
    val visitorParseErrors = new SqlVisitorParseErrors
    val params = mutable.Map.empty[String, SqlParam]
    val visitor = new SqlVisitor(positions, params, source, visitorParseErrors)
    val result = visitor.visit(tree).asInstanceOf[SqlProgramNode]
    val totalErrors = rawErrorListener.getErrors ++ visitorParseErrors.getErrors
    ParseProgramResult(totalErrors, params, visitor.returnDescription, result, positions)
  }

}

final case class ParseProgramResult(
    errors: List[Message],
    params: mutable.Map[String, SqlParam],
    returnDescription: Option[String],
    tree: SqlBaseNode,
    positions: Positions
) {
  def hasErrors: Boolean = errors.nonEmpty

  def isSuccess: Boolean = errors.isEmpty
}
