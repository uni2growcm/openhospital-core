-- reset admin password to 'Admin2022test!' without quotes.
update oh_user set us_passwd = '$2a$10$52y.1Y7ig9B6SQJy4hpPn.RscBZs7rh7fljh3GtC5RC8txi1O29NS' where (us_id_a = 'admin');
