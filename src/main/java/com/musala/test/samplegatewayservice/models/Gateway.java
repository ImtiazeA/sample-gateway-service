package com.musala.test.samplegatewayservice.models;

import com.musala.test.samplegatewayservice.validation.annotations.IpV4Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gateways")
public class Gateway extends AuditModel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    private String name;

    @IpV4Address
    @Column(name = "ip_v4_address")
    private String ipV4Address;

    /**
     * It's target usage is only when used in the service layer, it's transient and will not persist the collection as normally JPA does
     * */
    @Transient
    @Deprecated
    private List<Peripheral> peripherals;

}
