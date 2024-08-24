package com.xdpsx.music.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"id", "name", "image", "url", "durationMs", "totalLikes", "listeningCount",
        "trackNumber", "createdAt", "genre", "artists", "album", "linksMap"})
public class TrackResponse extends RepresentationModel<TrackResponse> implements Serializable {
    private Long id;
    private String name;
    @JsonProperty("duration_ms")
    private Integer durationMs;
    private String image;
    private String url;
    @JsonProperty("total_likes")
    private long totalLikes;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("track_number")
    private Integer trackNumber;
    @JsonProperty("listening_count")
    private int listeningCount;
    private AlbumResponse album;
    private GenreResponse genre;
    private List<ArtistResponse> artists;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("links")
    private Map<String, String> linksMap;

    @JsonIgnore
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

    public void addCustomLinks(Link... links) {
        if (this.linksMap == null){
            this.linksMap = new HashMap<>();
        }
        for (Link link : links) {
            this.linksMap.put(link.getRel().value(), link.getHref());
        }
    }
}
