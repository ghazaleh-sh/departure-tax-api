package ir.co.sadad.departuretaxapi.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author G.ShahrokhAbadi on 19/7/2023
 */
@Data
@Entity
@Table(name = "DEPARTURE_GROUP_TYPE")
public class DepartureGroupType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "PRIORITY")
    private Integer priority;
    @Column(name = "GROUP_CODE")
    private int code;
    @Column(name = "GROUP_NAME")
    private String name;
    @Column(name = "GROUP_TYPE")
    private String type;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    @Column(name = "VISIBILITY")
    private Boolean visibility;
    @Column(name = "DEACTIVATION_MESSAGE")
    private String deactivationMessage;
    @Column(name = "LOCALIZED_DEACTIVATION_MESSAGE")
    private String localizedDeactivationMessage;
}
