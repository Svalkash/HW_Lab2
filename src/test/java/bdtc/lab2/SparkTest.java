package bdtc.lab2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class SparkTest {

    static SparkConf conf;
    static JavaSparkContext sc;
    static Map<Integer, String> typeIDs;

    static final String typeID_csv = "./src/main/resources/typeID_example.csv";

    @BeforeClass
    public static void setUp() {
        conf = new SparkConf().setAppName("SparkApplication").setMaster("local");
        sc = new JavaSparkContext(conf);
        typeIDs = SparkApplication.readTypeIDs(typeID_csv);
        if (typeIDs == null) { //Проверка на ошибки при чтении
            throw new RuntimeException("Can't load typeIDs from CSV!");
        }
    }

    @Test
    public void testOnePostUser() {
        List<String> sin = Arrays.asList(
                "1234, user, 100045478, 1",
                "1234, user, 789452467, 2",
                "1234, user, 454378450, 3",
                "1234, user, 312456744, 1",
                "1234, user, 333440004, 2"
        );
        List<String> sout_exp = Arrays.asList(
                "1234, read, 1"
        );

        List<String> sout = ActionCounter.countPostActions(sc.parallelize(sin), typeIDs).collect();
        assertEquals("Wrong output size", 1, sout.size());
        assertEquals("Wrong output contents", sout_exp, sout);
    }

    @Test
    public void testMoreUsers() {
        List<String> sin = Arrays.asList(
                "1234, user, 100045478, 1",
                "1234, user2, 789452467, 2",
                "1234, user3, 454378450, 3",
                "1234, user2, 312456744, 1",
                "1234, user3, 333440004, 2"
        );
        List<String> sout_exp = Arrays.asList(
                "1234, read, 2",
                "1234, preview, 1"
        );

        List<String> sout = ActionCounter.countPostActions(sc.parallelize(sin), typeIDs).collect();
        Collections.sort(sout_exp);

        assertEquals("Wrong output size", sout_exp.size(), sout.size());
        assertEquals("Wrong output contents", sout_exp, sout);
    }

    @Test
    public void testMorePostsUsers() {
        List<String> sin = Arrays.asList(
                "1234, user, 100045478, 1",
                "1234, user2, 789452467, 2",
                "1234, user3, 454378450, 1",
                "12345, user2, 312456744, 1",
                "12345, user3, 333440004, 2",
                "123456, user2, 312456744, 2",
                "123456, user2, 312456744, 2",
                "123456, user2, 312456744, 2",
                "123456, user1, 312456744, 2",
                "123456, user2, 312456744, 3",
                "123456, user3, 312456744, 2",
                "123456, user3, 312456744, 3",
                "1234567, user3, 333440004, 3",
                "1234567, user2, 333440004, 3",
                "1234567, user1, 333440004, 1",
                "1234567, user1, 333440004, 1"
        );
        List<String> sout_exp = Arrays.asList(
                "1234, read, 2",
                "1234, preview, 1",
                "12345, read, 1",
                "12345, preview, 1",
                "123456, preview, 3",
                "1234567, read, 1",
                "1234567, no, 2"
        );

        List<String> sout = ActionCounter.countPostActions(sc.parallelize(sin), typeIDs).collect();
        Collections.sort(sout_exp);

        assertEquals("Wrong output size", sout_exp.size(), sout.size());
        assertEquals("Wrong output contents", sout_exp, sout);
    }
}
