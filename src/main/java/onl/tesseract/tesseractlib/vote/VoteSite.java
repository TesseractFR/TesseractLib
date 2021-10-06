package onl.tesseract.tesseractlib.vote;

import java.time.Duration;

public record VoteSite(String serviceName, String address, Duration delay) {
}
