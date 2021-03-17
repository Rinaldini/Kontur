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
  public  static double valueCovert;

  public static void main(String[] args) throws Exception {

    HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
    server.createContext("/convert", new ConvertHandler());
    server.setExecutor(null);
    server.start();
 
  }

 /**
  *   считываем данные из файла *.csv
  * @return value
  */

  public static Double readData(String csvFile, String s, String t) {
    double value = 0.0;
    try {
      reader = new FileReader(csvFile);
      CSVReader csvReader = new CSVReader(reader);

      String[] nextLine;
      while ((nextLine = csvReader.readNext()) != null) {
        if ((s.equals(nextLine[0]))&(t.equals(nextLine[1]))) {
          System.out.print("Совпадение! " + nextLine[0] +  " " + nextLine[1] + " " + nextLine[2] + "\r\n"); // удалить перед сдачей
          value = Double.parseDouble(nextLine[2]);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return value;
  }

  static class ConvertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

      if (exchange.getRequestMethod().equals("POST")) {
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
       
      }

      if (from.equals("")) {
        codeResponse = 400;
        response = String.valueOf(codeResponse);
      } else if (Pattern.matches("[А-я]+", from)) {
        System.out.println("1 We see from: " + from);
        if (Pattern.matches("[А-я]+", to)) {
          System.out.println("2 We see to: " + to);
          valueCovert = readData(file, to, from);
          System.out.println(valueCovert);
          if (valueCovert != 0.0) {
            codeResponse = 200;
            response = String.valueOf(valueCovert);
            System.out.println(valueCovert);
          } else {
            codeResponse = 404;
          }
        } else {
          codeResponse = 404;
        }
      } else {
        codeResponse = 200;
      }

      exchange.sendResponseHeaders(codeResponse, response.length());
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.flush();
      os.close();
      reader.close();
      response = "";
    }
  }
}
