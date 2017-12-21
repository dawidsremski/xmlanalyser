package pl.com.sremski;

import org.junit.Before;
import org.junit.Test;
import pl.com.sremski.domain.Analysis;
import pl.com.sremski.service.XMLAnalysisService;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLAnalysisServiceTest {

    private XMLAnalysisService xmlAnalysisService;
    private String exampleXML;

    @Before
    public void Before() {
        xmlAnalysisService = new XMLAnalysisService();
    }

    @Test
    public void XMLAnalysisServiceTestTwoPosts() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts>" +
                "<row Id=\"1\" " +
                "AcceptedAnswerId=\"51\" " +
                "CreationDate=\"2016-01-12T18:45:19.963\" " +
                "Score=\"4\" " +
                "/>" +
                "<row Id=\"2\" " +
                "AcceptedAnswerId=\"49\" " +
                "CreationDate=\"2016-01-12T18:47:19.963\" " +
                "Score=\"6\" " +
                "/>" +
                "</posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo("2016-01-12T18:45:19.963");
        assertThat(analysis.getDetails().getLastPost()).isEqualTo("2016-01-12T18:47:19.963");
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(2);
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(5);
    }

    @Test
    public void XMLAnalysisServiceFirstPostWithoutCreationDate() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts>" +
                "<row Id=\"1\" " +
                "AcceptedAnswerId=\"51\" " +
                "Score=\"4\" " +
                "/>" +
                "<row Id=\"2\" " +
                "AcceptedAnswerId=\"49\" " +
                "CreationDate=\"2016-01-12T18:47:19.963\" " +
                "Score=\"6\" " +
                "/>" +
                "</posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo(null);
    }

    @Test
    public void XMLAnalysisServiceTestLastPostWithoutCreationDate() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts>" +
                "<row Id=\"1\" " +
                "AcceptedAnswerId=\"51\" " +
                "CreationDate=\"2016-01-12T18:45:19.963\" " +
                "Score=\"4\" " +
                "/>" +
                "<row Id=\"2\" " +
                "AcceptedAnswerId=\"49\" " +
                "Score=\"6\" " +
                "/>" +
                "</posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getLastPost()).isEqualTo(null);
    }

    @Test
    public void XMLAnalysisServiceTestTwoPostsOneWithoutScore() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts>" +
                "<row Id=\"1\" " +
                "AcceptedAnswerId=\"51\" " +
                "CreationDate=\"2016-01-12T18:45:19.963\" " +
                "Score=\"4\" " +
                "/>" +
                "<row Id=\"2\" " +
                "AcceptedAnswerId=\"49\" " +
                "CreationDate=\"2016-01-12T18:47:19.963\" " +
                "/>" +
                "</posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(4);
    }

    @Test
    public void XMLAnalysisServiceTestTwoPostsOneWithoutId() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts>" +
                "<row Id=\"1\" " +
                "AcceptedAnswerId=\"51\" " +
                "CreationDate=\"2016-01-12T18:45:19.963\" " +
                "Score=\"4\" " +
                "/>" +
                "<row " +
                "AcceptedAnswerId=\"49\" " +
                "CreationDate=\"2016-01-12T18:47:19.963\" " +
                "Score=\"6\" " +
                "/>" +
                "</posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(1);
        assertThat(analysis.getDetails().getLastPost()).isEqualTo("2016-01-12T18:45:19.963");
    }

    @Test
    public void XMLAnalysisServiceTestNoPosts() throws IOException, XMLStreamException {
        //given
        exampleXML = "<posts></posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(0);
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo(null);
        assertThat(analysis.getDetails().getLastPost()).isEqualTo(null);
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(null);
        assertThat(analysis.getDetails().getTotalAcceptedPosts()).isEqualTo(0);
    }

    @Test(expected = XMLStreamException.class)
    public void XMLAnalysisTestInvalidXML() throws IOException, XMLStreamException {
        //given
        exampleXML = "asfsa98fyds987yf";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXML.getBytes(StandardCharsets.UTF_8));

        //then
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);
    }
}
