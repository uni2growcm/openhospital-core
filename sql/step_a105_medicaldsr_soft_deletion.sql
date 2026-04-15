alter table oh_medicaldsr 
add column mdsr_deleted char(1) not null default 'N' after mdsr_lock;
