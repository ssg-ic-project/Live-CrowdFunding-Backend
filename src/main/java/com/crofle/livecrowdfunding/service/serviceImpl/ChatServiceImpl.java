package com.crofle.livecrowdfunding.service.serviceImpl;

import com.crofle.livecrowdfunding.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
                .onlyWholeWords()     // 전체 단어만 매치
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
        Collection<Emit> emits = trie.parseText(content);

        // 원본 문자열을 char 배열로 변환
        char[] chars = content.toCharArray();

        // 매칭된 모든 비속어를 '*'로 치환
        for (Emit emit : emits) {
            for (int i = emit.getStart(); i < emit.getEnd(); i++) {
                chars[i] = '*';
            }
        }

        return new String(chars);
    }
}
