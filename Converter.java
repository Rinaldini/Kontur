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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.opencsv.CSVReader;

public class Converter {

  public static final String file = "D:\\Mydoc\\IT\\converter\\src\\main\\resources\\file.csv"; //  заменить на args[0]
  public static String to, from;
  public static String response;
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
   * считываем данные из файла *.csv (по единицам измерения возвращаем числовое правило конвертации)
   * @param csvFile, s, t
   * @return value
   */

  public static Double readData(String csvFile, String s, String t) {
    double value = 0.0;
    try {
      reader = new FileReader(csvFile);
      CSVReader csvReader = new CSVReader(reader);

      String[] nextLine;
      while ((nextLine = csvReader.readNext()) != null) {
        if ((s.equals(nextLine[0])) & (t.equals(nextLine[1]))) {
          value = Double.parseDouble(nextLine[2]);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return value;
  }

  public static int getCodeResponse(double valueConvert) {
    if (valueConvert != 0.0) {
      codeResponse = 200;
    } else {
      codeResponse = 404;
    }
    return codeResponse;
  }

  public static String getBodyResponse(double valueConvert) {
    DecimalFormat decimalFormat = new DecimalFormat( "#.###############" );
    return decimalFormat.format(valueConvert);
  }

  public static class ConvertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

      if (exchange.getRequestMethod().equals("POST")) {
        try {
          byte[] buffer = new byte[1024];
          int length;
          while ((length = exchange.getRequestBody().read(buffer)) != -1) {
            result.write(buffer, 0, length);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        // считывание потока с JSON данными и последующий разбор
        try {
          JSONParser jsonParser = new JSONParser();
          JSONObject jsonObject = (JSONObject) jsonParser.parse(result.toString(StandardCharsets.UTF_8));
          from = ((String) jsonObject.get("from")).replaceAll(" ", "");
          to = ((String) jsonObject.get("to")).replaceAll(" ", "");
          result.reset();
          result.close();
        } catch (ParseException | NullPointerException e) {
          e.printStackTrace();
        }
      }

      if (from.equals("")) {
        codeResponse = 400;
        response = "400";
      }

      // если  "from" и "to" вида  "x"
      else if (Pattern.matches("[А-я]+", from)) {
        if (Pattern.matches("[А-я]+", to)) {
          if (from.equals(to)) {
            codeResponse = getCodeResponse(1);
            response = getBodyResponse(1);
          } else {
            readData(file, from, to);
            codeResponse = getCodeResponse(valueConvert);
            response = getBodyResponse(valueConvert);
          }
        } else {
          codeResponse = 404;
          response = "404";
        }
      }

      // если  "from" и "to" вида  "1/x"
      else if (Pattern.matches("1/[А-я]+", from)) {
        from = from.substring(2);
        if (Pattern.matches("1/[А-я]+", to)) {
          to = to.substring(2);
          if (from.equals(to)) {
            codeResponse = getCodeResponse(1);
            response = getBodyResponse(1);
          } else {
            valueConvert = readData(file, to, from);
            codeResponse = getCodeResponse(valueConvert);
            response = getBodyResponse(valueConvert);
          }
        } else {
          codeResponse = 404;
          response = "404";
        }
      }

      // если  "from" и "to" вида  "x/y"
      else if (Pattern.matches("[А-я]+/[А-я]+", from)) {
        String[] arrayFrom = from.split("/");
        System.out.println(); // удалить перед сдачей
        if (Pattern.matches("[А-я]+/[А-я]+", to)) {
          String[] arrayTo = to.split("/");
          double[] arrayValueConvert = {0.0, 0.0};
          for (int i = 0; i < 2; i++) {
            arrayValueConvert[i] = readData(file, arrayFrom[i], arrayTo[i]);
          }
          valueConvert = arrayValueConvert[0] / arrayValueConvert[1];
          codeResponse = getCodeResponse(valueConvert);
          response = getBodyResponse(valueConvert);
        } else {
          codeResponse = 404;
          response = "404";
        }
      }

      // если  "from" и "to" вида  "xyz/ab"
      // arrayFrom1 = xyz , arrayFrom2 = ab. Аналогично arrayTo1 arrayTo2
      else if (Pattern.matches("[[А-я]+\\*]+[А-я]+/[[А-я]+\\*]+[А-я]+", from)) {
        String[] arrayFrom1 = (from.split("/")[0]).split("\\*");
        String[] arrayFrom2 = (from.split("/")[1]).split("\\*");
        if (Pattern.matches("[[А-я]+\\*]+[А-я]+/[[А-я]+\\*]+[А-я]+", to)) {
          String[] arrayTo1 = (to.split("/")[0]).split("\\*");
          String[] arrayTo2 = (to.split("/")[1]).split("\\*");

          if ((Arrays.equals(arrayFrom1, arrayTo1)) & (Arrays.equals(arrayFrom2, arrayTo2))) {
            codeResponse = getCodeResponse(1);
            response = getBodyResponse(1);
          } else {
            // допускаем, что максимальное количество единиц измерения не более 5
            ArrayList<Double> arrayValueConvert1 = new ArrayList<>(5);
            ArrayList<Double> arrayValueConvert2 = new ArrayList<>(5);
            for (int i = 0; i < arrayFrom1.length; i++) {
              arrayValueConvert1.add(readData(file, arrayFrom1[i], arrayTo1[i]));
            }
            arrayValueConvert1.trimToSize();

            for (int i = 0; i < arrayFrom2.length; i++) {
              arrayValueConvert2.add(readData(file, arrayFrom2[i], arrayTo2[i]));
            }
            arrayValueConvert2.trimToSize();

            double valueConvertFrom1 = 1.0;
            for (double v : arrayValueConvert1) {
              valueConvertFrom1 = valueConvertFrom1 * v;
            }

            double valueConvertFrom2 = 1.0;
            for (double v : arrayValueConvert2) {
              valueConvertFrom2 = valueConvertFrom2 * v;
            }

            valueConvert = valueConvertFrom1 / valueConvertFrom2;
            codeResponse = getCodeResponse(valueConvert);
            response = getBodyResponse(valueConvert);
          }
        } else {
          codeResponse = 400;
          response = "400";
        }
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
