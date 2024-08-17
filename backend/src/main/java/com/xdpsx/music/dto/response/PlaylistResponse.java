package com.xdpsx.music.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse extends RepresentationModel<PlaylistResponse> {
    private Long id;
    private String name;
    private int totalTracks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfileResponse owner;

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
