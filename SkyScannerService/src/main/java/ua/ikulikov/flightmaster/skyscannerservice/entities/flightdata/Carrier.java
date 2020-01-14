package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@JsonIgnoreProperties({"ImageUrl", "Code", "DisplayCode"})
@Table(schema = "SKY_SCANNER", name = "REF_CARRIERS")
@Entity
public class Carrier {
    @JsonProperty("Id")
    @Id
    @Column(name = "CARRIER_ID")
    private Integer id;

    @JsonProperty("Name")
    @Column(name = "NAME")
    private String name;
}
