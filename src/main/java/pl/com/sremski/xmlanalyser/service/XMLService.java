package pl.com.sremski.xmlanalyser.service;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public interface XMLService {
    Object readFromInputStream(InputStream inputStream) throws XMLStreamException;
}
