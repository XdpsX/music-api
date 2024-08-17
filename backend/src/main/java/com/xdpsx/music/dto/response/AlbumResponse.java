package com.xdpsx.music.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumResponse extends RepresentationModel<AlbumResponse> implements Serializable {
    private Long id;
    private String name;
    private String image;
    private LocalDate releaseDate;
    private int totalTracks;
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
