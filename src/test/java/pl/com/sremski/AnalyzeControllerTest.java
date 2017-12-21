package pl.com.sremski;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.com.sremski.web.Analyze;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Analyze.class})
@WebAppConfiguration
public class AnalyzeControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void AnalyzeControllerTestProperXML() throws Exception {
        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("{\"url\" : \"https://s3-eu-west-1.amazonaws.com/merapar-assessment/3dprinting-posts.xml\"}")).
                andExpect(status().isOk());
    }

    @Test
    public void AnalyzeControllerWrongURL() throws Exception {
        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("{\"url\" : \"f4fr43fdf\"}")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void AnalyzeControllerNoUrlSpecified() throws Exception {
        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("f4fr43fdf")).
                andExpect(status().isBadRequest());
    }

    /*
    @Test
    public void AnalyzeControllerAccessForbidden() throws Exception {

        MockServerClient mockServer = new MockServerClient("localhost", 234);

        mockServer.when(request().withMethod("GET").withPath("/getXML/")).respond(response().withStatusCode(403));

        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("{\"url\" : \"localhost:1080/getXML/\"}")).
                andExpect(status().isForbidden());
    }
    */
}
