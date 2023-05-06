package com.yakgwa.catchme.dto;

import com.yakgwa.catchme.domain.Mbti;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequestDto {
    String nickname;
    String introduction;
    String mbti;

    public Mbti getMbti() {
        // MBTI 없음
        if (!StringUtils.hasText(mbti)) {
            return Mbti.NONE;
        }

        switch (mbti) {
            case "ISTJ": return Mbti.ISTJ;
            case "ISTP": return Mbti.ISTP;
            case "ISFJ": return Mbti.ISFJ;
            case "ISFP": return Mbti.ISFP;

            case "INTJ": return Mbti.INTJ;
            case "INTP": return Mbti.INTP;
            case "INFJ": return Mbti.INFJ;
            case "INFP": return Mbti.INFP;

            case "ESTJ": return Mbti.ESTJ;
            case "ESTP": return Mbti.ESTP;
            case "ESFJ": return Mbti.ESFJ;
            case "ESFP": return Mbti.ESFP;

            case "ENTJ": return Mbti.ENTJ;
            case "ENTP": return Mbti.ENTP;
            case "ENFJ": return Mbti.ENFJ;
            case "ENFP": return Mbti.ENFP;
            default:
                return Mbti.NONE;
        }
    }
}
