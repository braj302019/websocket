var gulp = require('gulp'),
	del = require('del'),
	connect = require('gulp-connect');


var web_root = 'src/main/webapp';

gulp.task('clean', function() {
	return del([ web_root + '/app/ext', web_root + '/app/dist' ]);
});

gulp.task('move', function() {
	return gulp.src([ 'bower_components/**' ])
		.pipe(gulp.dest(web_root + '/app/ext'));
});

gulp.task('connect', function() {
	connect.server({
		root : './src/main/webapp',
		livereload : true
	})
});

gulp.task('default', [ 'clean', 'move' ], function() {});