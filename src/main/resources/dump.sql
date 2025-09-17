create table doctors
(
    id         bigint auto_increment
        primary key,
    first_name varchar(255) null,
    last_name  varchar(255) null,
    time_zone  varchar(255) null
);

create table patients
(
    id         bigint auto_increment
        primary key,
    first_name varchar(255) null,
    last_name  varchar(255) null
);

create table visits
(
    doctor_id  bigint      null,
    end        datetime(6) null,
    id         bigint auto_increment
        primary key,
    patient_id bigint      null,
    start      datetime(6) null,
    constraint FKra5p2e0tp6djolm46kdr42cyt
        foreign key (patient_id) references patients (id),
    constraint FKth95fndjk3y3nepjfu3f66r63
        foreign key (doctor_id) references doctors (id)
);

