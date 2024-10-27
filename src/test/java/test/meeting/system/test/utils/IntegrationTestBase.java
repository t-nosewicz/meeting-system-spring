package test.meeting.system.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import meeting.system.MeetingSystemSpringApplication;
import meeting.system.commons.dto.UserId;
import meeting.system.commons.logged.user.LoggedUserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@EnableAutoConfiguration
@AutoConfigureMockMvc
public class IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private LoggedUserFacade loggedUser;

    protected <T> T httpCall(MockHttpServletRequestBuilder request, TypeReference<T> responseContentType) throws Exception {
        var result = httpCall(request);
        return deserializeContent(result, responseContentType);
    }

    protected MvcResult httpCall(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc
                .perform(request)
                .andDo(print())
                .andReturn();
    }

    protected String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private <T> T deserializeContent(MvcResult result, TypeReference<T> responseContentType) throws UnsupportedEncodingException, JsonProcessingException {
        var contentAsString = result.getResponse().getContentAsString();
        if (contentAsString.isEmpty())
            return null;
        return objectMapper.readValue(contentAsString, responseContentType);
    }

    protected void logIn(UserId userId) {
        when(loggedUser.getLoggedUserId()).thenReturn(Option.of(userId));
    }

    protected void logOff(UserId userId) {
        when(loggedUser.getLoggedUserId()).thenReturn(Option.none());
    }

    protected UserId randomUserId() {
        return new UserId(current().nextLong());
    }
}