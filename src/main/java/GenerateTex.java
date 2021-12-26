import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class GenerateTex {

    private final static String dirPath = System.getProperty("user.dir");

    private static String readFromInputFile(JSONObject obj) throws IOException {
        String fileName = dirPath + "/src/main/resources/Title.tex";
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        StringBuilder texFile = new StringBuilder();
        while((line = br.readLine()) != null){
            line = parseTexLine(line, obj);
            texFile.append(line);
            texFile.append(System.getProperty("line.separator"));
        }
        br.close();
        return texFile.toString();
    }

    private static void writeOutputFile(String text, String fileName){
        Path path = Paths.get(dirPath + "/src/main/resources/out/" + fileName);
        byte[] data = text.getBytes();
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String parseTexLine(String line, JSONObject obj) {
        String[] strArr = line.split(" % ");
        String text = "";
        if (strArr.length > 1) {
            switch (strArr[1]) {
                case "Институт": text = obj.getString("institute"); break;
                case "Высшая школа": text = obj.getString("higherSchool"); break;
                case "Вид работы": text = obj.getString("templateType"); break;
                case "Номер работы": text = String.valueOf(obj.getInt("templateNumber")); break;
                case "Дисцплина": text = obj.getString("discipline"); break;
                case "Тема есть?": text = obj.has("templateTopic")? strArr[0] : "%" + strArr[0]; break; // Условие если тема есть, то добавляем тему, иначе комментим строку
                case "Тема": text = obj.has("templateTopic")? obj.getString("templateTopic") : "%" + strArr[0]; break; // То же условие, если есть вставляем название темы
                case "Вариант есть?": text = obj.has("variant")? strArr[0] : "% " + strArr[0]; break; // Аналогично с темой
                case "Вариант": text = obj.has("variant")? String.valueOf(obj.getInt("variant")) : "%" + strArr[0]; break;
                case "Студент": text = obj.getString("studentName");  break;
                case "Группа": text = obj.getString("teacherName"); break;
                case "Преподаватель": text = obj.getString("groupNumber"); break;
                case "Год": text = String.valueOf(obj.getInt("year")); break;
                default: text = strArr[0];
            }
            return text + " % " + strArr[1];
        }
        else {
            return line;
        }
    }


    private static void toPdf(String fileName) {
        try {
            String name = fileName.split(".tex")[0];
            File file = new File("/src/main/resources/out/" + name);
            file.delete();
            ProcessBuilder pb = new ProcessBuilder(
                    "pdflatex", dirPath + "/src/main/resources/out/" + fileName)
                    .inheritIO()
                    .directory(new File(dirPath + "/src/main/resources/out"));
            Process process = pb.start();
            process.waitFor();
        }
         catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generateDocument(String data) throws IOException {
        JSONObject obj = new JSONObject(data);

        String fileName = obj.getString("studentName"); // Думаю файл должен называться именем и фамилией человека
        String text = readFromInputFile(obj);
        writeOutputFile(text, fileName);
        toPdf(fileName);
    }

    public static void main(String[] args) throws IOException {
        String data = "{"
                + "\"institute\": Название института,"
                + "\"higherSchool\": Высшая школа,"
                + "\"templateType\": Вид работы,"
                + "\"templateNumber\": 2,"
                + "\"discipline\": Дисциплина,"
                + "\"templateTopic\": Тема,"
                + "\"variant\": 1,"
                + "\"studentName\": Студент,"
                + "\"teacherName\": Преподаватель,"
                + "\"groupNumber\": Группа,"
                + "\"year\": 2021 }"; // Пример строки котоая подается на вход методу generateDocument()
        generateDocument(data);
    }
}
