package com.yakgwa.catchme.dto.matching;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * List 형태로 저장시
 *
* Listener 에서 역직렬화 오류 발생
 * 몇 시간 동안 찾아보고 시도했으나 실패
 *
 * JsonCreator, JsonProperty 등 시도
 * 결과적으로는 Member에 setter 빠짐..
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MatchingMessage {
    private List<MatchingMemberDto> matchingMembers = new ArrayList<>();

    public MatchingMessage(List<MatchingMemberDto> members) {
        this.matchingMembers= members;
    }
}