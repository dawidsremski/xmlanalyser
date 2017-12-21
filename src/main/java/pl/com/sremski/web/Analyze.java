package pl.com.sremski.web;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import pl.com.sremski.domain.Analysis;
import pl.com.sremski.domain.XMLUrl;
import pl.com.sremski.service.XMLAnalysisService;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@EnableWebMvc
public class Analyze {

    @Bean
    public XMLAnalysisService xmlAnalysisService() {
        return new XMLAnalysisService();
    }


    @RequestMapping(value = "/analyze", method = RequestMethod.POST)
    public ResponseEntity<Analysis> analyze(@RequestBody XMLUrl url) {

        URL XMLUrl;

        try {
            XMLUrl = new URL(url.getUrl());
        } catch (MalformedURLException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpURLConnection.setFollowRedirects(false);

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) XMLUrl.openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            httpURLConnection.disconnect();
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Analysis analysis = null;

        try {
            analysis = xmlAnalysisService().readFromInputStream(XMLUrl.openStream());
        } catch (IOException | XMLStreamException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }
}