drop table if exists Client;
drop table if exists OrderLine;
drop table if exists SalesOrder;

drop table if exists Account;
drop table if exists JournalEntry;
drop table if exists Journal;



-- Tables
create table Client(clientCode varchar(60) not null, name varchar(255) not null, street varchar(255) not null, zip int not null, city varchar(255) not null, countryCode varchar(3) not null, primary key (clientCode));
create table SalesOrder(orderCode varchar(60) not null, forCustomerCode varchar(60) not null, state varchar(30) not null, primary key (orderCode));
create table OrderLine (id bigint not null auto_increment, lineId int not null, item varchar(255) not null, price double not null, currencyCode varchar(3) not null, lines_orderCode varchar(60), primary key (id), foreign key (lines_orderCode) references SalesOrder(orderCode));

create table Account(id bigint not null auto_increment, name varchar(60) not null, solde double not null, primary key(id));
create table Journal(id bigint not null auto_increment, forAccountName varchar(60) not null, primary key(id));
create table JournalEntry(id bigint not null auto_increment, journal_id bigint, somme double not null, solde double not null, operation varchar(20) not null, recordDate timestamp not null, primary key(id), foreign key (journal_id) references Journal(id));

-- Data
insert into Account (name, solde) values ('test-account-0', 0.0);
insert into Journal (forAccountName) values ('test-account-0');
