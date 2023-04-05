package com.yakgwa.catchme.repository;

import com.yakgwa.catchme.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
