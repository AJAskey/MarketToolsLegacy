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
package net.ajaskey.market.tools.SIP.BigDB;

/**
 * This enumeration contain the follow types:
 *
 * <p>
 * <i>NONE</i> - Default type. No processing assigned to this state.
 * </p>
 * <p>
 * <b>BIG_BINARY</b> - Used to process large binary files for each quarter of
 * SIP and DB data (both input and output).
 * </p>
 * <b>BINARY</b> - Used to process small individual binary files of each company
 * for each year and quarter of data (both input and output).
 * <p>
 * <b>TEXT</b> - Used to process small individual text files of each company for
 * each year and quarter of data (both input and output).
 * </p>
 * <p>
 * Text files can be Gzipped to save space. The reader will process text files
 * with .gz extension.
 * </p>
 * <p>
 * Binary files contain preprocessed data so that much of read time processing
 * is skipped. The <b>BIG_BINARY</b> option is the most efficient.
 * </p>
 *
 */
public enum FiletypeEnum {

  BIG_BINARY, BINARY, NONE, TEXT;

}
