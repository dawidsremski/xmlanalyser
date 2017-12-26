package pl.com.sremski.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pl.com.sremski.domain.Analysis;
import pl.com.sremski.domain.Details;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.MILLIS;

@Service
@Slf4j
public class XMLAnalysisService implements XMLService {

    @Bean
    public XMLAnalysisService xmlAnalysisService() {
        return new XMLAnalysisService();
    }

    @Override
    public Analysis readFromInputStream(InputStream inputStream) throws XMLStreamException {

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
        log.info("Stream opened");

        try {
            while (xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
                if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {

                    int attributeCount = xmlStreamReader.getAttributeCount();

                    for (int i = 0; i < attributeCount; i++) {

                        String attributeName = xmlStreamReader.getAttributeName(i).toString();

                        if (attributeName.equals("Id")) {
                            totalPosts++;
                            log.debug("Processing post " + totalPosts);
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
        } catch (XMLStreamException e) {
            log.debug(e.getMessage());
            if (totalPosts < 1) throw e;
        }

        xmlStreamReader.close();
        log.info("Stream closed");

        details.setLastPost((totalPosts == 1) ? details.getFirstPost() : lastPostDate);
        details.setTotalPosts(totalPosts);
        details.setTotalAcceptedPosts(acceptedPosts);
        details.setAvgScore((scoredPosts == 0) ? null : scoreSum / scoredPosts);
        analysis.setDetails(details);

        analysis.setAnalyseDate(analyseDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        analysis.setAnalyseTime((double) analyseDate.until(LocalDateTime.now(), MILLIS) / 1000);

        return analysis;
    }
}