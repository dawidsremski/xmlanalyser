package pl.com.sremski.service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public interface XMLService {
    Object readFromInputStream(InputStream inputStream) throws IOException, XMLStreamException;
}
