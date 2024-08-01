package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Gender;
import com.xdpsx.music.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtistCriteriaRepositoryImplTest {
    @Autowired
    private ArtistRepository artistRepository;

    @BeforeAll
    void setUp() {
        Artist artist1 = Artist.builder()
                .name("Smile John")
                .gender(Gender.MALE)
                .description("A popular artist 1")
                .dob(LocalDate.of(1990, 1, 1))
                .avatar("avatar1.jpg")
                .build();
        artistRepository.save(artist1);

        Artist artist2 = Artist.builder()
                .name("John Doe1")
                .gender(Gender.FEMALE)
                .description("A popular artist 2")
                .dob(LocalDate.of(1992, 2, 2))
                .avatar("avatar2.jpg")
                .build();
        artistRepository.save(artist2);

        Artist artist3 = Artist.builder()
                .name("John Doe2")
                .gender(Gender.MALE)
                .description("A popular artist 3")
                .dob(LocalDate.of(1994, 4, 4))
                .avatar("avatar3.jpg")
                .build();
        artistRepository.save(artist3);

    }

    @Test
    void findWithFilters_shouldReturnFilteredArtists() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Artist> result = artistRepository.findWithFilters(pageRequest, "John", "name", Gender.MALE);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("John Doe2", result.getContent().get(0).getName());

        assertThat(result.getContent()).isSortedAccordingTo(Comparator.comparing(Artist::getName));
    }

    @Test
    void findWithFilters_whenNoFilters_shouldReturnAllArtists() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Artist> result = artistRepository.findWithFilters(pageRequest, null, null, null);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void findWithFilters_whenFilterByDateDesc_shouldSortArtistsByCreatedAtDesc() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Artist> result = artistRepository.findWithFilters(pageRequest, null, "-date", null);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals("John Doe2", result.getContent().get(0).getName());
        assertThat(result.getContent()).isSortedAccordingTo((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));

//        result.getContent().forEach(System.out::println);
    }
}