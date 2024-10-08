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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.BinaryObject;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import com.rawlabs.utils.sources.api.Encoding;
import java.nio.charset.Charset;

@NodeInfo(shortName = "String.Encode")
@NodeChild(value = "string")
@NodeChild(value = "encoding")
public abstract class StringEncodeNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object stringEncode(String string, String encodingName) {
    if (Encoding.fromEncodingString(encodingName).isRight()) {
      Charset charset = Encoding.fromEncodingString(encodingName).right().get().charset();
      return new BinaryObject(string.getBytes(charset));
    } else {
      return new ErrorObject(Encoding.fromEncodingString(encodingName).left().get());
    }
  }
}
