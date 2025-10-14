ALTER TABLE personel_rol
  ADD CONSTRAINT fk_pr_personel
  FOREIGN KEY (personel_id) REFERENCES personel(id);

ALTER TABLE personel_rol
  ADD CONSTRAINT fk_pr_rol
  FOREIGN KEY (rol_id) REFERENCES rol(id);
