--------------------------------------------------------
--  File created - Wednesday-May-25-2016   
--------------------------------------------------------
REM INSERTING into MUS_ALBUM
SET DEFINE OFF;
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (1,1,'Dark Side of the Moon','30th Anniversary SACD',to_date('03-MAR-71','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (2,4,'Insurgentes','CD/DVD-A',to_date('26-NOV-08','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (3,4,'Grace For Drowning','2CD',to_date('26-SEP-11','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (4,4,'The Raven that Refused to Sing (and other stories)','CD/DVD-V digibook',to_date('25-FEB-13','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (5,4,'Hand. Cannot. Erase.','CD/DVD-V digibook',to_date('27-FEB-15','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (6,4,'Cover Version 2003-2010',null,to_date('30-JUN-14','DD-MON-RR'),to_date('24-MAY-16','DD-MON-RR'),2);
Insert into MUS_ALBUM (ID,ARTIST_ID,TITLE,EDITION,RELEASED,CREATED,STATUS) values (7,3,'The Astonishing',null,null,null,3);
REM INSERTING into MUS_ARTIST
SET DEFINE OFF;
Insert into MUS_ARTIST (ID,NAME,CREATED) values (1,'Pink Floyd',to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_ARTIST (ID,NAME,CREATED) values (2,'Porcupine Tree',to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_ARTIST (ID,NAME,CREATED) values (3,'Dream Theater',to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_ARTIST (ID,NAME,CREATED) values (4,'Steven Wilson',to_date('24-MAY-16','DD-MON-RR'));
REM INSERTING into MUS_SHELF
SET DEFINE OFF;
Insert into MUS_SHELF (ID,NAME,CREATED) values (1,'Andrew''s Music',null);
REM INSERTING into MUS_SHELF_ALBUM_MAP
SET DEFINE OFF;
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,1,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,2,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,3,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,4,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,5,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,6,to_date('24-MAY-16','DD-MON-RR'));
Insert into MUS_SHELF_ALBUM_MAP (SHELF_ID,ALBUM_ID,CREATED) values (1,7,to_date('24-MAY-16','DD-MON-RR'));
REM INSERTING into MUS_STATUS
SET DEFINE OFF;
Insert into MUS_STATUS (ID,NAME) values (1,'none');
Insert into MUS_STATUS (ID,NAME) values (2,'have');
Insert into MUS_STATUS (ID,NAME) values (3,'want');
