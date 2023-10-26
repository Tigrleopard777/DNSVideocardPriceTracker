<!DOCTYPE html>
<html>
	
	<head>
		<meta charset="UTF-8">
		<title>Список видеокарт</title>
	</head>
	<body>
	<input style="width:90%" id='myInput' onkeyup='searchTable()' type='text'>
	<br>
	
	<script>
	const pageHeight = document.documentElement.clientHeight;
	const pageWidth = document.documentElement.clientWidth;
	const $myInput = document.getElementById("myInput");
	if(pageHeight>pageWidth)
	{
		$myInput.style.height=pageHeight/10+"px";
	}
	else
	{
		$myInput.style.height=pageHeight/30+"px";
	}
	</script>
	<?php
	$dbuser = 'root';
	$dbpass = '1234';
	$host = 'localhost';
	$dbname='videocards';

	$db = mysqli_connect($host, $dbuser, $dbpass, $dbname);
	$query = 'select * from videocard';
	$result = mysqli_query($db, $query);
	$i=1;
	while($row=mysqli_fetch_array($result))
	{
		print("<a style=\"margin-top: 10px; margin-bottom: 10px; display:block\" href=\"price.php?id_videocard=".$row[0]."&name=".$row['name']."\">".$row['name']."</a>");
		$i++;
	}
	$count=$i;
	mysqli_close($db);
	?>
	<script>
	function searchTable() {
    var input, filter, found, i;
    input = document.getElementById("myInput");
    filter = input.value.toUpperCase();
	aw = document.querySelectorAll("a");
    for (i = 0; i < aw.length; i++) 
	{
        if (aw[i].innerHTML.toUpperCase().indexOf(filter) > -1) 
		{
			found = true;
		}
        if (found) 
		{
            aw[i].style.display = "block";
            found = false;
        } 
		else 
		{
            aw[i].style.display = "none";
        }
    }
}
	</script>
	</body>
</html>
