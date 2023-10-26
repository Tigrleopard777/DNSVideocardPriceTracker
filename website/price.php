<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
        <script src="chart.js-3.7.1/chart.js-3.7.1/package/dist/Chart.js"></script>
		<title>Мониторинг цены</title>
	</head>
	<body>
	<script>
	// Tags - это метки, которые идут по оси X.  
	const tags = [];
	const dataprices = {
    label: "Цена",
    data: [], // Данные представляют собой массив, который должен иметь такое же количество значений, как и количество тегов.
    backgroundColor: 'rgba(54, 162, 235, 0.2)', // Цвет фона
    borderColor: 'rgba(54, 162, 235, 1)', // Цвет границ
    borderWidth: 1,// Толщина границ
	};
	const pageWidth = document.documentElement.scrollWidth;
	</script>
	<div>
	<a id="a" style="float:left">
		<img src="home.png"
        alt="Домой"
        height="30"
        width="30"
		>
	</a>
	<h3 align="center"><?php echo($_GET['name'])?></h3>
	</div>
	<?php
	$dbuser = 'root';
	$dbpass = '1234';
	$host = 'localhost';
	$dbname='videocards';
	$db = mysqli_connect($host, $dbuser, $dbpass, $dbname);
	$query = "select price, time from price where id_videocard=".$_GET['id_videocard'];
	$result = mysqli_query($db, $query);
	$maxPrice=-1;
	$minPrice=-1;
	$averagePrice=-1;
	$sum=0;
	$count=0;
	$Prices=array();
	while($row=mysqli_fetch_array($result))
	{
		$count++;
		//print($row[0]." руб.<br>".$row[1]."<br><br>");
		array_push($Prices, $row[0]);
		if($maxPrice<=$row[0])
		{
			$maxPrice=$row[0];
		}
		if($minPrice>=$row[0]||$minPrice==-1)
		{
			$minPrice=$row[0];
		}
		echo "<script>tags.push(\"".$row[1]."\") </script>";
		echo "<script>dataprices.data.push(\"".$row[0]."\") </script>";
	}
	mysqli_close($db);
	$Prices=array_unique($Prices);
	foreach($Prices as $Price)
	{
		$sum=$sum+$Price;
	}
	unset($Price);
	$averagePrice=$sum/count($Prices);
	$db = mysqli_connect($host, $dbuser, $dbpass, $dbname);
	$query = "select memory_volume, memory_type, tech_procces, power from videocard where id_videocard=".$_GET['id_videocard'];
	$result=mysqli_query($db, $query);
	while($row=mysqli_fetch_array($result))
	{
		$memory_vol=$row[0];
		$memory_type=$row[1];
		$tech_procces=$row[2];
		$power=$row[3];
		
	}
	mysqli_close($db);
	?>
	<div style="display: flex; justify-content: space-between; width:90%">
		<div>
			<h4>Максимальная цена: <?php echo($maxPrice)?> руб.</h4>
			<h4>Минимальная цена: <?php echo($minPrice)?> руб.</h4>
			<h4>Средняя цена: <?php echo(round($averagePrice, 2))?> руб.</h4>
		</div>
		<div>
			<h4 align="left">Объём памяти: <?php echo($memory_vol)?> Гб</h4>
			<h4 align="left">Тип памяти: <?php echo($memory_type)?></h4>
			<h4 align="left">Техпроцесс: <?php echo($tech_procces)?> нм</h4>
			<h4 align="left">Рекомендованная мощность: <?php echo($power)?> Вт</h4>
		</div>
	</div>
	<!-- Определение местоположения и размера диаграммы -->
       <div id="container" style="border:1px solid #ccc;
                           width:100%;height:35%;">
            <canvas id="grafica" 
                    width="90%" height="20%"></canvas>
       </div>
<script>

const host=document.location.host;
var a = document.getElementById('a');
a.setAttribute('href', 'http://'+host+'/videocard/videocards.php');
const $grafica = document.querySelector("#grafica");
$grafica.style.width=pageWidth;
new Chart($grafica, {
    type: 'line',// Тип графика
    data: {
        labels: tags,
        datasets: [
            dataprices
        ]
    },
});

       </script> 
	</body>
</html>