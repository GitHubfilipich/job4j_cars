ALTER TABLE car ADD COLUMN body_type_id int REFERENCES body_type(id);
ALTER TABLE car ADD COLUMN brand_id int REFERENCES brand(id);
ALTER TABLE car ADD COLUMN gearbox_id int REFERENCES gearbox(id);
ALTER TABLE car ADD COLUMN production_year int;
ALTER TABLE car ADD COLUMN mileage int;
ALTER TABLE car ADD COLUMN power int;
ALTER TABLE car ADD COLUMN used boolean;