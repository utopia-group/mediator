package edu.utexas.mediator.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileUtil {

  public static <T> T fromJson(String filename, Class<T> classOfT) {
    Gson json = new GsonBuilder().create();
    try (Reader reader = new BufferedReader(new FileReader(filename))) {
      return json.fromJson(reader, classOfT);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("File reading error");
    }
  }

}
