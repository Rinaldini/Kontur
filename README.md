Файлы для тестового задания.

Класс Converter {

  main() // запуск http-сервера
  
  парсер CSV();
  
  getCodeResponse(); // код ответа
  
  getBodyResponse(); // тело ответа
  
  Класс Handler{
    - парсер JSON;
    - условная логика:
     - если from = " ";
     - если from&to = "x";
     - если from&to = "1/x";
     - если from&to = "x/y";
     - если from&to = "xyz/ab";
  }
}
