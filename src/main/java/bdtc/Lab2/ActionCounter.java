package bdtc.Lab2;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.util.Map;

@AllArgsConstructor
@Log4j
public class ActionCounter {
    /**
     * Функция подсчета уникальных действий во входной RDD.
     * Парсит строки в датасете, разделяя их на поля, после чего для используются трансформации для поиска уникальных действий с конкретной записью у каждого пользователя и суммирования.
     * @param inputRDD - входной JavaRDD (из строк) для анализа
     * @param typeIDs - таблица соответствия номеров типов их названиям
     * @return результат подсчета в формате JavaRDD
     */
    public static JavaRDD<String> countPostActions(JavaRDD<String> inputRDD, Map<Integer, String> typeIDs) {
        return inputRDD
                .map(s -> { // Создание экземпляров PostAction
                    try {
                        // Предполагается, что входные данные - целые числа, разделённые запятыми с пробелами.
                        // Любое несоответствие приводит к ошибке.
                        String[] fields = s.split(", ");
                        if (fields.length != 4)
                            throw new RuntimeException("Wrong field count");
                        PostAction pa = new PostAction(Integer.parseInt(fields[0]), fields[1], Long.parseLong(fields[2]), Integer.parseInt(fields[3]));
                        if (!typeIDs.containsKey(pa.type)) // Проверка существования типа сообщения
                            throw new RuntimeException("Non-existing type");
                        return pa;
                    } catch (Exception ex) { // Если строка не соответствует ожиданиям. Также ловятся исключения от parseInt/Long.
                        log.error(ex.getMessage());
                        return null;
                    }
                })
                .filter(s -> s != null) //Очистка от null (некорректных строк)
                .mapToPair(pa -> new Tuple2<>(new Tuple2<>(pa.postID, pa.userID), pa.type)) // Время не используется и отбрасывается. Ключ - (номер поста + имя польз.), значение - тип.
                .reduceByKey((a, b) -> Math.min(a, b)) // Оставляем только самый интересный тип. Если нам не нужна информация о многократном чтении пользователем поста, то, скорее всего, надо сохранять только "лучшее" взаимодействие (игнор -> предпросмотр -> чтение)
                //.distinct() //на случай, если можно разные типы - НЕ ТЕСТИРОВАЛОСЬ
                .mapToPair(oldPair -> new Tuple2<>(new Tuple2<>(oldPair._1._1, oldPair._2), 1)) //Ключ - (номер поста + номер типа), значение - 1 (для суммирования).
                .reduceByKey((a, b) -> a + b) // Суммируем обращения
                .map(newPair -> newPair._1._1.toString() + ", " + typeIDs.get(newPair._1._2) + ", " + newPair._2) // Превращаем таблицу в красивую строку для вывода
                .sortBy(a -> a, true, 1); // Сортировка, чтобы тесты перестали ломаться. Кстати, какого фига он read-only list возвращает? Так и не понял, в чём смысл.
        //log.info(ret.collect().toString());
    }

}
