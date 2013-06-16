<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
<title>FileUpload</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script
	src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script type="text/javascript">
$(function(){
  $("#multipletestButton").click(function(){
    var $form = $('#multipletestForm');
    var files = new FormData($form[0]);
    $.ajax('filepost', {
      type: 'post',
      processData: false,
      contentType: false,
      data: files,
      dataType: 'html',
      success: function(data){
    	//1ファイルしか返ってこない
    	var $img = $('<img>');
    	$img.attr({src:'data:image/png;base64,'+data});
        $('#resultImg').append($img);
      }
    });
  });
});
</script>
</head>
<body>
	<form id="multipletestForm" enctype="multipart/form-data" action="filepost" method="post">
		<input id="multipletestFile" type="file" name="multipletestFile" multiple="multiple" /> 
	</form>
	<button id="multipletestButton">send</button>
	<div id="resultImg" />
</body>
</html>