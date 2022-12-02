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
package net.ajaskey.market.misc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ParseZipfile {

  /**
   * net.ajaskey.market.misc.main
   *
   * @param args
   * @throws IOException
   */
  public static void main(final String[] args) throws IOException {

    final InputStream theFile = new FileInputStream("test.zip");
    final ZipInputStream stream = new ZipInputStream(theFile);

    final byte[] buffer = new byte[2048];

    try {

      // now iterate through each item in the stream. The get next
      // entry call will return a ZipEntry for each file in the
      // stream
      ZipEntry entry;
      while ((entry = stream.getNextEntry()) != null) {
        final String s = String.format("Entry: %s len %d added %TD", entry.getName(), entry.getSize(), new Date(entry.getTime()));
        System.out.println(s);

        // Once we get the entry from the stream, the stream is
        // positioned read to read the raw data, and we keep
        // reading until read returns 0 or less.
        final String outpath = "out/" + entry.getName();
        FileOutputStream output = null;
        try {
          output = new FileOutputStream(outpath);
          int len = 0;
          while ((len = stream.read(buffer)) > 0) {
            output.write(buffer, 0, len);
          }
        }
        finally {
          // we must always close the output file
          if (output != null) {
            output.close();
          }
        }
      }
    }
    finally {
      // we must always close the zip file.
      stream.close();
    }

  }

}
