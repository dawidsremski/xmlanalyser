package pl.com.sremski.service;

import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

@Service
public interface XMLService {
    Object readFromInputStream(InputStream inputStream) throws IOException, XMLStreamException;
}
