package pl.com.sremski.domain;

import lombok.Data;

@Data
class Details {

    private String firstPost;
    private String lastPost;
    private Integer totalPosts;
    private Integer totalAcceptedPosts;
    private Double avgScore;
}