package ru.gnkoshelev.kontur.intern;

/**
 * ���������� ���� � ��� ���������� c �������������� Java 11.
 * ������ ������� ��� ������ Apache Maven �������� mvn package.
 * ������ ������ ���������� � fat jar, �.�. ��� ����������� ������
 * ���� ��������� ������ ������ jar.
 * ������ ������� �������������� �������� java -jar universal-converter-1.0.0.jar /path/to/file.csv, ��� /path/to/file.csv � ���� �� ����� � ���������
 * �����������.
 * ������ ������ ��������� HTTP-������� �� ����������� ����� (80).
 * �������� ��� ������������� Java Code Conventions � Google Java Style Guide.
 * ������ ������ ������������� ���� ����� POST /convert � JSON � ���� �������:
 * 
 * {
 *  "from": "<��������� � �������� �������� ���������>",
 *  "to": "<��������� � �������� ���������, ������� ���������� ��������>"
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
