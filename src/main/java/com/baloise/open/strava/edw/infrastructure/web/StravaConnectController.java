package com.baloise.open.strava.edw.infrastructure.web;

import com.baloise.open.edw.infrastructure.kafka.model.ActivityDto;
import com.baloise.open.strava.client.ApiClient;
import com.baloise.open.strava.client.api.ActivitiesApi;
import com.baloise.open.strava.client.auth.OAuth;
import com.baloise.open.strava.client.model.SummaryActivityDto;
import com.baloise.open.strava.edw.domain.model.Activity;
import com.baloise.open.strava.edw.infrastructure.kafka.KafkaController;
import com.baloise.open.strava.edw.infrastructure.kafka.mapper.ActivityDtoMapper;
import com.baloise.open.strava.edw.infrastructure.web.config.StravaConfiguration;
import com.baloise.open.strava.edw.infrastructure.web.model.AuthorizationRequestDto;
import com.baloise.open.strava.edw.infrastructure.web.model.AuthorizationResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/strava")
@Slf4j
public class StravaConnectController {

    @Autowired
    private StravaConfiguration stravaConfiguration;

    @Autowired
    private KafkaController kafkaController;

    @GetMapping("/start")
    @Produces(MediaType.TEXT_HTML_VALUE)
    public String loginUrl(@RequestParam(name = "client_id") String clientId,
                           @RequestParam(name = "client_secret") String clientSecret) {
        return generateLoginHtmlPage(stravaConfiguration.getAuthorizationUrl() +
                "?client_id=" + clientId +
                "&redirect_uri=" + stravaConfiguration.getRedirectUrl() + "?client_id=" + clientId + "&client_secret=" + clientSecret +
                "&response_type=code" +
                "&approval_prompt=auto" +
                "&scope=read_all,profile:read_all,activity:read_all" +
                "&state=" + clientSecret);
    }

    @GetMapping("/auth")
    @Produces(MediaType.TEXT_HTML_VALUE)
    public String authenticate(@RequestParam(name = "client_id") String clientId,
                               @RequestParam String code,
                               @RequestParam String state,
                               @RequestParam(required = false) String scope,
                               HttpServletRequest httpRequest) {
        RestTemplate restTemplate = new RestTemplate();
        AuthorizationRequestDto request = AuthorizationRequestDto.builder()
                .clientId(clientId)
                .clientSecret(state)
                .code(code)
                .grantType("authorization_code")
                .build();
        HttpEntity<AuthorizationRequestDto> entity = new HttpEntity<>(request);
        ResponseEntity<AuthorizationResponseDto> response = restTemplate.exchange(stravaConfiguration.getTokenUrl(),
                HttpMethod.POST,
                entity,
                AuthorizationResponseDto.class);

        if (response.getStatusCodeValue() >= 300) {
            //todo: problem if we were not able to login
            return "FAILED to create login";
        } else {
            log.debug("Retrieved authorization: {}", response.getBody());
            return generatePostAuthenticationHtmlPage(httpRequest, clientId, Objects.requireNonNull(response.getBody()).getAccessToken());
        }
    }

    @GetMapping("/activities")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public List<Activity> getActivities(@RequestParam(name = "client_id") String clientId,
                                        @RequestParam(name = "access_token") String accessToken,
                                        @RequestParam(required = false) Integer page) {
        ApiClient client = new ApiClient();
        OAuth auth = (OAuth) client.getAuthentications().get(stravaConfiguration.getApiSecurity());
        auth.setAccessToken(accessToken);

        ActivitiesApi api = new ActivitiesApi(client);
        List<SummaryActivityDto> activities = api.getLoggedInAthleteActivities(null, null, page, null);
        log.debug("Found {} activities for client {}.", activities.size(), clientId);
        for (SummaryActivityDto currentActivity : activities) {
            log.debug("-> {}: {}", currentActivity.getStartDate(), currentActivity.getName());
        }
        publishActivities(activities);
        return com.baloise.open.strava.edw.infrastructure.web.mapper.ActivityDtoMapper.INSTANCE.map(activities);
    }

    /**
     * Publishes the given list of activities to the kafka topic:
     * first each entity will be converted following an AVRO schema.
     *
     * @param activities the activities that should be converted and published.
     */
    private void publishActivities(List<SummaryActivityDto> activities) {
        List<ActivityDto> kafkaActivities = ActivityDtoMapper.INSTANCE.map(activities);
        kafkaController.pushActivities(kafkaActivities);
    }

    /**
     * Provides the default activities URI using current base path and adds current access token.
     *
     * @param httpRequest the HTTP Request used to get the base path from.
     * @param clientId    the Strava client ID used to identify the account.
     * @param accessToken the access token that has been generated before.
     * @return the URI as string ready to be used.
     */
    private String createActivitiesUri(HttpServletRequest httpRequest,
                                       String clientId,
                                       String accessToken) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .replacePath("/strava/activities")
                .build()
                .toUriString();

        return baseUrl +
                "?client_id=" + clientId +
                "&access_token=" + accessToken +
                "&page=1";
    }

    /**
     * Provides the HTML code used to provide all REST calls that can now be processed to run authenticated requests
     * against Strava.
     *
     * @param httpRequest the HTTP Request used to get the base path from.
     * @param clientId    the Strava client ID used to identify the account.
     * @param accessToken the access token that has been generated before.
     * @return the HTML code as string.
     */
    private String generatePostAuthenticationHtmlPage(HttpServletRequest httpRequest,
                                                      String clientId,
                                                      String accessToken) {
        return "<html>" +
                "<body>" +
                "<h1>" + clientId + "</h1>" +
                "<ul>" +
                "<li><a target='_blank' href='" + createActivitiesUri(httpRequest, clientId, accessToken) + "'>Activities</a></li>" +
                "</ul>" +
                "</body>" +
                "</html>";
    }

    /**
     * Provides an HTML page we use to initiate the authentication process.
     *
     * @param loginUri the URI as string we need to start login process.
     * @return the HTML code as string.
     */
    private String generateLoginHtmlPage(String loginUri) {
        return "<html>" +
                "<body>" +
                "<a target='_blank' href='" + loginUri + "'>Login</a>" +
                "</body>" +
                "</html>";
    }
}
