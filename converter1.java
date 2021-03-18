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
 
  public static final String file = "D:\\Mydoc\\IT\\converter\\src\\main\\resources\\file.csv"; //  заменить на args[0]
  public static String to, from, response;
  public static int codeResponse;
  public static FileReader reader;
  public static ByteArrayOutputStream result = new ByteArrayOutputStream();
  public static double valueConvert;

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
      }

      if (from.equals("")) {
        codeResponse = 400;
        response = String.valueOf(codeResponse);
      }

      // если  "from" и "to" вида  "x"
      else if (Pattern.matches("[А-я]+", from)) {
        if (Pattern.matches("[А-я]+", to)) {
          valueConvert = readData(file, from, to);
          if (valueConvert != 0.0) {
            codeResponse = 200;
            response = String.valueOf(valueConvert);
          } else {
            codeResponse = 404;
          }
        } else {
          codeResponse = 404;
        }
      }

      // если  "from" и "to" вида  "1/x"
      else if (Pattern.matches("1/[А-я]+", from)) {
          from = from.substring(2);
          if (Pattern.matches("1/[А-я]+", to)) {
              to = to.substring(2);
              valueConvert = readData(file, to, from);
              if (valueConvert != 0.0) {
                  codeResponse = 200;
                  response = String.valueOf(valueConvert);
              } else {
                  codeResponse = 404;
              }
          } else {
              codeResponse = 404;
          }
      }

      // если  "from" и "to" вида  "x/y"
      else if (Pattern.matches("[А-я]+/[А-я]+", from)) {
          String[] arrayFrom = from.split("/");
          System.out.println(); // удалить перед сдачей
          if (Pattern.matches("[А-я]+/[А-я]+", to)) {
              String[] arrayTo = to.split("/");
              double[] arrayValueConvert = {0.0, 0.0};
              for (int i = 0; i <2; i++) {
                  arrayValueConvert[i] = readData(file, arrayFrom[i], arrayTo[i]);
              }
              valueConvert = arrayValueConvert[0]/arrayValueConvert[1];
              if (valueConvert != 0.0) {
                  codeResponse = 200;
                  response = String.valueOf(valueConvert);
              } else {
                  codeResponse = 404;
              }
          } else {
              codeResponse = 404;
          }
      }

      else {
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
