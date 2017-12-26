package pl.com.sremski.xmlanalyser.domain;

import lombok.Getter;
import lombok.Setter;

public class Analysis {

    @Setter @Getter private String analyseDate;
    @Setter @Getter private Double analyseTime;
    @Setter @Getter private Details details;
}
