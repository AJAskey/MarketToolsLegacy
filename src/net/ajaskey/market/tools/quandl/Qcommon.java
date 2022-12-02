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

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.ajaskey.common.Utils;
import net.ajaskey.market.optuma.OptumaCommon;

public class Qcommon {

  public final static String outpath = OptumaCommon.optumaPath + "/quandl";

  public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  private static DocumentBuilderFactory dbFactory = null;

  private static DocumentBuilder dBuilder = null;

  /**
   *
   * net.ajaskey.market.tools.quandl.getData
   *
   * @param url
   * @param num
   * @return
   */
  public static List<CommonQuandlData> getData(final String url, final int num) {

    final List<CommonQuandlData> ret = new ArrayList<>();

    try {
      final Document doc = Qcommon.getDocument(url, false);

      Qcommon.getLatestTime(doc);

      final NodeList nResp = doc.getElementsByTagName("datum");
      for (int knt = 0; knt < nResp.getLength(); knt++) {
        final Node nodeResp = nResp.item(knt);
        if (nodeResp.getNodeType() == Node.ELEMENT_NODE) {
          final NodeList nrList = nodeResp.getChildNodes();
          final Calendar cal = Calendar.getInstance();
          int dReads = 0;
          final Double[] dd = new Double[num];
          for (int cnt = 0; cnt < nrList.getLength(); cnt++) {
            final Node nr = nrList.item(cnt);
            if (nr.getNodeType() == Node.ELEMENT_NODE) {
              // final Element eElement = (Element) nodeResp;
              // System.out.println(nr.getNodeName() + " " + nr.getTextContent());

              final Element eElement = (Element) nr;
              // System.out.println("type: " + eElement.getAttribute("type"));
              final String s = eElement.getAttribute("type");

              if (s.contains("date")) {
                // System.out.println(nr.getNodeName() + " " + nr.getTextContent());
                final Date date = Qcommon.sdf.parse(nr.getTextContent().trim());
                cal.setTime(date);

              }
              else if (s.contains("float")) {
                // System.out.println(nr.getNodeName() + " " + nr.getTextContent());
                dd[dReads++] = Double.parseDouble(nr.getTextContent().trim());
                if (dReads == dd.length) {

                  final CommonQuandlData cqd = new CommonQuandlData(cal, dd);
                  ret.add(cqd);
                  // System.out.println("Adding - " + cqd.toString());
                  dReads = 0;
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

  public static Document getDocument(final String url, final boolean debug) {

    Qcommon.dbFactory = DocumentBuilderFactory.newInstance();
    Document doc = null;

    try {
      Qcommon.dBuilder = Qcommon.dbFactory.newDocumentBuilder();

      System.out.println("Processing : " + url);

      final String resp = Utils.getFromUrl(url);
      if (debug) {
        System.out.println(resp);
      }

      doc = Qcommon.dBuilder.parse(new InputSource(new StringReader(resp)));

      doc.getDocumentElement().normalize();

    }
    catch (final ParserConfigurationException e) {
      doc = null;
      e.printStackTrace();
    }
    catch (final SAXException e) {
      e.printStackTrace();
    }
    catch (final IOException e) {
      e.printStackTrace();
    }

    return doc;

  }

  public static Calendar getLatestTime(final Document doc) {

    final Calendar latest = Calendar.getInstance();
    latest.add(Calendar.YEAR, -25);

    final NodeList nDate = doc.getElementsByTagName("newest-available-date");

    for (int knt = 0; knt < nDate.getLength(); knt++) {
      final Node nodeResp = nDate.item(knt);

      if (nodeResp.getNodeType() == Node.ELEMENT_NODE) {
        final Element eElement = (Element) nodeResp;
        System.out.println("  " + eElement.getNodeName() + " " + eElement.getTextContent());
        Date date;
        try {
          date = Qcommon.sdf.parse(eElement.getTextContent().trim());
          latest.setTime(date);
        }
        catch (DOMException | ParseException e) {
          e.printStackTrace();
        }
        break;
      }
    }

    return latest;
  }
}
