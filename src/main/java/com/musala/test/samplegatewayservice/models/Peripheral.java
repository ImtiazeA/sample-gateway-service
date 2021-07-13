package com.musala.test.samplegatewayservice.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "peripherals")
public class Peripheral extends AuditModel {

    @Id
    @GeneratedValue(generator = "peripheral_id_generator")
    @GenericGenerator(
            name = "peripheral_id_generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "peripheral_id_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    private String name;

    private String vendor;

    @NotNull
    @Column(name = "gateway_id")
    private String gatewayId;

    @Enumerated(value = EnumType.STRING)
    private PeripheralStatus status;
}
