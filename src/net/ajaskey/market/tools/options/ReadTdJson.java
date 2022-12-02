package net.ajaskey.market.tools.options;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadTdJson {

  public static void main(String[] args) {
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    try (FileReader reader = new FileReader("data/SPY-20210416.json")) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      JSONObject optObject = (JSONObject) obj;

      String sym = (String) optObject.get("symbol");
      System.out.println(sym);

      String status = (String) optObject.get("status");
      System.out.println(status);

      JSONArray putList = (JSONArray) optObject.get("putExpDateMap");
      System.out.println(putList);

      JSONObject callObject = (JSONObject) optObject.get("callExpDateMap");
      JSONArray callList = (JSONArray) callObject.get("callExpDateMap");

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

  }

}
