package com.example.demowithtests.domain;


import com.example.demowithtests.util.validation.annotation.custom.CountryMatchesAddressesVerification;
import com.example.demowithtests.util.validation.annotation.custom.MarkedAsDeleted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@CountryMatchesAddresses- БУДУ ПОМНИТЬ! ЧТО В СЕРВИСАХ НЕЛЬЗЯ ЮЗАТЬ КАСТОМНЫЕ АННОТАЦИИ ВАЛИДАЦИИ ПОМЕЧЕННЫЕ @Constraint
// т.е. теже что для контроллеров.
@CountryMatchesAddressesVerification
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String country;

    private String email;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    @OrderBy("country asc, id desc")
    private Set<Address> addresses = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @MarkedAsDeleted(value = false)
    private Boolean isDeleted = Boolean.FALSE;

    private Boolean isPrivate = Boolean.FALSE;

    /**
     * Не бизнесовое поле:
     * после заполнения данных о работнике, письмо с подверждением  данных отправляется на
     * указанный email. Пока работник их не подтвердит (ссылка в письме отправляет запрос на сервер),
     * его учетная запись будет неактивна в системе.
     */
    private Boolean isConfirmed = Boolean.FALSE;

//----------------------------------------------------------------------------------------------------
//    @Override
//    public Employee clone() throws CloneNotSupportedException {
//        return new Employee(
//                this.id,
//                this.name,
//                this.country,
//                this.email,
//                this.addresses,
//                this.gender,
//                this.isDeleted,
//                this.isPrivate,
//                this.isConfirmed
//        );
}
