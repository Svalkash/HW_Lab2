package bdtc.Lab2;

import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.FileReader;
import java.util.*;

import org.apache.hadoop.io.Text;

/**
 * Считает количество событий syslog разного уровная log level по часам.
 */
@Log4j
public class SparkApplication {

    /**
     * Функция, считывающая соответствие typeID и имён из CSV-файла.
     * Возвращает Map, или null строку при любых ошибках.
     * Сами ошибки выводятся в лог.
     * @param filename - Имя файла для чтения.
     * @return Map с соответствием в случае успешного чтения, null в случае ошибок.
     */
    public static Map<Integer, String> readTypeIDs(String filename) {
        //"./src/main/resources/yourfile.csv"
        CSVReader reader;
        // Открытие файла
        try {
            reader = new CSVReader(new FileReader(filename));
        } catch (Exception ex) {
            log.fatal("CSV loading error: " + ex.getMessage());
            return null;
        }
        try {
            List<String[]> stringList = reader.readAll(); // Считываем все строки
            stringList.forEach((item)->{ if (item.length != 2) throw new RuntimeException("Wrong format"); }); //Проверка количества полей в каждой строке - должно быть 2
            if (stringList.size() == 0) // Проверка на пустой файл
                throw new RuntimeException("Empty CSV");
            Set<Integer> numSet = new LinkedHashSet<>();
            Set<String> nameSet = new LinkedHashSet<>();
            Map<Integer, String> ret = new HashMap<>(); // Финальная строка
            for (String[] strings : stringList) {
                Integer num = Integer.parseInt(strings[0]);
                if (!numSet.add(num)) // Проверка на одинаковые номера метрик
                    throw new RuntimeException("Duplicate type IDs");
                if (!nameSet.add(strings[1]))
                    throw new RuntimeException("Duplicate type names"); // Проверка на одинаковые имена метрик (иначе это не имеет смысла)
                ret.put(num, strings[1]);
            }
            return ret;
        }
        catch(NumberFormatException ex) { // Появляется, когда metricID - не число
            log.error("Type is not a number: " + ex.getMessage());
            return null;
        }
        catch(Exception ex) { // Другие ошибки
            log.error("Exception while trying to read types config: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Основная функция приложения
     * @param args - args[0]: входной файл / папка, args[1] - выходная папка, args[2] - имя файла с typeID (криво работает в HDFS)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new RuntimeException("Usage: java -jar SparkSQLApplication.jar inputFile outputDirectory typeIDcsv");
        }
        Map<Integer, String> typeIDs;
        if (args.length == 2) { // Автозаполнение типов (чтобы в HDFS "File not found" орать перестал)
            typeIDs = new HashMap<>();
            typeIDs.put(1, "read");
            typeIDs.put(2, "preview");
            typeIDs.put(3, "no");
        }
        else // Считывание из файла (локально работало, в прошлой лабе тоже... странно)
            typeIDs = readTypeIDs(args[2]);
        if (typeIDs == null) { //Проверка на ошибки при чтении
            log.fatal("Couldn't read typeIDs, stopping.");
            return;
        }

        log.info("Application started!");
        log.debug("Application started");
        SparkConf conf = new SparkConf().setAppName("SparkApplication").setMaster("local"); // Создание конфиг. объекта
        conf.set("spark.files.ignoreCorruptFiles", "true"); // Игнорирование битых файлов - для избежания конфликтов с Flume
        JavaSparkContext sc = new JavaSparkContext(conf); // Создание контекста

        JavaRDD<String> din = sc.sequenceFile(args[0], LongWritable.class, Text.class).map(s -> s._2.toString()); // Чтение из Sequence-файла и превращение содержимого в String.
        log.info("===============COUNTING...================");
        JavaRDD<String> result = ActionCounter.countPostActions(din, typeIDs); // Запуск функции подсчёта, готовящей трансформации
        log.info("============SAVING FILE TO " + args[1] + " directory============");
        result.saveAsTextFile(args[1]); // action для сохранения в файл
    }
}
