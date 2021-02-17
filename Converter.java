package ru.gnkoshelev.kontur.intern;

/**
 *  омпил€ци€ кода и его исполнение c использованием Java 11.
 * —борка сервиса при помощи Apache Maven командой mvn package.
 * —ервис должен собиратьс€ в fat jar, т.е. все зависимости должны
 * быть упакованы внутрь одного jar.
 * «апуск сервиса осуществл€етс€ командой java -jar universal-converter-1.0.0.jar /path/to/file.csv, где /path/to/file.csv Ц путь до файла с правилами
 * конвертации.
 * —ервис должен принимать HTTP-запросы на стандартном порту (80).
 * »сходный код соответствует Java Code Conventions и Google Java Style Guide.
 * —ервис должен предоставл€ть один метод POST /convert с JSON в теле запроса:
 * 
 * {
 *  "from": "<выражение в исходных единицах измерени€>",
 *  "to": "<выражение в единицах измерени€, которые необходимо получить>"
 * }
 *
 */


public class Converter {
  String s, t;
  //double Value;
  public void setFrom(String sFrom) {
    s = sFrom;
  }

  public void setTo(String sTo) {
    t = sTo;
  }

  public void getFrom() {
    return s;
  }

  public void getTo() {
    return t;
  }

  public static void main( String[] args) {
    System.out.println( "Hello World!" );
  }

  public static boolean checkUnits(String sFrom, String sTo) {
    // method
  }

  public static boolean checkExpression(String sFrom, String sTo) {
    // method
  }

  public static String sendMessage() {
    // method
  }
}
