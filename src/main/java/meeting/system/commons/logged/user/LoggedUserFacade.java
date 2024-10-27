package meeting.system.commons.logged.user;

import io.vavr.control.Option;
import meeting.system.commons.dto.UserId;
import org.springframework.stereotype.Service;

//not implemented
@Service
public class LoggedUserFacade {

    public Option<UserId> getLoggedUserId() {
        return Option.none();
    }
}