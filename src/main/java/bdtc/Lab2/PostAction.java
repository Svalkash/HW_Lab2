package bdtc.Lab2;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс для хранения информации о действии
 * postID - номер записи
 * userID - имя пользователя
 * time - время действия
 * type - номер типа действия
 */
@Data
@AllArgsConstructor
public class PostAction {
    int postID;
    String userID;
    long time;
    int type;
}

