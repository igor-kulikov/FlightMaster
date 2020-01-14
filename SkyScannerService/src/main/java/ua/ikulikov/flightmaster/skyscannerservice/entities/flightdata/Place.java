package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@JsonIgnoreProperties({"ParentId"})
@Table(schema = "SKY_SCANNER", name = "REF_PLACES")
@Entity
public class Place {
    @JsonProperty("Id")
    @Id
    @Column(name = "PLACE_ID")
    private Integer id;

    @JsonProperty("Code")
    @Column(name = "CODE")
    private String code;

    @JsonProperty("Type")
    @Column(name = "TYPE")
    private String type;

    @JsonProperty("Name")
    @Column(name = "NAME")
    private String name;
}
