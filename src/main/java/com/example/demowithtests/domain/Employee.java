package com.example.demowithtests.domain;


import com.example.demowithtests.util.validation.annotation.CountryMatchesAddresses;
import com.example.demowithtests.util.validation.annotation.IsBooleanFieldValid;
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
@CountryMatchesAddresses
public class Employee /*implements Cloneable*/ {

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

    @IsBooleanFieldValid(value = false, message = "Check the user's \"isDeleted\" field. " +
            "Employee marked as deleted (isDeleted=true)!")
    private Boolean isDeleted = Boolean.FALSE;

    //    @IsBooleanFieldValid(value = false, message = "Check the user's   \"isPrivate\" field")
    private Boolean isPrivate = Boolean.FALSE;

    /**
     * Не бизнесовое поле:
     * после заполнения данных о работнике, письмо с подверждением  данных отправляется на
     * указанный email. Пока работник их не подтвердит (ссылка в письме отправляет запрос на сервер),
     * его учетная запись будет неактивна в системе.
     */
//    @IsBooleanFieldValid(value = true, message = "Check the user's   \"isConfirmed\" field")
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

