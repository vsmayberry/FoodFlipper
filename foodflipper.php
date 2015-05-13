<?php
/*
foodflipper.php
Author: Joshua Solomon

	API that the FoodFlipper Andoid App uses to communicate with the FoodFlipper database
	The API uses HTTP POST requests. 
		o: Operation
			The specific API function to run
		a: the arguments to pass to each function stored as an array.
		
	NOTES: 
		Currently hosted at http://www.jsxshq.com/foodflipper.php
		
	User Functions: 
		- attemptLogin
		- insertUser
		- selectUser
		- updateUser
		- deleteUser
		//TODO: User Cascade Delete
			
	Food Functions: 
		- insertFood 
		- selectFoodForGame
		- selectFoodByUser
		- updateFood
		- flagFood
		- deleteFood
		
	Score Functions: 
		- insertScore
		- selectScores
		- selectScoreByDistance
		- selectScoresByUser
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
	if(count($args) != 2){ return "INVALID ARGS."; }
	
	// Select query
	$query = $db->prepare("SELECT `uid`, `email`, `password`
						   FROM `users`
						   WHERE `email` = :email");
    $params = array(
        ':email' => $args[0]
    );
    $query->execute($params);
    $results = $query->fetch();

	// Check emai;
	if(empty($results)){ return "THIS EMAIL IS NOT IN OUR SYSTEM."; }

	// Checks a password hashed using password_hash
    if(!password_verify( $args[1], $results['password'])){ return "INCORRECT PASSWORD."; }
	
	$return = array($results['uid'], $args[0], $args[1]);
	return json_encode($return);
}//end attemptLogin




/*
 * Insert User
 *
 * 	Params:
 *		$args[0] - email sent from android device
 *		$args[1] - plaintext password sent from android device
 *		$args[2] - String: address;
 *	Returns:
 *		If 3 values are not set in $args:
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
 *			calls and returns attemptLogin($args, $db)
 */

