package com.midas.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final WebClient webClient;
    private final String API_KEY = "69TA1MjNmYjQtOWJiNC0";
    private final String BASE_URL = "https://numclass-api.nubefone.com/v2";

    public ApiProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    // Endpoint para consulta individual
    @GetMapping("/numbers/{number}")
    public ResponseEntity<String> getNumber(@PathVariable String number) {
        try {
            String response = webClient.get()
                    .uri("/numbers/{number}", number)
                    .header("X-Api-Key", API_KEY)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.<Throwable>error(new RuntimeException(errorBody)))
                    )
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Aquí puedes loguear el error y devolver una respuesta más específica
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno en el servidor: " + ex.getMessage());
        }
    }


    // Endpoint para consulta múltiple (bulk)
    @PostMapping("/bulk")
    public ResponseEntity<String> postBulk(@RequestBody String body) {
        String response = webClient.post()
                .uri("/bulk")
                .header("X-Api-Key", API_KEY)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.<Throwable>error(new RuntimeException(errorBody)))
                )
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
