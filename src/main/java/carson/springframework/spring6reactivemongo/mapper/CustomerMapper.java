package carson.springframework.spring6reactivemongo.mapper;


import carson.springframework.spring6reactivemongo.domain.Customer;
import carson.springframework.spring6reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerDTO customerToCustomerDto(Customer customer);

    Customer customerDtoToCustomer(CustomerDTO customerDTO);

}
