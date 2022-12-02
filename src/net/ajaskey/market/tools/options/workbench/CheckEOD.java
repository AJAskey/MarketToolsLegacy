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
package net.ajaskey.market.tools.options.workbench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckEOD {

  public static void main(String[] args) throws IOException {

    final Option opt = new Option();

    System.out.printf("Enter code:%n%n");

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    final String code = reader.readLine();

    opt.processJson(code);

    System.out.println(opt.response.substring(0, 100));

    System.out.println(opt.optList.size());

  }

}
