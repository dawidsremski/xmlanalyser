package pl.com.sremski.domain;

import lombok.Getter;
import lombok.Setter;

public class Analysis {

    @Setter @Getter private String analyseDate;
    @Setter @Getter private Double analyseTime;
    @Setter @Getter private Details details;
}
