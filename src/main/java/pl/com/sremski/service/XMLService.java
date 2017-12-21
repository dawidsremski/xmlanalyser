package pl.com.sremski.service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;

public interface XMLService {
    Object readFromURL(URL url) throws IOException, XMLStreamException;
}
