package ch.hslu.vsk.logger.component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ch.hslu.vsk.logger.api.LogLevel;
import ch.hslu.vsk.logger.common.LogMessage;
import ch.hslu.vsk.stringpersistor.api.PersistedString;

public final class MessageFormatter {

    private MessageFormatter() {

    };

    public static PersistedString format(final LogMessage log) {
        final String payload = String.format("[%s] %s %s", log.getLoglevel(), log.getMessage(),
                log.getApplicationId());
        return new PersistedString(log.getTimeOfLog(), payload);
    }

    public static LogMessage parse(final PersistedString str) {
        final String payload = str.getPayload();
        final Pattern pattern = Pattern.compile("^\\[(.+)\\] (.+) (([a-z0-9]+-){4}[a-z0-9]+)$");
        final Matcher matcher = pattern.matcher(payload);
        if (!matcher.matches() || matcher.groupCount() < 2) {
            return null;
        }

        final String stringLevel = matcher.group(1);
        LogLevel level = null;

        switch (stringLevel) {
            case "INFO":
                level = LogLevel.INFO;
                break;

            case "ERROR":
                level = LogLevel.ERROR;
                break;

            case "DEBUG":
                level = LogLevel.DEBUG;
                break;

            default:
                level = LogLevel.WARN;
        }
        final String message = matcher.group(2);
        final UUID id = UUID.fromString(matcher.group(3));
        return new LogMessage(message, level, id, str.getTimestamp());
    }
}
