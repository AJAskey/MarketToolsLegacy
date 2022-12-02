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

package net.ajaskey.market.tools.quandl;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.ajaskey.common.DateTime;
import net.ajaskey.common.Utils;
import net.ajaskey.market.optuma.OptumaCommon;
import net.ajaskey.market.optuma.PriceData;

public class ProcessEIA {

  private static DocumentBuilderFactory dbFactory = null;

  private static DocumentBuilder  dBuilder = null;
  private static SimpleDateFormat sdf      = new SimpleDateFormat("yyyyMMdd");

  /**
   * net.ajaskey.market.tools.main
   *
   * @param args
   * @throws ParserConfigurationException
   */
  public static void main(final String[] args) throws ParserConfigurationException {

    final String apiKey = "5083132038aeb07288f19e6313b85532";

    // String outName = "C:/Users/ajask_000/Documents/Market Analyst 8/CSV
    // Data/EIA/.csv";
    final String gasURL = "http://api.eia.gov/series/?api_key=" + apiKey + "&series_id=PET.WGFUPUS2.W&out=xml";
    final String keroURL = "http://api.eia.gov/series/?api_key=" + apiKey + "&series_id=PET.WKJUPUS2.W&out=xml";

    ProcessEIA.dbFactory = DocumentBuilderFactory.newInstance();

    final List<PriceData> gas = ProcessEIA.getData(gasURL);
    ProcessEIA.writeList(gas, "gasoline_demand");

    final List<PriceData> kero = ProcessEIA.getData(keroURL);
    ProcessEIA.writeList(kero, "kerosene_demand");

  }

  private static List<PriceData> getData(final String url) {

    final List<PriceData> ret = new ArrayList<>();

    String resp;
    try {
      ProcessEIA.dBuilder = ProcessEIA.dbFactory.newDocumentBuilder();

      System.out.println("Processing : " + url);

      resp = Utils.getFromUrl(url);
      // System.out.println(resp);

      final Document doc = ProcessEIA.dBuilder.parse(new InputSource(new StringReader(resp)));

      doc.getDocumentElement().normalize();

      final NodeList nResp = doc.getElementsByTagName("row");
      for (int knt = 0; knt < nResp.getLength(); knt++) {
        final Node nodeResp = nResp.item(knt);
        if (nodeResp.getNodeType() == Node.ELEMENT_NODE) {
          final NodeList nrList = nodeResp.getChildNodes();
          // Calendar cal = null;
          DateTime dt = null;
          for (int cnt = 0; cnt < nrList.getLength(); cnt++) {
            final Node nr = nrList.item(cnt);
            if (nr.getNodeType() == Node.ELEMENT_NODE) {
              final String s = nr.getNodeName();
              if (s.contains("date")) {
                // System.out.println(nr.getNodeName() + " " + nr.getTextContent());
                final Date date = ProcessEIA.sdf.parse(nr.getTextContent().trim());
                // cal = Calendar.getInstance();
                // cal.setTime(date);
                dt = new DateTime(date);

              }
              else if (s.contains("value")) {
                // System.out.println(nr.getNodeName() + " " + nr.getTextContent());
                if (dt != null) {
                  final double c = Double.parseDouble(nr.getTextContent().trim());
                  final PriceData d = new PriceData(dt, c, c, c, c, 0);
                  dt = null;
                  ret.add(d);
                  // System.out.println(d.toShortString());
                }
              }
            }
          }
        }
      }

    }
    catch (final Exception e) {
      ret.clear();
      e.printStackTrace();
    }

    return ret;
  }

  private static void writeList(final List<PriceData> list, final String fname) {

    Collections.reverse(list);
    try (PrintWriter pw = new PrintWriter(OptumaCommon.optumaPath + "\\Quandl\\" + fname + ".csv")) {
      for (final PriceData price : list) {

        pw.printf("%s,%.2f%n", price.date.format("yyyy-MM-dd"), price.close);
      }
      System.out.println(list.get(list.size() - 1).date);

    }
    catch (final FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
