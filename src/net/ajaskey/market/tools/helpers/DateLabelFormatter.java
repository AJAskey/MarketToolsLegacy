/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Original author : Andy Askey (ajaskey34@gmail.com)
 */

package net.ajaskey.market.tools.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class DateLabelFormatter extends AbstractFormatter {

  /**
   *
   */
  private static final long      serialVersionUID = 1L;
  private final SimpleDateFormat dateFormatter    = new SimpleDateFormat(this.datePattern);

  private final String datePattern = "yyyy-MM-dd";

  @Override
  public Object stringToValue(final String text) throws ParseException {

    return this.dateFormatter.parseObject(text);
  }

  @Override
  public String valueToString(final Object value) throws ParseException {

    if (value != null) {
      final Calendar cal = (Calendar) value;
      return this.dateFormatter.format(cal.getTime());
    }

    return "";
  }

}
