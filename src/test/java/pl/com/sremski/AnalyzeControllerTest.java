package pl.com.sremski;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.com.sremski.web.Analyze;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Analyze.class})
@WebAppConfiguration
public class AnalyzeControllerTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 1080);
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
    public void AnalyzeControllerTestWrongURL() throws Exception {
        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("{\"url\" : \"f4fr43fdf\"}")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void AnalyzeControllerTestNoUrlSpecified() throws Exception {
        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).content("f4fr43fdf")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void AnalyzeControllerTestAccessForbidden() throws Exception {
        new MockServerClient("localhost", 1080).when(HttpRequest.request().
                withPath("/forbidden-xml.xml").withMethod("HEAD")).
                respond(HttpResponse.response().withStatusCode(403));

        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).
                content("{ \"url\" : \"http://localhost:1080/forbidden-xml.xml\" }")).
                andExpect(status().isForbidden());
    }

    @Test
    public void AnalyzeControllerTestXmlNotFound() throws Exception {
        new MockServerClient("localhost", 1080).when(HttpRequest.request().
                withPath("/no-such-xml-existing.xml").withMethod("HEAD")).
                respond(HttpResponse.response().withStatusCode(404));

        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).
                content("{ \"url\" : \"http://localhost:1080/no-such-xml-existing.xml\" }")).
                andExpect(status().isNotFound());
    }

    @Test
    public void AnalyzeControllerTestEverythingOK() throws Exception {

        new MockServerClient("localhost", 1080).when(HttpRequest.request().
                withPath("/correct-xml.xml")).
                respond(HttpResponse.response().withBody("<posts></posts>").
                        withStatusCode(200));

        mockMvc.perform(post("/analyze").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).
                content("{ \"url\" : \"http://localhost:1080/correct-xml.xml\" }")).
                andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).
                andExpect(status().isOk());
    }
}
