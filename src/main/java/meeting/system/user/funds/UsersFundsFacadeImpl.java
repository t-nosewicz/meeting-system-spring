package meeting.system.user.funds;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.UserId;
import meeting.system.user.funds.dto.ChargeFailure;
import meeting.system.user.funds.dto.ReturnFundsFailure;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static meeting.system.user.funds.dto.ReturnFundsFailure.VALUE_CANNOT_BE_NEGATIVE;

//naive implementation
@Slf4j
class UsersFundsFacadeImpl implements UsersFundsFacade {
    private final Map<UserId, BigDecimal> funds = new HashMap<>();
    @Override
    public Option<ReturnFundsFailure> returnFunds(UserId userId, BigDecimal amount) {
        if(amount.doubleValue() < 0)
            return Option.of(VALUE_CANNOT_BE_NEGATIVE);
        funds.merge(userId, amount, BigDecimal::add);
        return Option.none();
    }

    @Override
    public Option<ChargeFailure> charge(UserId userId, BigDecimal amount) {
        if (amount.doubleValue() < 0)
            return Option.of(ChargeFailure.REDUCE_AMOUNT_CANNOT_BE_NEGATIVE);
        if (!hasAtLeastFunds(userId, amount))
            return Option.of(ChargeFailure.USER_DOESNT_HAVE_ENOUGH_FUNDS);
        var currentAmount = funds.get(userId);
        funds.put(userId, currentAmount.subtract(amount));
        return Option.none();
    }

    private boolean hasAtLeastFunds(UserId userId, BigDecimal expectedAmount) {
        return Option
                .of(funds.get(userId))
                .map(currentAmount -> currentAmount.compareTo(expectedAmount))
                .map(result -> result != -1)
                .getOrElse(false);
    }
}