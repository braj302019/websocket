var gulp = require('gulp'),
	del = require('del'),
	jshint = require('gulp-jshint'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    connect = require('gulp-connect');


var web_root = 'src/main/webapp';

gulp.task('clean', function() {
	return del([web_root + '/app/ext', web_root + '/app/dist']);
});

gulp.task('move', function(){
	return gulp.src(['bower_components/**'])
				.pipe(gulp.dest(web_root + '/app/ext'));
});

gulp.task('scripts', function(){
	return gulp.src(web_root + '/app/js/**/*.js')
				.pipe(jshint('.jshintrc'))
				.pipe(jshint.reporter('default'))
				.pipe(concat('main.js'))
				.pipe(uglify())
				.pipe(gulp.dest(web_root + '/app/dist'));
});

gulp.task('watch', function() {
	gulp.watch(web_root + '/app/js/**/*.js', ['scripts']);
});

gulp.task('connect', function() {
  connect.server({
    root: './src/main/webapp',
    livereload: true
  })
});

gulp.task('default', ['clean', 'move', 'scripts'], function() {
	
});

