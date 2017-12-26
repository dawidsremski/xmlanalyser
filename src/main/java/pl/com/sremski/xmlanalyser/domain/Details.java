package pl.com.sremski.xmlanalyser.domain;

import lombok.Data;

@Data
public class Details {

    private String firstPost;
    private String lastPost;
    private Integer totalPosts;
    private Integer totalAcceptedPosts;
    private Double avgScore;
}