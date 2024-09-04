package carson.springframework.spring6reactivemongo.mapper;

import carson.springframework.spring6reactivemongo.domain.Beer;
import carson.springframework.spring6reactivemongo.model.BeerDTO;

import org.mapstruct.Mapper;
/**
 * Created by jt, Spring Framework Guru.
 */
@Mapper
public interface BeerMapper {

    BeerDTO beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDTO beerDTO);
}