package com.xdpsx.music.dto.common;

import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.Serializable;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> extends RepresentationModel<PageResponse<T>> implements Serializable {
    private Collection<T> items;
    private int pageNum;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public void addPaginationLinks(String baseUri) {
        if (pageNum > 1) {
            String firstPageUri = getUriString(baseUri, 1);
            this.add(Link.of(firstPageUri, "first"));
            String prevPageUri = getUriString(baseUri, pageNum - 1);
            this.add(Link.of(prevPageUri, "prev"));
        }

        if (pageNum < totalPages) {
            String nextPageUri = getUriString(baseUri, pageNum + 1);
            this.add(Link.of(nextPageUri, "next"));

            String lastPageUri = getUriString(baseUri, totalPages);
            this.add(Link.of(lastPageUri, "last"));
        }
    }

    private String getUriString(String baseUri, int newPageNum) {
        return ServletUriComponentsBuilder.fromUriString(baseUri)
                .replaceQueryParam("pageNum", newPageNum)
                .replaceQueryParam("pageSize", pageSize)
                .toUriString();
    }
}
