package kr.co.backend.dto.User;


import kr.co.backend.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDto {
    private String city;
    private String street;
    private String zipcode;

    public Address toEntity() {
        return new Address(this.city, this.street, this.zipcode);
    }

    public static AddressDto fromEntity(Address address) {
        return new AddressDto(address.getCity(), address.getStreet(), address.getZipcode());
    }
}
