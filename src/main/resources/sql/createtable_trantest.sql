create table trantest (
    id number(5) NOT NULL,
    name varchar2(50),
    lasttime timestamp,
    constraint pk_trantest primary key(id)
);

create sequence trantest_seq start with 1 increment by 1 maxvalue 99999 cycle nocache;
