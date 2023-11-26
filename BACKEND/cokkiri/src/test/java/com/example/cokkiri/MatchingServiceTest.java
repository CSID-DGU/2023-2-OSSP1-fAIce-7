import com.example.cokkiri.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class MatchingServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MatchingServiceTest.class);

}
