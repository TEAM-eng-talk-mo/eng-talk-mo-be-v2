package com.engtalkmo.config.oauth;

import com.engtalkmo.config.OAuth2Attribute;
import com.engtalkmo.domain.user.User;
import com.engtalkmo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 요청을 바탕으로 OAuth2User 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Client 등록 ID(Google, Naver, KAKAO)와 사용자 이름 속성을 가져온다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService 를 사용하여 가져온 OAuth2User 정보로 OAuth2Attribute 를 만든다.
        OAuth2Attribute attributes = OAuth2Attribute.of(
                registrationId, userNameAttributeName, oAuth2User.getAttributes());

        log.info("OAuth2Attribute >>>> {}", attributes);

        // attributes 를 통해 회원이 존재 유무에 따라 저장 또는 업데이트를 한다.
        User user = saveOrUpdate(attributes);

        // attributes 를 이용하여 권한, 속성, 이름을 이용해 DefaultOAuth2User 를 생성해 반환한다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.attributes(),
                attributes.attributeKey());
    }

    private User saveOrUpdate(OAuth2Attribute attributes) {
        return userRepository.findByEmail(attributes.email())
                .map(entity -> entity.update(attributes.name(), attributes.picture()))
                .orElse(userRepository.save(attributes.toEntity()));
    }
}
