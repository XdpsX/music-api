package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.enums.Gender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ArtistCriteriaRepositoryImplTest {
    @Autowired
    private ArtistRepository artistRepository;

    @BeforeAll
    void setUp() {
        Artist artist1 = Artist.builder().name("John Doe").gender(Gender.MALE).build();
        artistRepository.save(artist1);
        Artist artist2 = Artist.builder().name("Jane Smith").gender(Gender.FEMALE).build();
        artistRepository.save(artist2);
        Artist artist3 = Artist.builder().name("John Smith").gender(Gender.MALE).build();
        artistRepository.save(artist3);
    }

    @Test
    void testFindWithFiltersByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, "John", null, null);

        assertThat(page.getTotalElements()).isEqualTo(2);
        List<Artist> artists = page.getContent();
        assertThat(artists).extracting("name").containsExactlyInAnyOrder("John Doe", "John Smith");
    }

    @Test
    void testFindWithFiltersByGender() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, null, null, Gender.FEMALE);

        assertThat(page.getTotalElements()).isEqualTo(1);
        List<Artist> artists = page.getContent();
        assertThat(artists).extracting("name").containsExactly("Jane Smith");
    }

    @Test
    void testFindWithFiltersByNameAndGender() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, "Smith", null, Gender.FEMALE);

        assertThat(page.getTotalElements()).isEqualTo(1);
        List<Artist> artists = page.getContent();
        assertThat(artists).extracting("name").containsExactly("Jane Smith");
    }

    @Test
    void testSortingByNameAsc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, null, "name", null);

        assertThat(page.getTotalElements()).isEqualTo(3);
        List<Artist> artists = page.getContent();
//        assertThat(artists).extracting("name").containsExactly("Jane Smith", "John Doe", "John Smith");
        assertThat(artists).isSortedAccordingTo(Comparator.comparing(Artist::getName));
    }

    @Test
    void testSortingByNameDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, null, "-name", null);

        assertThat(page.getTotalElements()).isEqualTo(3);
        List<Artist> artists = page.getContent();
        assertThat(artists).isSortedAccordingTo((a1, a2) -> a2.getName().compareTo(a1.getName()));
    }

    @Test
    void testSortingByDateAsc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, null, "date", null);

        assertThat(page.getTotalElements()).isEqualTo(3);
        List<Artist> artists = page.getContent();
        assertThat(artists).isSortedAccordingTo(Comparator.comparing(Artist::getCreatedAt));
    }

    @Test
    void testSortingByDateDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = artistRepository.findWithFilters(pageable, null, "-date", null);

        assertThat(page.getTotalElements()).isEqualTo(3);
        List<Artist> artists = page.getContent();
        assertThat(artists).isSortedAccordingTo((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
    }
}