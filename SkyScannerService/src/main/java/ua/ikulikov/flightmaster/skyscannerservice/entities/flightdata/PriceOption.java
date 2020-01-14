package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(schema = "SKY_SCANNER", name = "PRICE_OPTIONS")
public class PriceOption implements Comparable<PriceOption> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRICE_OPTION_ID")
    private Long id;

    @JsonProperty("Price")
    @Column(name = "PRICE")
    private int price;

    @JsonProperty("QuoteAgeInMinutes")
    @Column(name = "QUOTE_AGE_MM")
    private int quoteAgeInMinutes;

    @JsonProperty("Agents")
    @ElementCollection
    @CollectionTable(schema = "SKY_SCANNER", name = "PRICE_OPTION_AGENTS", joinColumns = @JoinColumn(name = "PRICE_OPTION_ID"))
    @Column(name = "AGENT_ID")
    private Set<Integer> agentIds;

    @JsonProperty("DeeplinkUrl")
    @Column(name = "DEEPLINK_URL")
    private String deeplinkUrl;

    @Override
    public int compareTo(PriceOption o) {
        return (price < o.getPrice()) ? -1 : 1;
    }
}