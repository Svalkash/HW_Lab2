package bdtc.lab2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostActionKey {
    int postID;
    String userID;

    public PostActionKey(PostAction pa) {
        this.postID = pa.postID;
        this.userID = pa.userID;
    }
}
