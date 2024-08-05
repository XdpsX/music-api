package com.xdpsx.music.model.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LikeId implements Serializable {
    private Long userId;
    private Long trackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LikeId likeId = (LikeId) o;

        if (!userId.equals(likeId.userId)) return false;
        return trackId.equals(likeId.trackId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + trackId.hashCode();
        return result;
    }
}