function insertUser($args, $db){
	
	// Check args
	if(count($args) != 3){ return "INVALID ARGS."; }
	
	// Check email
	if(filter_var($args[0], FILTER_VALIDATE_EMAIL) === false){ return "INVALID EMAIL."; }
	
	// Check password
	if(strlen($args[1]) <= 4){ return "INVALID PASSWORD."; }
	
	// Select query
	$selectQuery = $db->prepare("SELECT `email`
						   FROM `users`
						   WHERE `email` = :email");
    $selectParams = array(
        ':email' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if email is already in use
	if(!empty($selectResults)){ return "THIS EMAIL IS ALREADY IN OUR SYSTEM."; }
	
	// Insert Quuery
	$insertQuery = $db->prepare("INSERT INTO `users` (`email`, `password`, `address`)
						   VALUES (:email, :password, :address)");
    $params = array(
        ':email' 	=> $args[0],
		':password' => password_hash($args[1], PASSWORD_DEFAULT),
		':address'  => $args[2]
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
 * Select User
 *
 * 	Params:
 *		$args[0] - int: uid
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is negative:
 *			"INVALID USER."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD USER, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user object
 */

function selectUser($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
	//check size
	if($args[0] < 0){ return "INVALID UID."; }
	
	$size = $args[0];
	
	// Select query
	$selectQuery = $db->prepare("SELECT *
						   FROM `users`
						   WHERE `uid` = :uid");
    $selectParams = array(':uid' => $args[0]);
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD USER, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectUser


/*
 * Update User
 *
 * 	Params:
 *		$args[0] - int: uid
 *		$args[1] - String: email;
 *		$args[2] - String: password;
 *		$args[3] - String: address;
 *	Returns:
 *		If 4 values are not set in $args:
 *			"INVALID ARGS."
 *		If the email is already in the users table:
 *			"THIS EMAL IS ALREADY IN OUR SYSTEM."
 *		If the email is invalid:
 *			"INVAID EMAIL."
 *		If the password is invalid:
 *			"INVAID PASSWORD."
 *		If the update was unsuccessful:
 *			"FAILED TO UPDATE ACCOUNT, PLEASE TRY AGAIN."
 *		If the insert was successful:
 *			calls and returns attemptLogin($args[0], $db)
 */

function updateUser($args, $db){
	
	// Check args
	if(count($args) != 4){ return "INVALID ARGS."; }
	
	// Check email
	if(filter_var($args[1], FILTER_VALIDATE_EMAIL) === false){ return "INVALID EMAIL."; }
	
	// Check password
	if(strlen($args[2]) <= 4){ return "INVALID PASSWORD."; }
	
	// Update query
	$updateQuery = $db->prepare("UPDATE `users`
						   SET `email` = :email, `password` = :password
						   WHERE `uid` = :uid");
    $updateParams = array(
		':uid' 		=> $args[0],
		':email' 	=> $args[1],
        ':password' => password_hash($args[2], PASSWORD_DEFAULT),
		':address'  => $args[3]
    );
    
	// Check update success
    if($updateQuery->execute($updateParams)){ return "SUCCESSFULLY UPDATED!"; }
	
	return "FAILED TO UPDATE ACCOUNT, PLEASE TRY AGAIN.";
}//end updateUser




/*
 * Delete User
 *
 * 	Params:
 *		$args[0] - int: uid
 *		$args[1] - int: password
 *
 *	Returns:
 *		If 2 value are not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is negative:
 *			"INVALID USER."
 *		If the uid isn't in the users table:
 *			"USER DOES NOT EXIST."
 *		If the password is incorrect:
 *			"INCORRECT PASSWORD."
 *		If the delete was unsuccessful:
 *			"FAILED TO DELETE USER, PLEASE TRY AGAIN"
 *		If the delete was successful:
 *			"SUCCESSFULLY DELETED!";
 */

function deleteUser($args, $db){
	
	// Check args
	if(count($args) != 2){ return "INVALID ARGS."; }
	
	//check size
	if($args[0] < 0){ return "INVALID USER."; }
	
	// Select query
	$selectQuery = $db->prepare("SELECT `password`
						   FROM `users`
						   WHERE `uid` = :uid");
    $selectParams = array(
        ':uid' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetch();

	// Check if user credentials
	if(!empty($selectResults)){	
		// Checks a password hashed using password_hash
		if(!password_verify( $args[1], $selectResults['password'])){
			return "INCORRECT PASSWORD.";	
		}
	}else{
		return "USER DOES NOT EXIST.";
	}
	
	// Delete query - TODO: MySQL set up to cascade deletes to food andscores table
	$deleteQuery = $db->prepare("DELETE FROM `users`
						   WHERE `uid` = :uid");
    $deleteParams = array(':uid' => $args[0]);
    $deleteQuery->execute($deleteParams);
    $deleteResults = $deleteQuery->fetchAll();

	var_dump($deleteResults);
	// Check if UID exists in users table
	if($deleteQuery->rowCount() < 1){	
		return "FAILED TO DELETE USER, PLEASE TRY AGAIN."; }
	
	return "SUCCESSFULLY DELETED!";
}//end deleteUser




/*
 * Food functions
 */




/*
 * Insert Food
 *
 * 	Params:
 *		$args[0] - int: uid of creator
 *		$args[1] - String: 	Name of food
 *		$args[2] - int:  	Calories
 *		$args[3] - int:  	Carbs
 *		$args[4] - int:  	Fat 
 *		$args[5] - int:  	Protein
 *		$_FILES['photo'] -  photo
 *		
 *	Returns:
 *		If 7 values are not set in $args:
 *			"INVALID ARGS."
 *		If the uid isn't in the users table:
 *			"INVALID USER."
 *		If the insert was unsuccessful:
 *			"FAILED TO ADD FOOD, PLEASE TRY AGAIN"
 *		If the insert was successful:
 *			"SUCCESSFULLY ADDED!";
 */

function insertFood($args, $db){
	
	// Check args
	if(count($args) != 7){ return "INVALID ARGS."; }
	
	// Select query
	$selectQuery = $db->prepare("SELECT `uid`
						   FROM `users`
						   WHERE `uid` = :uid");
    $selectParams = array(
        ':uid' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){ return "IVALID USER."; }
	
	$newFileName;
		
	// Check if photo is uploaded
	if (!isset($_FILES['photo']) || empty($_FILES['photo'])){ 
		$newFileName = "";
	}else{
		
		$photo = $_FILES['photo'];
		$ext = "";
		// Check filename, must be .jpg, .jpeg, or .png
		if (preg_match('/^[a-zA-Z0-9\-_]+\.jpe?g$/i', $photo['name'])){
			$ext = ".jpg";
		}else if(preg_match('/^[a-zA-Z0-9\-_]+\.png$/i', $photo['name'])){
			$ext = ".png";
		}else{
			return "INVALID FILE NAME.";	
		}
	
		// Move file to uploads directory, named uploads/UID-SAFEFOODNAME-RANDM{.jpg,.png}
		$safeFoodName = preg_replace("/[^A-Za-z0-9]/", '', $args[1]);
		$newFileName = $args[0] . "-$safeFoodName-" . substr(sha1(rand()), 0, 5) . $ext;
		move_uploaded_file($photo['tmp_name'], "uploads/$newFileName");
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
		':image' 	=> $newFileName
    );

	// Check insert success
    if($insertQuery->execute($params)){ return "SUCCESSFULLY ADDED!"; }
    
	return "FAILED TO ADD FOOD, PLEASE TRY AGAIN."; 
}//end insertFood



 /*
 * Select Food By User
 *
 * 	Params:
 *		$args[0] - int: uid of creator
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD FOOD, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user's food objects
 */

function selectFoodByUser($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
	// Select query
	$selectQuery = $db->prepare("SELECT *
						   FROM `food`
						   WHERE `uid` = :uid");
    $selectParams = array(
        ':uid' => $args[0]
    );
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD FOOD, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectFoodByUser




 /*
 * Select Food For Game
 *
 *	Returns a somewhat random selection from the food table.
 *
 * 	Params:
 *		$args[0] - the number of food items to return
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD FOOD, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user's food objects
 */
 
function selectFoodForGame($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
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
	
	$sql = "SELECT `fid`, `uid`, `name`, `calories`, `carbs`, `fat`, `protein` 
							FROM `food` 
							WHERE `flags` < 3 
							ORDER BY $order 
							LIMIT $start, $size";
	
	// Select query 
	$selectQuery = $db->prepare($sql);
    
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD FOOD, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectFoodForGame




/*
 * Update Food
 *
 * 	Params:
 *		$args[0] - int: fid
  *		$args[1] - String: name;
 *		$args[2] - int: calories;
 *		$args[3] - int: carbs;
 *		$args[4] - int: fat;
 *		$args[5] - int: protein;
 *
 *	Returns:
 *		If 6 values are not set in $args:
 *			"INVALID ARGS."
 *		If the fid is invalid:
 *			"INVAID FID."
 *		If the update was unsuccessful:
 *			"FAILED TO UPDATE FOOD, PLEASE TRY AGAIN."
 *		If the insert was successful:
 *			calls and returns attemptLogin($args[0], $db)
 */


function updateFood($args, $db){
	
	// Check args
	if(count($args) != 6){ return "INVALID ARGS."; }
	
	// Check fid
	if(strlen($args[0]) < 0){ return "INVALID FID."; }
	
	// Update query
	$updateQuery = $db->prepare("UPDATE `food`
						   SET `name` = :name. `calories` = :calories, `carbs` = :carbs, `fat` = :fat, `protein` = :protein
						   WHERE `fid` = :fid");
    $updateParams = array(
		':fid'	 	=> $args[0],
		':name'		=> $args[1],
		':calories' => $args[2],
		':carbs' 	=> $args[3],
        ':fat' 		=> $args[4],
		':protein' 	=> $args[5],
    );
    
	
	// Check update success
    if($updateQuery->execute($updateParams)){ return "SUCCESSFULLY UPDATED!"; }
	
	return "FAILED TO UPDATE FOOD, PLEASE TRY AGAIN.";
}//end updateFood




/*
 * Flag Food
 *
 * 	Params:
 *		$args[0] - int: fid
 *		//TODO: blob
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If the fid is invalid:
 *			"INVAID FID."
 *		If the update was unsuccessful:
 *			"FAILED TO UPDATE FOOD, PLEASE TRY AGAIN."
 *		If the insert was successful:
 *			calls and returns attemptLogin($args[0], $db)
 */


function flagFood($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
	// Check fid
	if(strlen($args[0]) < 0){ return "INVALID FID."; }
	
	// Update query
	$updateQuery = $db->prepare("UPDATE `food`
						   SET `flags` = `flags` + 1
						   WHERE `fid` = :fid");
    $updateParams = array(
		':fid'	 	=> $args[0]
    );
    
	// Check update success
    if($updateQuery->execute($updateParams)){ return "SUCCESSFULLY FLAGGED!"; }
	
	return "FAILED TO FLAG FOOD, PLEASE TRY AGAIN.";
}//end flagFood




/*
 * Delete Food
 *
 * 	Params:
 *		$args[0] - int: fid
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is negative:
 *			"INVALID FOOD."
 *		If the delete was unsuccessful:
 *			"FAILED TO DELETE FOOD, PLEASE TRY AGAIN"
 *		If the delete was successful:
 *			"SUCCESSFULLY DELETED!";
 */

function deleteFood($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
	//check size
	if($args[0] < 0){ return "INVALID FOOD."; }
	
	// Delete query
	$deleteQuery = $db->prepare("DELETE FROM `food`
						   WHERE `fid` = :fid");
    $deleteParams = array(':fid' => $args[0]);
   
	if($deleteQuery->execute($deleteParams)){	
		return "SUCCESSFULLY DELETED!"; }
	
	return "FAILED TO DELETE FOOD, PLEASE TRY AGAIN.";
}//end deleteFood



/*
 * Score functions
 */



/*
 * Insert Score
 *
 * 	Params:
 *		$args[0] - int:		uid of the player
 *		$args[1] - int: 	score
 *		$args[2] - double:	latitude
 *		$args[3] - double:	longitude
 *	Returns:
 *		If 4 values are not set in $args:
 *			"INVALID ARGS."
 *		If the score is negative:
 *			"INVAID SCORE."
 *		If the insert was unsuccessful:
 *			"FAILED TO ADD SCORE, PLEASE TRY AGAIN."
 *		If the insert was successful:
 *			"SUCCESSFULLY ADDED!"
 */
 
 function insertScore($args, $db){
	
	// Check args
	if(count($args) != 4){ return "INVALID ARGS."; }

	// Check uid
	if($args[0] < 0){ return "IVALID UID."; }

	// Check score
	if($args[1] < 0){ return "IVALID SCORE."; }
	
	// Insert Quuery
	$insertQuery = $db->prepare("INSERT INTO `scores` (`uid`, `score`, `latitude`, `longitude`)
						   VALUES (:uid, :score, :latitude, :longitude)");
    $params = array(
        ':uid' 		 => $args[0],
		':score' 	 => $args[1],
		':latitude'  => $args[2],
		':longitude' => $args[4]
    );
	
	// Check insert success
    if($insertQuery->execute($params)){ return "SUCCESSFULLY ADDED!"; }
	
	return "FAILED TO ADD SCORE, PLEASE TRY AGAIN.";
}//end insertScore




 /*
 * Select Scores
 *
 * 	Params:
 *		$args[0] - int: number of scores
 *
 *	Returns:
 *		If 1 value is not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is nonpositive:
 *			"INVALID SIZE."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD SCORES, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of score objects
 */

function selectScores($args, $db){
	
	// Check args
	if(count($args) != 1){ return "INVALID ARGS."; }
	
	//check size
	if($args[0] < 1){ return "INVALID SIZE."; }
	
	$size = $args[0];
	
	// Select query
	$selectQuery = $db->prepare("SELECT *
						   FROM `scores`
						   ORDER BY `score` DESC
						   LIMIT $size");
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){	
		return "FAILED TO LOAD SCORES, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectScore


 /*
 * Select Scores By Distance
 *
 * 	Params:
 *		$args[0] - int: 	number of scores
 *		$args[1] - double:  latitude
 *		$args[2] - double:  longitude
 *		$args[3] - double:	radius
 *
 *	Returns:
 *		If 4 values are not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is nonpositive:
 *			"INVALID SIZE."
 *		If args[3] is nonpositive:
 *			"INVALID RADIUS."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD SCORES, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of score objects
 */

function selectScoresByDistance($args, $db){
	
	// Check args
	if(count($args) != 4){ return "INVALID ARGS."; }
	
	// Check size
	if($args[0] < 1){ return "INVALID SIZE."; }
	
	// Check radius
	if($args[3] <= 0){ return "INVALID RADIUS."; }
	
	$size 		= $args[0];
	$latitude 	= $args[1];
	$longitude 	= $args[2];
	$radius 	= $args[3];
	
	// Distance Column - calculates distance based on latitude and longitude
	// 3959 = scalar that converts the distance to miles
	$distCol = "3959 * acos( "
			 . "cos(radians($latitude)) * "
			 . "cos(radians(`latitude`)) * "
			 . "cos(radians(`longitude`) "
			 . "- radians($longitude)) + "
			 . "sin(radians($latitude)) * "
			 . "sin(radians(`latitude`)))";
	
	// Select query
	$selectQuery = $db->prepare("SELECT *, ($distCol) as `distance` 
						   FROM `scores` 
						   HAVING `distance` < $radius 
						   ORDER BY `score` DESC, `distance` ASC 
						   LIMIT $size");
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){ return "FAILED TO LOAD SCORES, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectScoreByDistance


 /*
 * Select Scores By User
 *
 * 	Params:
 *		$args[0] - int: uid
 *		$args[1] - int: number of scores
 *
 *	Returns:
 *		If 2 value are not set in $args:
 *			"INVALID ARGS."
 *		If args[0] is negative:
 *			"INVALID USER."
 *		If args[1] is nonpositive:
 *			"INVALID SIZE."
 *		If the select was unsuccessful:
 *			"FAILED TO LOAD SCORES, PLEASE TRY AGAIN"
 *		If the select was successful:
 *			returns json of the user's score objects
 */

function selectScoresByUser($args, $db){
	
	// Check args
	if(count($args) != 2){ return "INVALID ARGS."; }
	
	//check uid
	if($args[0] < 0){ return "INVALID USER."; }
	
	//check size
	if($args[1] < 1){ return "INVALID SIZE."; }
	
	$size = $args[1];
	
	// Select query
	$selectQuery = $db->prepare("SELECT *
						   FROM `scores`
						   WHERE `uid` = :uid
						   ORDER BY `score` DESC
						   LIMIT $size");
    $selectParams = array(':uid' => $args[0]);
    $selectQuery->execute($selectParams);
    $selectResults = $selectQuery->fetchAll();

	// Check if UID exists in users table
	if(empty($selectResults)){ return "FAILED TO LOAD SCORES, PLEASE TRY AGAIN."; }
	
	return json_encode($selectResults);
}//end selectFoodByUser


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

if(empty($_POST) || empty($_POST['o']) || empty($_POST['a'])){
	die("NO DATA.");	
}

//TODO: Auth here

// run the specified function
$operation = $_POST['o'];
$args = $_POST['a'];
echo($operation($args, $db));

// close DB connection
?>