package pl.com.sremski.xmlanalyser;

import org.junit.Before;
import org.junit.Test;
import pl.com.sremski.xmlanalyser.domain.Analysis;
import pl.com.sremski.xmlanalyser.service.XMLAnalysisService;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLAnalysisServiceTest {

    private XMLAnalysisService xmlAnalysisService;
    private String exampleXml;

    @Before
    public void before() {
        xmlAnalysisService = new XMLAnalysisService();
    }

    @Test
    public void xmlAnalysisServiceTestTwoPosts() throws XMLStreamException {
        //given
        exampleXml = "<posts>" +
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
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo("2016-01-12T18:45:19.963");
        assertThat(analysis.getDetails().getLastPost()).isEqualTo("2016-01-12T18:47:19.963");
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(2);
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(5);
    }

    @Test
    public void xmlAnalysisServiceFirstPostWithoutCreationDate() throws XMLStreamException {
        //given
        exampleXml = "<posts>" +
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
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo(null);
    }

    @Test
    public void xmlAnalysisServiceTestLastPostWithoutCreationDate() throws XMLStreamException {
        //given
        exampleXml = "<posts>" +
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
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getLastPost()).isEqualTo(null);
    }

    @Test
    public void xmlAnalysisServiceTestTwoPostsOneWithoutScore() throws XMLStreamException {
        //given
        exampleXml = "<posts>" +
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
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(4);
    }

    @Test
    public void xmlAnalysisServiceTestTwoPostsOneWithoutId() throws XMLStreamException {
        //given
        exampleXml = "<posts>" +
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
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(1);
        assertThat(analysis.getDetails().getLastPost()).isEqualTo("2016-01-12T18:45:19.963");
    }

    @Test
    public void xmlAnalysisServiceTestNoPosts() throws XMLStreamException {
        //given
        exampleXml = "<posts></posts>";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);

        //then
        assertThat(analysis.getDetails().getTotalPosts()).isEqualTo(0);
        assertThat(analysis.getDetails().getFirstPost()).isEqualTo(null);
        assertThat(analysis.getDetails().getLastPost()).isEqualTo(null);
        assertThat(analysis.getDetails().getAvgScore()).isEqualTo(null);
        assertThat(analysis.getDetails().getTotalAcceptedPosts()).isEqualTo(0);
    }

    @Test(expected = XMLStreamException.class)
    public void xmlAnalysisTestInvalidXML() throws XMLStreamException {
        //given
        exampleXml = "asfsa98fyds987yf";

        //when
        InputStream inputStream = new ByteArrayInputStream(exampleXml.getBytes(StandardCharsets.UTF_8));

        //then
        Analysis analysis = xmlAnalysisService.readFromInputStream(inputStream);
    }
}