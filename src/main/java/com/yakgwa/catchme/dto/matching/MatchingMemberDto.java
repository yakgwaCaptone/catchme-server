package com.yakgwa.catchme.dto.matching;


import com.yakgwa.catchme.domain.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MatchingMemberDto {
    private Long id;
    private Gender gender;

    public MatchingMemberDto(Long id, Gender gender) {
        this.id = id;
        this.gender = gender;
    }

}
