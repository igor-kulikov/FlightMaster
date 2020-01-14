package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@JsonIgnoreProperties({"BookingDetailsLink"})
@Table(schema = "SKY_SCANNER", name = "ITINERARIES")
@Entity
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITINERARY_ID")
    private Long id;

    @JsonProperty("InboundLegId")
    @Column(name = "INBOUND_LEG_ID")
    private String inboundLegId;

    @JsonProperty("OutboundLegId")
    @Column(name = "OUTBOUND_LEG_ID")
    private String outboundLegId;

    @JsonProperty("PricingOptions")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITINERARY_ID", nullable = false)
    private List<PriceOption> pricingOptions;
}