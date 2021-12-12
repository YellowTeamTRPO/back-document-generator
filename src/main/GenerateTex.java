package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

public class GenerateTex {

    private final static String dirPath = System.getProperty("user.dir");

    private static String readFromInputFile() throws IOException {
        String fileName = "src/res/Title.tex";
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder texFile = new StringBuilder();
        while((line = br.readLine()) != null){
            line = parseTexLine(line);
            texFile.append(line);
            texFile.append(System.getProperty("line.separator"));
        }
        br.close();
        return texFile.toString();
    }

    private static void writeOutputFile(String text, String fileName){
        Path path = Paths.get("src/res/out/" + fileName);
        byte[] data = text.getBytes();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String parseTexLine(String line) {
        String[] strArr = line.split(" % ");
        String text = "";
        if (strArr.length > 1) {
            text = switch (strArr[1]) {
                case "Институт" -> "Институт";
                case "Высшая школа" -> "Высшая школа";
                case "Вид работы" -> "Вид работы";
                case "Дисцплина" -> "Дисцплина";
                case "Тема есть?" -> true? strArr[0] : "%" + strArr[0]; // Условие если тема есть, то добавляем тему, иначе комментим строку
                case "Тема" -> true? "Название темы" : "%" + strArr[0]; // То же условие, если есть вставляем название темы
                case "Вариант есть?" -> true? strArr[0] : "%" + strArr[0]; // Аналогично с темой
                case "Вариант" -> true? "Номер варианта" : "%" + strArr[0];
                case "Студент" ->  "Студент";
                case "Группа" -> "Группа";
                case "Преподаватель" -> "Преподаватель";
                case "Год" -> "Год";
                default -> strArr[0];
            };
            return text + " % " + strArr[1];
        }
        else {
            return line;
        }
    }


    private static void toPdf(String fileName) {
        try {
            String name = fileName.split(".tex")[0];
            File file = new File("/src/res/out/" + name);
            file.delete();
            ProcessBuilder pb = new ProcessBuilder(
                    "pdflatex", dirPath + "/src/res/out/" + fileName)
                    .inheritIO()
                    .directory(new File(dirPath + "/src/res/out"));
            Process process = pb.start();
            process.waitFor();
        }
         catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args)
    {
        try {
            String fileName = "newTitle.tex"; // Думаю файл должен называться именем и фамилией человека
            String text = readFromInputFile();
            writeOutputFile(text, fileName);
            toPdf(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
