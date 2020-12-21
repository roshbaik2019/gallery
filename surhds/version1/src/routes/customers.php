<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

$app = new \Slim\App;

//Get All Users

$app->get('/api/allusers',function(Request $request, Response $response ){
    $sql = "SELECT * FROM users ORDER BY id DESC";
    try{
        //Get DB object
        $db = new db();
        // connect
        $db = $db->connect();

        $stmt = $db->query($sql);
        $customers = $stmt->fetchAll(PDO::FETCH_OBJ);
        // convert to json
        echo json_encode($customers);
        $db = null;



    } catch (PDOException $e) {
        print "Error!: " . $e->getMessage() . "<br/>";
        die();
    }

});


//Get data for single user

$app->get('/api/user/{id}',function(Request $request, Response $response ){
    $id = $request->getAttribute('id');

    $sql = "SELECT * FROM users WHERE id = $id";
    try{
        //Get DB object
        $db = new db();
        // connect
        $db = $db->connect();

        $stmt = $db->query($sql);
        $customer = $stmt->fetchAll(PDO::FETCH_OBJ);
        // convert to json
        echo json_encode($customer);
        $db = null;



    } catch (PDOException $e) {
        print "Error!: " . $e->getMessage() . "<br/>";
        die();
    }

});


//Add New user to db (Registration)

$app->post('/api/user/add',function(Request $request, Response $response ){
    $userName = $request->getParam('username');
    $email = $request->getParam('email');
    $sql = "INSERT INTO users (username,email) VALUES (:username,:email)";
    try{
        //Get DB object
        $db = new db();
        // connect
        $db = $db->connect();

        $stmt = $db->prepare($sql);
        $stmt->bindParam(':username',$userName);
        $stmt->bindParam(':email',$email);
        $stmt->execute();
        echo'{"notice": {"text": "Register successfully"}';

    } catch (PDOException $e) {
        print "Error!: " . $e->getMessage() . "<br/>";
        die();
    }

});


//Update user profile

$app->get('/api/user/update/{id}',function(Request $request, Response $response ){
    $id = $request->getAttribute('id');

    $sql = "UPDATE users SET 
              viewcount = viewcount+1 WHERE id = $id";
    try{
        //Get DB object
        $db = new db();
        // connect
        $db = $db->connect();

        $stmt = $db->prepare($sql);
        //$stmt->bindParam(':viewcount',$viewcount);
        $stmt->execute();
        echo'{"notice": {"text": "user Updated"}';

    } catch (PDOException $e) {
        print "Error!: " . $e->getMessage() . "<br/>";
        die();
    }

});


//delete Customers

$app->delete('/api/customer/delete/{id}',function(Request $request, Response $response ){
    $id = $request->getAttribute('id');

    $sql = "DELETE FROM images WHERE id = $id";
    try{
        //Get DB object
        $db = new db();
        // connect
        $db = $db->connect();

        $stmt = $db->prepare($sql);
        $stmt->execute();
        $db = null;
        echo'{"notice": {"text": "Customer Deleted"}';


    } catch (PDOException $e) {
        print "Error!: " . $e->getMessage() . "<br/>";
        die();
    }

});




