insert into personel_rol values(
(select id from personel where adi = 'ali'),
(select id from rol where adi = 'admin'));

insert into personel_rol values(
(select id from personel where adi = 'veli'),
(select id from rol where adi = 'user'));
