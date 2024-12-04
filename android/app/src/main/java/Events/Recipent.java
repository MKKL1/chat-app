package Events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Recipent {
    private final long id;
    private final EventContext eventContext;

    public enum EventContext {
        COMMUNITY,
        USER,
        CHANNEL
    }
}
