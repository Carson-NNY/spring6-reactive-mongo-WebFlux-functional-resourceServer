package carson.springframework.spring6reactivemongo.webFn;

import carson.springframework.spring6reactivemongo.model.BeerDTO;
import carson.springframework.spring6reactivemongo.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.validation.Validator;


// note: 3种techniques to implement APIs:  Spring Web MVC,  Spring WebFlux, Spring WebFlux fn  (这个目前比较新, 用的人还不多)
@Component
@RequiredArgsConstructor
public class BeerHandler {
    private final BeerService beerService;

    public final Validator validator;

    private void validate(BeerDTO beerDTO) {
        Errors errors  = new BeanPropertyBindingResult(beerDTO, "beerDTO");
        validator.validate(beerDTO, errors);

        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> deleteBeerById(ServerRequest request) {
        return beerService
                .getById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> beerService.deleteBeerById(beerDTO.getId()))
                .then(ServerResponse.noContent().build()); // since the beerService.deleteBeerById reuturns Mono<Void>,
        //Since deleteBeerById doesn't emit any value, you can't pass a result to the next operation directly. Instead, .then() is used to say, "Once the deletion is done, proceed to this next operation," which in this case is building a ServerResponse.
        // and if there is an error, it will transmit the error to the next operation is the stream is not reset so that we can spot the error
    }

    public Mono<ServerResponse> patchBeerById(ServerRequest request) {
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)  // call the validate method to validate the input
                .flatMap(beerDTO -> beerService.patchBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateBeerById(ServerRequest request) {
        //flatMap: The flatMap operator is used to chain asynchronous operations. It takes the BeerDTO extracted from the request body and performs an update operation
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)
                .flatMap(beerDTO -> beerService
                        .updateBeer(request.pathVariable("beerId"), beerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request) {
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class).doOnNext(this::validate)
                ).flatMap(beerDto -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BeerRouterConfig.BEER_PATH_ID)
                                .build(beerDto.getId()))
                        .build());
    }


    public Mono<ServerResponse> getBeerById(ServerRequest request) {
       return ServerResponse
               .ok()
               .body(beerService.getById(request.pathVariable("beerId"))
                               .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                       BeerDTO.class);
    }

    public Mono<ServerResponse> listBeers(ServerRequest request) {
        Flux<BeerDTO> flux;

        if(request.queryParam("beerStyle").isPresent()) {
            flux = beerService
                    .findByBeerStyle(request.queryParam("beerStyle").get());
        }else {
            flux = beerService.listBeers();
        }
        return ServerResponse.ok()
                .body(flux, BeerDTO.class);
    }

}
