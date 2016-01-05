<?php 
$data['md5_hash']  = md5_file( '../timetable/varun.db');
echo json_encode($data);
?>