CREATE INDEX idx_auto_user_login_password ON auto_user(login, password);
CREATE INDEX idx_auto_post_created ON auto_post(created);
CREATE INDEX idx_auto_post_car_id ON auto_post(car_id);
CREATE INDEX idx_car_model_id ON car(model_id);
CREATE INDEX idx_auto_post_foto_post_id ON auto_post_foto(post_id);
CREATE INDEX idx_foto_file_path ON foto(file_path);