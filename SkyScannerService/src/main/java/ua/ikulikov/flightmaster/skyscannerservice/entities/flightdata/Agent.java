package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@JsonIgnoreProperties({"ImageUrl", "OptimisedForMobile"})
@Table(schema = "SKY_SCANNER", name = "REF_AGENTS")
@Entity
public class Agent {
    @JsonProperty("Id")
    @Id
    @Column(name = "AGENT_ID")
    private Integer id;

    @JsonProperty("Name")
    @Column(name = "NAME")
    private String name;

    @JsonProperty("Type")
    @Column(name = "TYPE")
    private String type;

    @JsonProperty("Status")
    @Column(name = "STATUS")
    private String status;
}
