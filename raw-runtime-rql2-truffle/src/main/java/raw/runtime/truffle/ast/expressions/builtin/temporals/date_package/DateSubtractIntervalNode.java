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

package raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Date.SubtractInterval")
@NodeChild("date")
@NodeChild("interval")
public abstract class DateSubtractIntervalNode extends ExpressionNode {

  @Specialization
  protected TimestampObject addInterval(DateObject dateObj, IntervalObject intervalObj) {

    LocalDate date = dateObj.getDate();

    LocalDateTime result =
        LocalDateTime.of(date, LocalTime.of(0, 0, 0, 0))
            .minusYears(intervalObj.getYears())
            .minusMonths(intervalObj.getMonths())
            .minusWeeks(intervalObj.getWeeks())
            .minusDays(intervalObj.getDays())
            .minusHours(intervalObj.getHours())
            .minusMinutes(intervalObj.getMinutes())
            .minusSeconds(intervalObj.getSeconds())
            .minusNanos(1000000L * intervalObj.getMillis());

    return new TimestampObject(result);
  }
}
