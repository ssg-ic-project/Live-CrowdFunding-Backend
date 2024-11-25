package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private Trie trie;

    @Value("#{'${chat.profanity.words}'.split(',')}")
    private List<String> profanityList;

    @PostConstruct
    public void initialize() {
        // 비속어 사전 초기화
        Trie.TrieBuilder builder = Trie.builder()
//                .onlyWholeWords()     // 전체 단어만 매치
                .ignoreCase();        // 대소문자 무시

        // 비속어 목록 추가
//        List<String> profanities = Arrays.asList(
//                "비속어1",
//                "비속어2"
//                // ... 더 많은 비속어 추가
//        );

        profanityList.forEach(log::info);

        profanityList.forEach(builder::addKeyword);

        trie = builder.build();

        // 로딩된 비속어 목록 로깅
        log.info("Loaded {} profanity words", profanityList.size());
        if (log.isDebugEnabled()) {
            log.debug("Profanity words: {}",profanityList.toString());
        }
    }

    public String filterProfanity(String content) {
        String normalizedContent = content.replaceAll("\\s+", "");
        Collection<Emit> emits = trie.parseText(normalizedContent);

        if (emits.isEmpty()) {
            log.info("No profanity found in: '{}'", content);
            return content;
        }

        // 원본 문자열에서 매칭된 위치 조정을 위한 매핑
        Map<Integer, Integer> positionMap = new HashMap<>();
        int normalizedPos = 0;
        for (int originalPos = 0; originalPos < content.length(); originalPos++) {
            if (!Character.isWhitespace(content.charAt(originalPos))) {
                positionMap.put(normalizedPos++, originalPos);
            }
        }

        // 원본 문자열 유지
        StringBuilder result = new StringBuilder(content);

        // 역순으로 처리하여 인덱스 변화 방지
        List<Emit> emitList = new ArrayList<>(emits);
        emitList.sort((e1, e2) -> Integer.compare(e2.getStart(), e1.getStart()));

        for (Emit emit : emitList) {
            int originalStart = positionMap.get(emit.getStart());
            int originalEnd = positionMap.get(emit.getEnd() - 1) + 1;
            String matchedText = content.substring(originalStart, originalEnd);
            String stars = "*".repeat(matchedText.length());

            log.info("Found profanity: '{}' at positions {}-{}",
                    matchedText, originalStart, originalEnd);
            result.replace(originalStart, originalEnd, stars);
        }

        String filtered = result.toString();
        log.info("Filtered result: '{}'", filtered);
        return filtered;
    }
}
