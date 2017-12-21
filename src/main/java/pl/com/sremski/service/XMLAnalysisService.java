package pl.com.sremski.service;

import org.springframework.stereotype.Service;
import pl.com.sremski.domain.Analysis;
import pl.com.sremski.domain.Details;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class XMLAnalysisService implements XMLService {

    @Override
    public Analysis readFromURL(URL url) throws IOException, XMLStreamException {

        LocalDateTime analyseDate = LocalDateTime.now();

        Analysis analysis = new Analysis();
        Details details = new Details();
        String date = null;
        int acceptedPosts = 0;
        int totalPosts = 0;
        Double scoreSum = 0.0;

        InputStream inputStream = url.openStream();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {

                int attributeCount = xmlStreamReader.getAttributeCount();

                for (int i = 0; i < attributeCount; i++) {

                    String attributeName = xmlStreamReader.getAttributeName(i).toString();

                    switch (attributeName) {
                        case "Id":
                            totalPosts++;
                            break;
                        case "AcceptedAnswerId":
                            acceptedPosts++;
                            break;
                        case "Score":
                            scoreSum += Integer.valueOf(xmlStreamReader.getAttributeValue(i));
                            break;
                        case "CreationDate":
                            if (details.getFirstPost() == null) {
                                details.setFirstPost(xmlStreamReader.getAttributeValue(i));
                            }
                            date = xmlStreamReader.getAttributeValue(i);
                    }
                }
            }
        }

        xmlStreamReader.close();

        details.setLastPost(date);
        details.setTotalPosts(totalPosts);
        details.setTotalAcceptedPosts(acceptedPosts);
        details.setAvgScore(scoreSum / totalPosts);
        analysis.setDetails(details);

        analysis.setAnalyseDate(analyseDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        analysis.setAnalyseTime(analyseDate.until(LocalDateTime.now(), SECONDS));

        return analysis;
    }
}