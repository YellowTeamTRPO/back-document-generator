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
        fileName = "newTitle.tex"; // Думаю файл должен называться именем и фамилией человека
        Path path = Paths.get("src/res/out/" + fileName);
        byte[] data = text.getBytes();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }


    private static String parseTexLine(String line) {
        String[] strArr = line.split(" % ");
        String text = "";
        if (strArr.length > 1) {
            text = switch (strArr[1]) {
                case "Институт" -> "strArr[0]";
                case "Высшая школа" -> "strArr[0]";
                case "Вид работы" -> "strArr[0]";
                case "Дисцплина" -> "strArr[0]";
                case "Тема" -> false? "%" + strArr[0] : "strArr[0]";
                case "Вариант" -> false? "%" + strArr[0] : "strArr[0]";
                case "Студент" ->  "strArr[0]";
                case "Группа" -> "strArr[0]";
                case "Преподаватель" -> "strArr[0]";
                case "Год" -> "strArr[0]";
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
            ProcessBuilder pb = new ProcessBuilder(
                    "pdflatex", dirPath + "/src/res/out/newTitle.tex")
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
            String text = readFromInputFile();
            writeOutputFile(text, "");
            toPdf("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
