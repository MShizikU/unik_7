package tvkp.prakt4.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/logs")
public class GrayLogController {

    @Value("${graylog.url}")
    private String graylogUrl;

    @Value("${graylog.username}")
    private String username;

    @Value("${graylog.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/export-logs")
    public ResponseEntity<ByteArrayResource> exportLogs() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime fiveMinutesAgo = now.minusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String url = UriComponentsBuilder.fromHttpUrl(graylogUrl + "/api/search/universal/absolute/export")
                .queryParam("query", "")
                .queryParam("fields", "timestamp,source,message")
                .queryParam("streams", "000000000000000000000001")
                .queryParam("from", fiveMinutesAgo.format(formatter))
                .queryParam("to", now.format(formatter))
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setAccept(MediaType.parseMediaTypes("text/csv"));

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);

        if (response.getBody() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ByteArrayResource resource = new ByteArrayResource(response.getBody());

        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String filename = contentDisposition != null ? contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1") : "graylog_export.csv";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}