package bdtc.Lab2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAction {
    int postID;
    String userID;
    long time;
    int type;
}

