package com.zzheads.Downloader;

import com.zzheads.Downloader.model.Downloader;
import com.zzheads.Downloader.model.Task;
import com.zzheads.Downloader.model.TaskFile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by zzheads on 18.11.16.
 */

//Консольная утилита для скачивания файлов по HTTP протоколу.
//
//        Входные параметры:
//
//        -n количество одновременно качающих потоков (1,2,3,4....)
//        -l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
//        -f путь к файлу со списком ссылок
//        -o имя папки, куда складывать скачанные файлы
//        Формат файла со ссылками:
//
//<HTTP ссылка><пробел><имя файла, под которым его надо сохранить>
//        пример:
//
//        http://example.com/archive.zip my_archive.zip
//        http://example.com/image.jpg picture.jpg
//        ......
//        В HTTP ссылке нет пробелов, нет encoded символов и прочей ерунды - это всегда обычные ссылки с английскими символами без специальных символов в именах файлов и прочее. Короче - ссылкам можно не делать decode. Ссылки без авторизации, не HTTPS/FTP - всегда только HTTP-протокол.
//
//        Ссылки могут повторяться в файле, но с разными именами для сохранения, например:
//
//        http://example.com/archive.zip first_archive.zip
//        http://example.com/archive.zip second_archive.zip
//        Одинаковые ссылки - это нормальная ситуация, хорошо бы ее учитывать.
//
//        В конце работы утилита должна выводить статистику - время работы и количество скачанных байт.
//
//        Утилита должна быть написана на Java (версия 7 или выше, по желанию) или Kotlin. Для сборки проекта необходимо использовать ant/gradle.
//
//        Пример вызова:
//
//        java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt
//        По всем вопросам смело писать на vgv@ecwid.com



public class Application {

    public static String ERROR_MESSAGE = "Входные параметры:\n" +
            "\n" +
            "  -n количество одновременно качающих потоков (1,2,3,4....)\n" +
            "  -l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)\n" +
            "  -f путь к файлу со списком ссылок\n" +
            "  -o имя папки, куда складывать скачанные файлы\n";


    public static void main(String[] args) {
        // проверим входные параметры
        // downloader -n<количество потоков> -l<ограничение скорости> -f<путь к файлу> -o<имя папки>

        int numberOfFlows = 0;
        int speedLimit = 0;
        String pathToFile = "";
        String nameOfDir = "";

        if (args.length != 4) {
            System.out.printf(ERROR_MESSAGE);
            return;
        } else {
            try {
                numberOfFlows = Integer.valueOf(args[0].replaceAll("[^0-9]", ""));

                String suffix = args[1].replaceAll("[^a-zA-Z]", "").replaceAll("-", "").toLowerCase(); // обработаем суффикс k - килобайты, m - мегабайты
                speedLimit = Integer.valueOf(args[1].replaceAll("[^0-9]", ""));
                switch (suffix) {
                    case "k":
                        speedLimit = speedLimit * 1024;
                        break;
                    case "m":
                        speedLimit = speedLimit * 1024 * 1024;
                        break;
                }
                pathToFile = String.valueOf(args[2].replaceAll("-", ""));
                nameOfDir = String.valueOf(args[3].replaceAll("-", ""));
            } catch(Exception exc) {
                System.out.printf("%s", exc.toString());
                return;
            }
        }


        System.out.printf("Параметры: \n");
        System.out.printf("Количество потоков: %d\n", numberOfFlows);
        System.out.printf("Ограничение скорости: %d байт/сек\n", speedLimit);
        System.out.printf("Путь к файлу: %s\n", pathToFile);
        System.out.printf("Имя папки: %s\n\n", nameOfDir);


        // проверим наличие файла (и его синтаксис), наличие папки
        TaskFile taskFile = new TaskFile(pathToFile); // TODO: проверка синтаксиса файла, а то можно понаписать...

        System.out.printf("Получены задания: \n");
        for (Task task : taskFile.getTasks()) {
            System.out.printf("Путь: %s в файл: %s\n", task.getPath(), task.getFileName());
        }
        System.out.printf("\n");

        File file = new File(nameOfDir);
        if (!file.exists() || !file.isDirectory()) {
            System.out.printf("Неверное имя папки для сохранения: %s", nameOfDir);
            return;
        }


        for (Task task : taskFile.getTasks()) {
            Downloader dl = new Downloader();
            try {
                dl.getFile(task.getPath());
            } catch (IOException exc) {
                System.out.printf("%s", exc.toString());
                return;
            }
            String pathToWriteFile = nameOfDir+"/"+task.getFileName();
            File fileToWrite = new File(pathToWriteFile);
            try {
                FileWriter writer = new FileWriter(fileToWrite);
                writer.write(dl.getBufferOfChars());
                writer.close();
            } catch (IOException e) {
                System.out.printf("%s", e.toString());
                return;
            }
            System.out.printf("Содержимое из: %s скачано и записано в файл: %s\n", task.getPath(), pathToWriteFile);
        }


    }
}
