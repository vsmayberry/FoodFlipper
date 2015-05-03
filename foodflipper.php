<?php
/*
foodflipper.php
Author: Joshua Solomon

	API that the FoodFlipper Andoid App uses to communicate with the FoodFlipper database
	The API uses HTTP POST requests. 
		o: Operation
			The specific API function to run
		a: the arguments to pass to each function stored as an array.
*/



/*
 * Login and user functions
 */
 
 
 
 
/*
 * Attempt Login 
 *
 * 	Params:
 *		$args[0] - email sent from android device
 *		$args[1] - plaintext password sent from android device
 *	Returns:
 *		If 2 values are not set in $args:
 *			"INVALID ARGS."
 *		If the email is not in the users table:
 *			"THIS EMAL IS NOT IN OUR SYSTEM."
 *		If the password is incorrect:
 *			"INCORRECT PASSWORD."
 *		If the login was successful:
 *		String of JSON  containing "["uid", "email", "password"]"
 */

function attemptLogin($args, $db){
	
	// Check args
	if(count($args) != 2){
		return "INVALID ARGS.";
	}
	
	// Select query
	$query = $db->prepare("SELECT `uid`, `email`, `password`
						   FROM `users`
						   WHERE `email` = :email");
    $params = array(
        ':email' => $args[0],
    );
    $query->execute($params);
    $results = $query->fetch();

	// Check emai;
	if(empty($results)){
		return "THIS EMAIL IS NOT IN OUR SYSTEM.";	
	}

	// Checks a password hashed using password_hash
    if(!password_verify( $args[1], $results['password'])){  
		return "INCORRECT PASSWORD.";	
	}
	
	$return = array($results['uid'], $args[0], $args[1]);
	return json_encode($return);
}//end attemptLogin


/*
 * Insert User
 *
 * 	Params:
 *		$args[0] - email sent from android device
 *		$args[1] - plaintext password sent from android device
 *	Returns:
 *		If 2 values are not set in $args:
 *			"INVALID ARGS."
 *		If the email is already in the users table:
 *			"THIS EMAL IS ALREADY IN OUR SYSTEM."
 *		If the email is invalid:
 *			"INVAID email."
 *		If the password is invalid:
 *			"INVAID PASSWORD."
 *		If the insert was unsuccessful:
 *			"FAILED TO ADD ACCOUNT, PLEASE TRY AGAIN."
 *		If the insert was successful:
 *			calls and returns attemptLogin($args[0], $args[2])
 */

function insertUser($args, $db){
	
	// Check args
	if(count($args) != 2){
		return "INVALID ARGS.";
	}
	
	// Check email
	if(filter_var($args[0], FILTER_VALIDATE_EMAIL) === false){
		return "INVALID EMAIL.";
	}
	
	// Check password
	if(strlen($args[1]) <= 4){
		return "INVALID PASSWORD.";
	}
	
	// Select query
	$selectQuery = $db->prepare("SELECT `email`
						   FROM `users`
						   WHERE `email` = :email");
    $selectParams = array(
        ':email' => $args[0],
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetch();

	// Check if email is already in use
	if(!empty($selectResults)){
		return "THIS EMAIL IS ALREADY IN OUR SYSTEM.";	
	}
	
	// Insert Quuery
	$insertQuery = $db->prepare("INSERT INTO `users` (`email`, `password`)
						   VALUES (:email, :password)");
    $params = array(
        ':email' 	=> $args[0],
		':password' => password_hash($args[1], PASSWORD_DEFAULT),
    );
	
	// Check insert success
    if($insertQuery->execute($params)){
		
		// Attempt login to get uid, username and password
		$return = attemptLogin($args, $db);
		return $return; //no json encoding, it's done in attemptLogin
	}
	
	return "FAILED TO ADD ACCOUNT, PLEASE TRY AGAIN.";
}//end insertUser








/*
 * Food functions
 */
 
 
 
 
 /*
 * Insert Food
 *
 * 	Params:
 *		$args[0] - int: uid of creator
 *		$args[1] - String: Name of food
 *		$args[2] - int: Calories
 *		$args[3] - int: Carbs
 *		$args[4] - int: Fat 
 *		$args[5] - int: Protein
 *		$args[6] - Blob: Image 
 *	Returns:
 *		If 7 values are not set in $args:
 *			"INVALID ARGS."
 *		If the uid isn't in the users table:
 *			"INVALID USER."
 *		If the insert was unsuccessful:
 *			"FAILED TO ADD FOOD, PLEASE TRY AGAIN"
 *		If the insert was successful:
 *			calls and returns selectFood($args[0])
 */

function insertFood($args, $db){
	
	// Check args
	if(count($args) != 7){
		return "INVALID ARGS.";
	}
	
	// Select query
	$selectQuery = $db->prepare("SELECT `uid`
						   FROM `users`
						   WHERE `uid` = :uid");
    $selectParams = array(
        ':uid' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetch();

	// Check if UID exists in users table
	if(empty($selectResults)){
		return "IVALID USER.";	
	}
	
	// Insert Quuery
	$insertQuery = $db->prepare("INSERT INTO `food` (`uid`, `name`, `calories`, `carbs`, `fat`, `protein`, `image`)
						   VALUES (:uid, :name, :calories, :carbs, :fat, :protein, :image)");
    $params = array(
        ':uid' 		=> $args[0],
		':name'		=> $args[1],
		':calories' => $args[2],
		':carbs' 	=> $args[3],
		':fat' 		=> $args[4],
		':protein' 	=> $args[5],
		':image' 	=> $args[6], // TODO: BLOBS, HOW
    );
	
	// Check insert success
    if($insertQuery->execute($params)){
		return "SUCCESSFULLY ADDED!";
	}
	
	return "FAILED TO ADD FOOD, PLEASE TRY AGAIN.";
}//end insertFood



 /*
 * Select Food By User
 *
 * 	Params:
 *		$args[0] - int: uid of creator
 *
 *	Returns:
 *		If 1 value are not set in $args:
 *			"INVALID ARGS."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD FOOD, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user's food objects
 */

function selectFoodByUser($args, $db){
	
	// Check args
	if(count($args) != 1){
		return "INVALID ARGS.";
	}
	
	// Select query
	$selectQuery = $db->prepare("SELECT *
						   FROM `food`
						   WHERE `uid` = :uid");
    $selectParams = array(
        ':uid' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetch();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD FOOD, PLEASE TRY AGAIN.";
	}
	
	return json_encode($selectResults);
}//end selectFoodByUser




 /*
 * Select Food For Game
 *
 *	Returns a somewhat random selection from the food table.
 *	
 *
 * 	Params:
 *		$args[0] - the number of food items to return
 *
 *	Returns:
 *		If 1 value are not set in $args:
 *			"INVALID ARGS."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD FOOD, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user's food objects
 */
 
function selectFoodForGame($args, $db){
	
	// Check args
	if(count($args) != 1){
		return "INVALID ARGS.";
	}
	
	// Count query
	$countQuery = $db->prepare("SELECT COUNT(*) FROM `food`");
	$countQuery->execute();
    $countResults = $countQuery->fetch();
	$count = 0 + $countResults[0]; //string to int
	
	// Figure out how many items to return
	$start 	= -1;
	$size = 0 + $args[0];
	
	if($count > $size){
		// Select from a random positon between 0
		// and $size less than the $count
		$start = rand(0, $count - $size);
	}else{
		// Gotta select 'em all
		$start 	= 0;
		$size	= $count;
	}
	
	// Figure out what to order by
	$order = "";
	switch ($start % 6) {
    case 0:
        $order = "`uid`";
        break;
    case 1:
        $order = "`fid`";
        break;
    case 2:
        $order = "`calories`";
        break;
	case 3:
        $order = "`carbs`";
        break;
	case 4:
        $order = "`fat`";
        break;
	case 5:
        $order = "`protein`";
        break;
	}
	

	// Select query
	$selectQuery = $db->prepare("SELECT *
						   		 FROM `food`
						   		 ORDER BY :order
						   		 LIMIT :start, :end");
    $selectParams = array(
        ':order' => $order,
		':start' => $start,
		':end'	 => ($start + $size),
    );
	
	
	
	echo("<br/>order: $order, start: $start, end: " . ($start + $size) .  "<br/>");
	
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetch();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD FOOD, PLEASE TRY AGAIN.";
	}
	
	return json_encode($selectResults);
}//end selectFoodForGame



/*
 * Code that runs upon loading the page:
 *
 * Runs the function specified in the "o" parameter in the POST
 * with argument array args specified in the "a" parameter in the POST
 * along with the $db object
 */

// Databse PDO object 
$hostname = "jsxshq.com";
$database = "jsxshq7_397proj";
$username = "jsxshq7_397proj";
$password = "jsxshq7_397proj";
$db = new PDO("mysql:host=localhost;dbname=jsxshq7_397proj", $username, $password);

// run the specified function
$operation = $_GET['o'];
$args = $_GET['a'];
echo($operation($args, $db));

// close DB connection
?>