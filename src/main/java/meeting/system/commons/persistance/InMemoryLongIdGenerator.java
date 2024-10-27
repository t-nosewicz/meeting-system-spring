package meeting.system.commons.persistance;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class InMemoryLongIdGenerator implements Supplier<Long> {
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Long get() {
        return idGenerator.getAndIncrement();
    }
}