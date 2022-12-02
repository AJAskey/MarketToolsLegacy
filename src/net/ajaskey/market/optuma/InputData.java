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
package net.ajaskey.market.optuma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ajaskey.common.Utils;

public class InputData {

  /**
   * net.ajaskey.market.optuma.main
   *
   * @param args
   */
  public static void main(final String[] args) {

    final List<String> dNames = new ArrayList<>();
    final List<String> ext = new ArrayList<>();
    dNames.add("C:\\temp\\redline-master");
    ext.add(".txt");
    ext.add(".xml");
    ext.add(".sh");
    final InputData id = new InputData(dNames, ext);

    for (final File f : id.files) {
      System.out.println(f.getAbsolutePath() + "   " + Utils.getFileBaseName(f));
    }

  }

  private final List<File> files;

  /**
   * This method serves as a constructor for the class.
   *
   */
  public InputData(final List<String> paths, final List<String> exts) {

    final String[] arr = exts.toArray(new String[exts.size()]);

    this.files = new ArrayList<>();

    for (final String s : paths) {
      final List<File> fil = Utils.getDirTree(s, arr);
      for (final File f : fil) {
        this.files.add(f);
      }
    }
  }

  public TickerData read(final String tkr) throws FileNotFoundException, IOException {

    final List<String> data = new ArrayList<>();
    TickerData td = null;

    final File f = this.getDataFile("");
    if (f != null) {
      try (BufferedReader reader = new BufferedReader(new FileReader(f))) {

        String line;
        while ((line = reader.readLine()) != null) {
          if (line != null) {
            data.add(line.trim());
          }
        }
      }
      Collections.reverse(data);

      td = new TickerData(tkr, data);

    }

    return td;

  }

  private File getDataFile(final String tkr) {

    for (final File f : this.files) {
      final String name = Utils.getFileBaseName(f);
      if (name.trim().equalsIgnoreCase(tkr.trim())) {
        return f;
      }
    }
    return null;
  }

}
