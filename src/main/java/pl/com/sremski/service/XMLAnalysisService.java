package pl.com.sremski.service;

import org.springframework.stereotype.Service;
import pl.com.sremski.domain.Analysis;
import pl.com.sremski.domain.Details;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class XMLAnalysisService implements XMLService {

    @Override
    public Analysis readFromInputStream(InputStream inputStream) throws IOException, XMLStreamException {

        LocalDateTime analyseDate = LocalDateTime.now();

        Analysis analysis = new Analysis();
        Details details = new Details();
        String lastPostDate = null;
        boolean isElementPost = false;
        int acceptedPosts = 0;
        int scoredPosts = 0;
        int totalPosts = 0;
        Double scoreSum = 0.0;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

        while (xmlStreamReader.hasNext()) {
            xmlStreamReader.next();
            if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {

                int attributeCount = xmlStreamReader.getAttributeCount();

                for (int i = 0; i < attributeCount; i++) {

                    String attributeName = xmlStreamReader.getAttributeName(i).toString();

                    if (attributeName.equals("Id")) {
                        totalPosts++;
                        isElementPost = true;
                    }

                    if (isElementPost) {
                        switch (attributeName) {
                            case "AcceptedAnswerId":
                                acceptedPosts++;
                                break;
                            case "Score":
                                scoreSum += Integer.valueOf(xmlStreamReader.getAttributeValue(i));
                                scoredPosts++;
                                break;
                            case "CreationDate":
                                if (totalPosts == 1) {
                                    details.setFirstPost(xmlStreamReader.getAttributeValue(i));
                                } else {
                                    lastPostDate = xmlStreamReader.getAttributeValue(i);
                                }
                        }
                    }
                }
                isElementPost = false;
            }
        }

        xmlStreamReader.close();

        details.setLastPost((totalPosts == 1) ? details.getFirstPost() : lastPostDate);
        details.setTotalPosts(totalPosts);
        details.setTotalAcceptedPosts(acceptedPosts);
        details.setAvgScore((scoredPosts == 0) ? null : scoreSum / scoredPosts);
        analysis.setDetails(details);

        analysis.setAnalyseDate(analyseDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        analysis.setAnalyseTime(analyseDate.until(LocalDateTime.now(), SECONDS));

        return analysis;
    }
}