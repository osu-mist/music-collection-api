--------------------------------------------------------
--  File created - Wednesday-May-25-2016   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table MUS_ALBUM
--------------------------------------------------------

  CREATE TABLE "MUS_ALBUM" 
   (	"ID" NUMBER, 
	"ARTIST_ID" NUMBER, 
	"TITLE" VARCHAR2(999), 
	"EDITION" VARCHAR2(999), 
	"RELEASED" DATE, 
	"CREATED" DATE, 
	"STATUS" NUMBER
   ) ;
--------------------------------------------------------
--  DDL for Table MUS_ARTIST
--------------------------------------------------------

  CREATE TABLE "MUS_ARTIST" 
   (	"ID" NUMBER, 
	"NAME" VARCHAR2(999), 
	"CREATED" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table MUS_SHELF
--------------------------------------------------------

  CREATE TABLE "MUS_SHELF" 
   (	"ID" NUMBER, 
	"NAME" VARCHAR2(100), 
	"CREATED" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table MUS_SHELF_ALBUM_MAP
--------------------------------------------------------

  CREATE TABLE "MUS_SHELF_ALBUM_MAP" 
   (	"SHELF_ID" NUMBER, 
	"ALBUM_ID" NUMBER, 
	"CREATED" DATE
   ) ;
--------------------------------------------------------
--  DDL for Table MUS_STATUS
--------------------------------------------------------

  CREATE TABLE "MUS_STATUS" 
   (	"ID" NUMBER, 
	"NAME" VARCHAR2(100)
   ) ;
--------------------------------------------------------
--  DDL for Index MUS_ALBUM_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MUS_ALBUM_PK" ON "MUS_ALBUM" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index MUS_ARTIST_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MUS_ARTIST_PK" ON "MUS_ARTIST" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index MUS_SHELF_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MUS_SHELF_PK" ON "MUS_SHELF" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index MUS_SHELF_ALBUM_MAP_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MUS_SHELF_ALBUM_MAP_PK" ON "MUS_SHELF_ALBUM_MAP" ("SHELF_ID", "ALBUM_ID") 
  ;
--------------------------------------------------------
--  DDL for Index MUS_STATUS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MUS_STATUS_PK" ON "MUS_STATUS" ("ID") 
  ;
--------------------------------------------------------
--  Constraints for Table MUS_ALBUM
--------------------------------------------------------

  ALTER TABLE "MUS_ALBUM" ADD CONSTRAINT "MUS_ALBUM_PK" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "MUS_ALBUM" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MUS_ARTIST
--------------------------------------------------------

  ALTER TABLE "MUS_ARTIST" ADD CONSTRAINT "MUS_ARTIST_PK" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "MUS_ARTIST" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MUS_SHELF
--------------------------------------------------------

  ALTER TABLE "MUS_SHELF" ADD CONSTRAINT "MUS_SHELF_PK" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "MUS_SHELF" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MUS_SHELF_ALBUM_MAP
--------------------------------------------------------

  ALTER TABLE "MUS_SHELF_ALBUM_MAP" ADD CONSTRAINT "MUS_SHELF_ALBUM_MAP_PK" PRIMARY KEY ("SHELF_ID", "ALBUM_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "MUS_SHELF_ALBUM_MAP" MODIFY ("CREATED" NOT NULL ENABLE);
  ALTER TABLE "MUS_SHELF_ALBUM_MAP" MODIFY ("ALBUM_ID" NOT NULL ENABLE);
  ALTER TABLE "MUS_SHELF_ALBUM_MAP" MODIFY ("SHELF_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MUS_STATUS
--------------------------------------------------------

  ALTER TABLE "MUS_STATUS" ADD CONSTRAINT "MUS_STATUS_PK" PRIMARY KEY ("ID")
  USING INDEX  ENABLE;
  ALTER TABLE "MUS_STATUS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "MUS_STATUS" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table MUS_ALBUM
--------------------------------------------------------

  ALTER TABLE "MUS_ALBUM" ADD CONSTRAINT "MUS_ALBUM_FK1" FOREIGN KEY ("ARTIST_ID")
	  REFERENCES "MUS_ARTIST" ("ID") ENABLE;
  ALTER TABLE "MUS_ALBUM" ADD CONSTRAINT "MUS_ALBUM_FK2" FOREIGN KEY ("STATUS")
	  REFERENCES "MUS_STATUS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table MUS_SHELF_ALBUM_MAP
--------------------------------------------------------

  ALTER TABLE "MUS_SHELF_ALBUM_MAP" ADD CONSTRAINT "MUS_SHELF_ALBUM_MAP_FK1" FOREIGN KEY ("SHELF_ID")
	  REFERENCES "MUS_SHELF" ("ID") ENABLE;
  ALTER TABLE "MUS_SHELF_ALBUM_MAP" ADD CONSTRAINT "MUS_SHELF_ALBUM_MAP_FK2" FOREIGN KEY ("ALBUM_ID")
	  REFERENCES "MUS_ALBUM" ("ID") ENABLE;
--------------------------------------------------------
--  DDL for Trigger MUS_ALBUM_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "MUS_ALBUM_TRG" 
BEFORE INSERT ON MUS_ALBUM 
FOR EACH ROW 
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.ID IS NULL THEN
      SELECT MUS_ALBUM_SEQ.NEXTVAL INTO :NEW.ID FROM SYS.DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;
/
ALTER TRIGGER "MUS_ALBUM_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger MUS_ARTIST_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "MUS_ARTIST_TRG" 
BEFORE INSERT ON MUS_ARTIST 
FOR EACH ROW 
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.ID IS NULL THEN
      SELECT MUS_ARTIST_SEQ.NEXTVAL INTO :NEW.ID FROM SYS.DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;
/
ALTER TRIGGER "MUS_ARTIST_TRG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger MUS_SHELF_TRG
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "MUS_SHELF_TRG" 
BEFORE INSERT ON MUS_SHELF 
FOR EACH ROW 
BEGIN
  <<COLUMN_SEQUENCES>>
  BEGIN
    IF INSERTING AND :NEW.ID IS NULL THEN
      SELECT MUS_SHELF_SEQ.NEXTVAL INTO :NEW.ID FROM SYS.DUAL;
    END IF;
  END COLUMN_SEQUENCES;
END;
/
ALTER TRIGGER "MUS_SHELF_TRG" ENABLE;
