package meeting.system.user.funds;

import io.vavr.control.Option;
import meeting.system.commons.dto.UserId;
import meeting.system.user.funds.dto.ChargeFailure;
import meeting.system.user.funds.dto.ReturnFundsFailure;

import java.math.BigDecimal;

public interface UsersFundsFacade {
    Option<ReturnFundsFailure> returnFunds(UserId userId, BigDecimal amount);

    Option<ChargeFailure> charge(UserId userId, BigDecimal amount);
}
