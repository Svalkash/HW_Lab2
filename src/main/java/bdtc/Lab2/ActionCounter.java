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
     * Функция подсчета количества логов разного уровня в час.
     * Парсит строку лога, в т.ч. уровень логирования и час, в который событие было зафиксировано.
     * @param inputDataset - входной DataSet для анализа
     * @return результат подсчета в формате JavaRDD
     */
    public static JavaRDD<String> countPostActions(JavaRDD<String> inputRDD, Map<Integer, String> typeIDs) {
        return inputRDD
                .map(s -> { //transform to PA
                    try {
                        // Предполагается, что входные данные - целые числа, разделённые запятыми с пробелами.
                        // Любое несоответствие приводит к ошибке.
                        String[] fields = s.split(", ");
                        if (fields.length != 4)
                            throw new RuntimeException("Wrong field count");
                        PostAction pa = new PostAction(Integer.parseInt(fields[0]), fields[1], Long.parseLong(fields[2]), Integer.parseInt(fields[3]));
                        if (!typeIDs.containsKey(pa.type))
                            throw new RuntimeException("Non-existing type");
                        return pa;
                    } catch (Exception ex) { // Если строку не получилось разбить на массив целых чисел.
                        log.error(ex.getMessage());
                        return null;
                    }
                })
                .filter(s -> s != null) //clear nulls (incorrect data)
                .mapToPair(pa -> new Tuple2<>(new Tuple2<>(pa.postID, pa.userID), pa.type)) //remove time so it won't be in our way AND turn it into a pair like (postID, userID) | type
                .reduceByKey((a, b) -> Math.min(a, b)) //leave only the "best" type (read < open < no read, for example)
                //.distinct() //leave 1 instance of each user+type - otherwise
                .mapToPair(oldPair -> new Tuple2<>(new Tuple2<>(oldPair._1._1, oldPair._2), 1)) //to (postID, type) | 1 to count - drop user because the're unique now
                .reduceByKey((a, b) -> a + b) //count per post+type
                .map(newPair -> newPair._1._1.toString() + ", " + typeIDs.get(newPair._1._2) + ", " + newPair._2) //turn into CSV: "postID,typeName,"
                .sortBy(a->a, true, 1); //because f* lists
        //log.info(ret.collect().toString());
    }

}
