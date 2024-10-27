package meeting.system.user.funds;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.UserId;
import meeting.system.user.funds.dto.ChargeFailure;
import meeting.system.user.funds.dto.ReturnFundsFailure;

import java.math.BigDecimal;

@Slf4j
@AllArgsConstructor
public class LogsDecorator implements UsersFundsFacade {
    private final UsersFundsFacade usersFundsFacade;

    @Override
    public Option<ReturnFundsFailure> returnFunds(UserId userId, BigDecimal amount) {
        log.info("returning {} units for user {}", amount.doubleValue(), userId.id());
        return usersFundsFacade
                .returnFunds(userId, amount)
                .peek(failure -> log.info("failed to increase user's {} funds by {}, reason: {}", userId.id(), amount, failure))
                .onEmpty(() -> log.info("user's {} funds got increased by {}", userId.id(), amount));
    }

    @Override
    public Option<ChargeFailure> charge(UserId userId, BigDecimal amount) {
        log.info("trying to reduce funds of user {} by {}", userId.id(), amount);
        return usersFundsFacade
                .charge(userId, amount)
                .peek(failure -> log.info("failed to reduce user's {} funds by {}, reason: {}", userId.id(), amount, failure))
                .onEmpty(() -> log.info("user's {} funds got reduced by {}", userId.id(), amount));
    }
}