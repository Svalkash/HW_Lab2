package bdtc.lab2;

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

