package ru.gnkoshelev.kontur.intern;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Converter {
 
  // заменить на args[0]
  private static final String filePath = "C:/Mydocs/Java/universal-converter/src/main/resources/JSON.json";
  public static String to, from;

  public static void main(String[] args) {

    // считывание файла с JSON данными и последующий разбор 
    try {
      FileReader reader = new FileReader(filePath);
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

      // считывание файла с JSON данными
      from = ((String) jsonObject.get("from")).replaceAll(" ","");
      System.out.println("The FROM is: " + from);
      to = ((String) jsonObject.get("to")).replaceAll(" ","");
      System.out.println("The TO is: " + to);

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ParseException ex) {
      ex.printStackTrace();
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }

    System.out.println("The TO is: " + to + " and FROM is: " + from);

    

  }
}
