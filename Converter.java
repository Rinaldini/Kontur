package ru.gnkoshelev.kontur.intern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

//import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.opencsv.CSVReader;

public class Converter {
 
  private static final String file = "D:\\Mydoc\\IT\\converter\\src\\main\\resources\\file.csv"; //  заменить на args[0]
  public static String to, from, response;
  public static int codeResponse;
  public static FileReader reader;
  public static ByteArrayOutputStream result = new ByteArrayOutputStream();

  public static void main(String[] args) throws Exception {

    HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
    server.createContext("/convert", new ConvertHandler());
    server.setExecutor(null);
    server.start();
 
  }

 /**
  *   считываем данные из файла *.csv
  */

  public static void readData(String csvFile) {
    try {
      reader = new FileReader(csvFile);
      CSVReader csvReader = new CSVReader(reader);

      String[] nextLine;
      while ((nextLine = csvReader.readNext()) != null) {
          System.out.print(nextLine[0] +  " " + nextLine[1] + " " + nextLine[2] + "\r\n");
      }
      System.out.println(); // удалить перед сдачей
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  static class ConvertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

      if (exchange.getRequestMethod().equals("POST")) {
        response = "This is the POST response";  // удалить перед сдачей
        try {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = exchange.getRequestBody().read(buffer)) != -1) {
            result.write(buffer, 0, length);
          }
          System.out.println(result.toString(StandardCharsets.UTF_8)); // удалить перед сдачей
        } catch (Exception e) {
          e.printStackTrace();
        }
        // считывание потока с JSON данными и последующий разбор
        try {
          JSONParser jsonParser = new JSONParser();
          JSONObject jsonObject = (JSONObject) jsonParser.parse(result.toString(StandardCharsets.UTF_8));
          from = ((String) jsonObject.get("from")).replaceAll(" ","");
          to = ((String) jsonObject.get("to")).replaceAll(" ","");
          result.reset();
          result.close();
        } catch (ParseException | NullPointerException e) {
          e.printStackTrace();
        }
        System.out.println("The TO is: " + to + " and FROM is: " + from);  // удалить перед сдачей
       
      } else {
        response = "This is the GET response"; // удалить перед сдачей
      }
      //Pattern p = Pattern.compile("\\w/\\w");
      //Matcher m = p.matcher(from);

      if (from.equals("")) {
        codeResponse = 400;
      } else if (from.matches("\\w+\\s*/\\w+\\s*")) {
        codeResponse = 404;
        System.out.println("УРА!!! Видим единицы измерения: " + from);
      } else {
        codeResponse = 200;
      }


      readData(file);      
      exchange.sendResponseHeaders(codeResponse, response.length());
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.flush();
      os.close();
      reader.close();
    }
  }
}
